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

package org.jvnet.testing.hk2mockito;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.lang.reflect.Type;
import javax.inject.Inject;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.Rank;
import org.glassfish.hk2.api.ServiceHandle;
import org.jvnet.hk2.annotations.Service;
import org.jvnet.testing.hk2mockito.internal.MockitoService;
import org.jvnet.testing.hk2mockito.internal.cache.ParentCache;

/**
 * This class is a custom resolver that creates or finds services and wraps in a
 * spy.
 *
 * @author Sharmarke Aden
 */
@Rank(Integer.MAX_VALUE)
@Service
public class HK2MockitoInjectionResolver implements InjectionResolver<Inject> {

    private final MockitoService mockitoService;
    private final ParentCache parentCache;

    @Inject
    HK2MockitoInjectionResolver(MockitoService mockitoService, ParentCache parentCache) {
        this.mockitoService = mockitoService;
        this.parentCache = parentCache;
    }

    @Override
    public Object resolve(Injectee injectee, ServiceHandle<?> root) {
        AnnotatedElement parent = injectee.getParent();
        Member member = (Member) parent;
        Type requiredType = injectee.getRequiredType();
        Type parentType = member.getDeclaringClass();

        SUT sut = parent.getAnnotation(SUT.class);
        SC sc = parent.getAnnotation(SC.class);
        MC mc = parent.getAnnotation(MC.class);

        Object service;

        parentCache.put(requiredType, parentType);

        if (sut != null) {
            service = mockitoService.findOrCreateSUT(sut, injectee, root);
        } else if (sc != null) {
            service = mockitoService.findOrCreateCollaborator(sc.value(), sc.field(), injectee, root);
        } else if (mc != null) {
            service = mockitoService.findOrCreateCollaborator(mc.value(), mc.field(), injectee, root);
        } else {
            service = mockitoService.createOrFindService(injectee, root);
        }

        return service;
    }

    @Override
    public boolean isConstructorParameterIndicator() {
        return false;
    }

    @Override
    public boolean isMethodParameterIndicator() {
        return false;
    }

}
