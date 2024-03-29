/*
 * Copyright (c) 2015, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package org.jvnet.hk2.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReentrantLock;

import jakarta.inject.Inject;

import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.InjectionPointIndicator;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.utilities.general.Hk2ThreadLocal;
import org.glassfish.hk2.utilities.reflection.Pretty;
import org.jvnet.hk2.annotations.Service;

/**
 * These utilities are per service locator.  Another service locator may have a different view
 * of the caches stored here
 * 
 * @author jwells
 *
 */
public class PerLocatorUtilities {
    private final ReentrantLock lock = new ReentrantLock();
    /** Must not be static, otherwise it can leak when using thread pools */
    private final Hk2ThreadLocal<WeakHashMap<Class<?>, String>> threadLocalAutoAnalyzerNameCache =
            new Hk2ThreadLocal<WeakHashMap<Class<?>, String>>() {
                @Override
                protected WeakHashMap<Class<?>, String> initialValue() {
                    return new WeakHashMap<Class<?>, String>();
                }
            };

    /** Must not be static, otherwise it can leak when using thread pools */
    private final Hk2ThreadLocal<WeakHashMap<AnnotatedElement, SoftAnnotatedElementAnnotationInfo>>
        threadLocalAnnotationCache =
            new Hk2ThreadLocal<WeakHashMap<AnnotatedElement, SoftAnnotatedElementAnnotationInfo>>() {
                @Override
                protected WeakHashMap<AnnotatedElement, SoftAnnotatedElementAnnotationInfo> initialValue() {
                    return new WeakHashMap<AnnotatedElement, SoftAnnotatedElementAnnotationInfo>();
                }
            };
            
    private final Hk2ThreadLocal<WeakHashMap<AnnotatedElement, Boolean>> hasInjectCache =
            new Hk2ThreadLocal<WeakHashMap<AnnotatedElement, Boolean>>() {
                @Override
                protected WeakHashMap<AnnotatedElement, Boolean> initialValue() {
                    return new WeakHashMap<AnnotatedElement, Boolean>();
                }
            };
            
    private volatile ProxyUtilities proxyUtilities;
    private final ServiceLocatorImpl parent;
    
    /* package */ PerLocatorUtilities(ServiceLocatorImpl parent) {
        this.parent = parent;
    }
    
    /**
     * Checks whether an annotated element has any annotation that was used for the injection
     *
     * @param locator The service locator to use (as it will get all
     * the annotations that were added on as well as the normal Inject)
     * @param annotated  the annotated element
     * @param checkParams  check the params if true
     * @return True if element contains at least one inject annotation
     */
    /* package */ boolean hasInjectAnnotation(AnnotatedElement annotated) {
        WeakHashMap<AnnotatedElement, Boolean> cache = hasInjectCache.get();
        Boolean rv = cache.get(annotated);
        if (rv != null) return rv;

        for (Annotation anno : annotated.getAnnotations()) {
            if (anno.annotationType().getAnnotation(InjectionPointIndicator.class) != null) {
                cache.put(annotated, true);
                return true;
            }
            
            if (parent.isInjectAnnotation(anno)) {
                cache.put(annotated, true);
                return true;
            }
        }

        boolean isConstructor;
        Annotation allAnnotations[][];
        if (annotated instanceof Method) {
            Method m = (Method) annotated;

            isConstructor = false;
            allAnnotations = m.getParameterAnnotations();
        } else if (annotated instanceof Constructor) {
            Constructor<?> c = (Constructor<?>) annotated;

            isConstructor = true;
            allAnnotations = c.getParameterAnnotations();
        } else {
            cache.put(annotated, false);
            return false;
        }

        for (Annotation allParamAnnotations[] : allAnnotations) {
            for (Annotation paramAnno : allParamAnnotations) {
                if (paramAnno.annotationType().getAnnotation(InjectionPointIndicator.class) != null) {
                    cache.put(annotated, true);
                    return true;
                }
                
                if (parent.isInjectAnnotation(paramAnno, isConstructor)) {
                    cache.put(annotated, true);
                    return true;
                }
            }
        }

        cache.put(annotated, false);
        return false;
    }
            
    /**
     * Gets the analyzer name from the Service annotation
     *
     * @param c The class to get the analyzer name from
     * @return The name of the analyzer (null for default)
     */
    public String getAutoAnalyzerName(Class<?> c) {
        String retVal = threadLocalAutoAnalyzerNameCache.get().get(c);
        if (retVal != null) return retVal;
                    
        Service s = c.getAnnotation(Service.class);
        if (s == null) return null;
                    
        retVal = s.analyzer();
        threadLocalAutoAnalyzerNameCache.get().put(c, retVal);

        return retVal;
    }
    
    /**
     * Returns an injection resolver for the injectee
     *
     * @param locator The locator to use when finding the resolver
     * @param injectee Injectee from which the annotation should be extracted
     * @return Injection resolver used to resolve the injection for the injectee
     * @throws IllegalStateException If we could not find a valid resolver
     */
    public InjectionResolver<?> getInjectionResolver(
            ServiceLocatorImpl locator, Injectee injectee) throws IllegalStateException {
        return getInjectionResolver(locator, injectee.getParent(), injectee.getPosition());
    }
    
    /**
     * Returns an injection resolver for this AnnotatedElement. The method cannot be used for constructors
     * or methods.
     *
     * @param locator The locator to use when finding the resolver
     * @param annotatedGuy The annotated class or producer method
     * @return The scope of this class or producer method.  If no scope is
     * found will return the dependent scope
     * @throws IllegalStateException If we could not find a valid resolver
     */
    /* package */ InjectionResolver<?> getInjectionResolver(
            ServiceLocatorImpl locator, AnnotatedElement annotatedGuy) throws IllegalStateException {
        if (annotatedGuy instanceof Method || annotatedGuy instanceof Constructor<?>) {
            throw new IllegalArgumentException("Annotated element '" + annotatedGuy + "' can be neither a Method nor a Constructor.");
        }
        return getInjectionResolver(locator, annotatedGuy, -1);
    }
    
    private InjectionResolver<?> getInjectionResolver(
            ServiceLocatorImpl locator, AnnotatedElement annotatedGuy, int position) throws IllegalStateException {
        boolean methodOrConstructor = annotatedGuy instanceof Method || annotatedGuy instanceof Constructor<?>;
        Annotation injectAnnotation = getInjectAnnotation(locator, annotatedGuy, methodOrConstructor, position);

        //Annotation injectAnnotation = getInjectAnnotation(locator, annotatedGuy, position);

        Class<? extends Annotation> injectType = (injectAnnotation == null) ?
                Inject.class : injectAnnotation.annotationType();

        InjectionResolver<?> retVal = locator.getInjectionResolver(injectType);
        if (retVal == null) {
            // Not possible to get here, we only are here if we already found a resolver
            throw new IllegalStateException("There is no installed injection resolver for " +
                    Pretty.clazz(injectType) + " for type " + annotatedGuy);
        }

        return retVal;
    }
    
    /**
     * Gets the annotation that was used for the injection
     *
     * @param locator The service locator to use (as it will get all
     * the annotations that were added on as well as the normal Inject)
     * @param annotated the annotated annotated
     * @param checkParams  check the params if true
     * @param position index of constructor or method parameter which which will be checked
     *                 for inject annotations. The {@code position} parameter is only used when
     *                 {@code annotated} is method or constructor otherwise the value will be ignored.
     * @return The annotation that is the inject annotation, or null
     * if no inject annotation was found
     */
    private Annotation getInjectAnnotation(final ServiceLocatorImpl locator, final AnnotatedElement annotated,
            final boolean checkParams, final int position) {
        
        final AnnotatedElementAnnotationInfo annotationInfo = computeElementAnnotationInfo(annotated);

        if (checkParams) {

            if (annotationInfo.hasParams) {
                for (Annotation paramAnno : annotationInfo.paramAnnotations[position]) {
                    if (locator.isInjectAnnotation(paramAnno, annotationInfo.isConstructor)) {
                        return paramAnno;
                    }
                }
            }
        }

        for (Annotation annotation : annotationInfo.elementAnnotations) {
            if (locator.isInjectAnnotation(annotation)) {
                return annotation;
            }
        }

        return null;
    }
    
    private AnnotatedElementAnnotationInfo computeElementAnnotationInfo(AnnotatedElement ae) {
        AnnotatedElementAnnotationInfo hard;
        SoftAnnotatedElementAnnotationInfo soft = threadLocalAnnotationCache.get().get(ae);
        if (soft != null) {
            hard = soft.harden(ae);
        }
        else {
            hard = Utilities.computeAEAI(ae);
            soft = hard.soften();
            threadLocalAnnotationCache.get().put(ae, soft);
        }
        return hard;
    }
    
    public void releaseCaches() {
        lock.lock();
        try {
            hasInjectCache.removeAll();
            if (proxyUtilities != null) {
                proxyUtilities.releaseCache();
            }
        } finally {
            lock.unlock();
        }
    }
    
    public void shutdown() {
        releaseCaches();
        
        threadLocalAutoAnalyzerNameCache.removeAll();
        threadLocalAnnotationCache.removeAll();
    }
    
    public ProxyUtilities getProxyUtilities() {
        if (proxyUtilities != null) return proxyUtilities;
        
        lock.lock();
        try {
            if (proxyUtilities != null) return proxyUtilities;
            
            proxyUtilities = new ProxyUtilities();
            
            return proxyUtilities;
        } finally {
            lock.unlock();
        }
    }
}
