/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.extras.interception.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashSet;

import org.glassfish.hk2.extras.interception.InterceptionBinder;

/**
 * @author jwells
 *
 */
public class ReflectionUtilities {
    public static  HashSet<String> getAllBindingsFromMethod(Method m) {
        HashSet<String> retVal = getAllBindingsFromClass(m.getDeclaringClass());
        
        Annotation allMethodAnnotations[] = m.getAnnotations();
        for (Annotation aMethodAnnotation : allMethodAnnotations) {
            if (!isBindingAnnotation(aMethodAnnotation)) continue;
            
            getAllBinderAnnotations(aMethodAnnotation, retVal);
        }
        
        return retVal;
    }
    
    public static  HashSet<String> getAllBindingsFromConstructor(Constructor<?> m) {
        HashSet<String> retVal = getAllBindingsFromClass(m.getDeclaringClass());
        
        Annotation allMethodAnnotations[] = m.getAnnotations();
        for (Annotation aMethodAnnotation : allMethodAnnotations) {
            if (!isBindingAnnotation(aMethodAnnotation)) continue;
            
            getAllBinderAnnotations(aMethodAnnotation, retVal);
        }
        
        return retVal;
    }
    
    public static  HashSet<String> getAllBindingsFromClass(Class<?> c) {
        HashSet<String> retVal = new HashSet<String>();
        
        Annotation allClassAnnotations[] = c.getAnnotations();
        for (Annotation aClassAnnotation : allClassAnnotations) {
            if (!isBindingAnnotation(aClassAnnotation)) continue;
            
            getAllBinderAnnotations(aClassAnnotation, retVal);
        }
        
        return retVal;
    }
    
    private static boolean isBindingAnnotation(Annotation a) {
        return (a.annotationType().getAnnotation(InterceptionBinder.class)) != null;
    }
    
    private static void getAllBinderAnnotations(Annotation a, HashSet<String> retVal) {
        String aName = a.annotationType().getName();
        if (retVal.contains(aName)) return;
        retVal.add(aName);
        
        Annotation subAnnotations[] = a.annotationType().getAnnotations();
        for (Annotation subAnnotation : subAnnotations) {
            if (!isBindingAnnotation(subAnnotation)) continue;
            
            String subName = subAnnotation.annotationType().getName();
            if (retVal.contains(subName)) continue;
            
            getAllBinderAnnotations(subAnnotation, retVal);
        }
    }

}
