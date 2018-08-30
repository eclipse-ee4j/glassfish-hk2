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

package org.jvnet.testing.hk2mockito.internal.cache;

import java.lang.reflect.Type;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Provider;
import org.jvnet.hk2.annotations.Service;
import org.jvnet.testing.hk2mockito.internal.HK2Mockito;
import org.jvnet.testing.hk2mockito.internal.MockitoCacheKey;

/**
 * A cache service for tracking spy injectees and spy services.
 *
 * @author Sharmarke Aden
 */
@Service
public class MemberCache {

    private final Map<Type, Map<MockitoCacheKey, Object>> cache;
    private final Provider<Map> cacheProvider;

    @Inject
    MemberCache(@HK2Mockito Map cache, @HK2Mockito Provider<Map> cacheProvider) {
        this.cache = cache;
        this.cacheProvider = cacheProvider;
    }

    public Map<MockitoCacheKey, Object> get(Type type) {
        return cache.get(type);
    }

    public Map<MockitoCacheKey, Object> add(Type type) {
        Map typeCache = cacheProvider.get();
        cache.put(type, typeCache);

        return typeCache;
    }

}
