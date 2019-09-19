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

package org.jvnet.testing.hk2mockito.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Type;
import static java.security.AccessController.doPrivileged;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.InjectionResolver;
import static org.glassfish.hk2.api.InjectionResolver.SYSTEM_RESOLVER_NAME;
import org.glassfish.hk2.api.IterableProvider;
import org.glassfish.hk2.api.ServiceHandle;
import org.jvnet.hk2.annotations.Service;
import org.jvnet.hk2.internal.SystemInjecteeImpl;
import static org.jvnet.hk2.internal.Utilities.getFieldInjectees;
import org.jvnet.testing.hk2mockito.HK2MockitoInjectionResolver;
import org.jvnet.testing.hk2mockito.MC;
import org.jvnet.testing.hk2mockito.SC;
import org.jvnet.testing.hk2mockito.SUT;
import org.jvnet.testing.hk2mockito.internal.cache.MemberCache;
import org.jvnet.testing.hk2mockito.internal.cache.ParentCache;
import org.mockito.MockSettings;
import static org.mockito.Mockito.withSettings;

/**
 *
 * A helper service for creating SUT, SC, MC or regular service.
 *
 * @author Sharmarke Aden
 */
@Service
public class MockitoService {

    private final MemberCache memberCache;
    private final ParentCache parentCache;
    private final ObjectFactory objectFactory;
    private final IterableProvider<InjectionResolver> resolvers;
    private final InjectionResolver<Inject> systemResolver;

    @Inject
    MockitoService(MemberCache memberCache,
            ParentCache parentCache,
            ObjectFactory objectFactory,
            IterableProvider<InjectionResolver> resolvers,
            @Named(SYSTEM_RESOLVER_NAME) InjectionResolver<Inject> systemResolver) {
        this.memberCache = memberCache;
        this.parentCache = parentCache;
        this.objectFactory = objectFactory;
        this.resolvers = resolvers;
        this.systemResolver = systemResolver;

    }

    /**
     * Given an injectee find the service by iterating through all the resolvers
     * in the system to resolve it. This is necessary to resolve services that
     * are resolved via injection resolvers (assisted injection). Note that this
     * method returns the first resolved service.
     *
     * @param injectee The injection point this value is being injected into
     * @param root The service handle of the root class being created, which
     * should be used in order to ensure proper destruction of associated
     * &64;PerLookup scoped objects. This can be null in the case that this is
     * being used for an object not managed by HK2. This will only happen if
     * this object is being created with the create method of ServiceLocator.
     * @return A possibly null value to be injected into the given injection
     * point
     */
    private Object resolve(Injectee injectee, ServiceHandle<?> root) {
        Member member = (Member) injectee.getParent();
        Class<?> parentType = member.getDeclaringClass();

        //if the injectee an instance of InjectionResolver delegate it to the 
        //system resolver
        if (InjectionResolver.class.isAssignableFrom(parentType)) {
            return systemResolver.resolve(injectee, root);
        }

        for (InjectionResolver<?> resolver : resolvers) {

            //ignore mockito injection resolver so we don't get into an infinite loop
            if (resolver instanceof HK2MockitoInjectionResolver) {
                continue;
            }

            Object service = resolver.resolve(injectee, root);

            //return the first resolved service
            if (service != null) {
                return service;
            }
        }

        return null;
    }

    /**
     * Given an SUT annotation and an injectee resolve the service associated
     * with the injectee and use the metadata in the SUT annotation to possibly
     * create a mockito spy.
     *
     * @param injectee The injection point this value is being injected into
     * @param root The service handle of the root class being created
     * @return the service or a proxy spy of the service
     */
    public Object findOrCreateSUT(Injectee injectee, ServiceHandle<?> root) {
        Member member = (Member) injectee.getParent();
        Type requiredType = injectee.getRequiredType();
        Type parentType = member.getDeclaringClass();

        Map<MockitoCacheKey, Object> cache = primeCache((Class) parentType, root);
        MockitoCacheKey key = objectFactory.newKey(requiredType, member.getName());

        return cache.get(key);
    }

    /**
     * Given an injectee create and cache or resolve the service associated with
     * it.
     *
     * @param injectee The injection point this value is being injected into
     * @param root The service handle of the root class being created
     * @return the service or a proxy spy or mock of the service
     */
    public Object createOrFindService(Injectee injectee, ServiceHandle<?> root) {
        Member member = (Member) injectee.getParent();
        Class<?> parentType = member.getDeclaringClass();
        Type requiredType = injectee.getRequiredType();

        //get the cache for the injectee's parent type. if one is not found that
        //means we are not dealing with a test class instance so return the 
        //resolved service.
        Type serviceParent = parentCache.get(parentType);

        if (serviceParent == null) {
            return resolve(injectee, root);
        }

        //look for the test parent, which is at the root of the ancestry.
        Type grandParent = parentCache.get(serviceParent);

        while (grandParent != null) {
            serviceParent = grandParent;
            grandParent = parentCache.get(serviceParent);
        }

        //get the service's parent (the test class) cache. if one is not found 
        //that means the test class didn't contain any injections that required
        //mocking/spying so we return the original service.
        Map<MockitoCacheKey, Object> cache = memberCache.get(serviceParent);

        if (cache == null) {
            return resolve(injectee, root);
        }

        // determine the cache key for the service
        MockitoCacheKey key;

        if (member instanceof Field) {
            key = objectFactory.newKey(requiredType, member.getName());
        } else {
            key = objectFactory.newKey(requiredType, injectee.getPosition());
        }

        //get the service from the cache.
        Object service = cache.get(key);

        //if the service is not found in the cache that means the test class
        //was not injected with services that required mocking or spying.
        //simply resolve these services since they werent created up front.
        if (service == null) {
            service = resolve(injectee, root);
        }

        return service;
    }

    /**
     * Given metadata about collaborator and an injectee create or resolve the
     * collaborating service.
     *
     * @param position method or constructor the parameter position metadata
     * @param fieldName field name metadata
     * @param injectee The injection point this value is being injected into
     * @param root The service handle of the root class being created
     * @return the service or a proxy spy or mock of the service
     */
    public Object findOrCreateCollaborator(int position,
            String fieldName,
            Injectee injectee,
            ServiceHandle<?> root) {
        Member member = (Member) injectee.getParent();
        Type parentType = member.getDeclaringClass();
        Type requiredType = injectee.getRequiredType();

        //prime the cache for the test class.
        Map<MockitoCacheKey, Object> cache = primeCache((Class) parentType, root);

        //get the service from the cache.
        MockitoCacheKey key;
        if (member instanceof Field) {
            key = objectFactory.newKey(requiredType, position);
        } else {
            key = objectFactory.newKey(requiredType, getFieldName(fieldName, member.getName()));
        }

        return cache.get(key);
    }

    /**
     * Given a class analyze its fields, create services, create mock/spy
     * proxies of found services, and them to the cache.
     *
     * @param type the class that will be analyzed
     * @param root The service handle of the root class being created
     * @return a map containing nothing, or services or proxy/spy object
     */
    private Map<MockitoCacheKey, Object> primeCache(final Class type, ServiceHandle<?> root) {
        //if a cache already exists for the given class simply return that cache
        Map<MockitoCacheKey, Object> cache = memberCache.get(type);

        if (cache != null) {
            return cache;
        }
        
        //add the type to the cache
        cache = memberCache.add(type);

        Field[] fields = doPrivileged((PrivilegedAction<Field[]>) type::getDeclaredFields);

        //iterate over all the fields in the class
        for (Field field : fields) {
            String name = field.getName();
            Class<?> fieldClass = field.getType();
            Type fieldType = field.getGenericType();

            SC sc = field.getAnnotation(SC.class);
            MC mc = field.getAnnotation(MC.class);

            if (sc != null) {
                //if we are dealing with spy collaborator then we create an injectee 
                //for it and resolve that injectee
                List<SystemInjecteeImpl> injectees = getFieldInjectees(type, field, null);

                Object service = resolve(injectees.get(0), root);

                if (service != null) {
                    //if we found the service then we create two entries for it
                    //in the cache. one for field injection and another for
                    //method injection

                    MockitoCacheKey executableKey = objectFactory.newKey(fieldType, sc.value());
                    MockitoCacheKey fieldKey = objectFactory.newKey(fieldType, getFieldName(sc.field(), name));

                    Object spy = objectFactory.newSpy(service);
                    cache.put(executableKey, spy);
                    cache.put(fieldKey, spy);
                }
            } else if (mc != null) {
                //if we are dealing with a mock collaborator then get all the
                //metadata associated with the mock and create a mock of the 
                //service and add it to the cache twice. one for field injection
                //and one for method injection.
                Class<?>[] interfaces = mc.extraInterfaces();
                String mockName = mc.name();

                if ("".equals(mockName)) {
                    mockName = name;
                }

                MockSettings settings = withSettings()
                        .name(mockName)
                        .defaultAnswer(mc.answer());

                if (interfaces.length > 0) {
                    settings.extraInterfaces(mc.extraInterfaces());
                }

                Object service = objectFactory.newMock(fieldClass, settings);

                MockitoCacheKey executableKey = objectFactory.newKey(fieldClass, mc.value());
                MockitoCacheKey fieldKey = objectFactory.newKey(fieldClass, getFieldName(mc.field(), name));

                cache.put(executableKey, service);
                cache.put(fieldKey, service);
            }
        }

        // Only do SUT after we've created any mock dependencies.
        for (Field field : fields) {
            String name = field.getName();
            Type fieldType = field.getGenericType();
            SUT sut = field.getAnnotation(SUT.class);

            if (sut != null) {
                List<SystemInjecteeImpl> injectees = getFieldInjectees(type, field, null);
                Object service = resolve(injectees.get(0), root);

                if (sut.value()) {
                    service = objectFactory.newSpy(service);
                }

                MockitoCacheKey fieldKey = objectFactory.newKey(fieldType, name);
                cache.put(fieldKey, service);
            }
        }

        return cache;
    }

    private String getFieldName(String fieldName, String defaultName) {
        if ("".equals(fieldName)) {
           return defaultName;
        }
        
        return fieldName;
    }

}
