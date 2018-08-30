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
import org.jvnet.hk2.annotations.Service;
import org.jvnet.testing.hk2mockito.internal.HK2Mockito;

/**
 * Cache service used to track parent child relationship between injectees and
 * their parent.
 *
 * @author Sharmarke Aden
 */
@Service
public class ParentCache {

    private final Map<Type, Type> cache;

    @Inject
    ParentCache(@HK2Mockito Map cache) {
        this.cache = cache;
    }

    public void put(Type child, Type parent) {
        cache.put(child, parent);
    }

    public Type get(Type child) {
        return cache.get(child);
    }

}
