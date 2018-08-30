/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.tests.locator.arrays;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.FactoryDescriptors;
import org.glassfish.hk2.api.PerLookup;
import org.glassfish.hk2.tests.locator.utilities.TestModule;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.jvnet.hk2.internal.Utilities;

/**
 * @author jwells
 *
 */
public class ArraysModule implements TestModule {

    /* (non-Javadoc)
     * @see org.glassfish.hk2.tests.locator.utilities.TestModule#configure(org.glassfish.hk2.api.Configuration)
     */
    @Override
    public void configure(DynamicConfiguration config) {
        config.bind(BuilderHelper.link(ArrayOfIntFactory.class).
                to(int[].class).
                in(PerLookup.class.getName()).
                buildFactory(Singleton.class.getName()));
        
        config.bind(BuilderHelper.link(ArrayOfListFactory.class).
                to(List[].class).
                in(PerLookup.class.getName()).
                buildFactory(Singleton.class.getName()));
        
        config.bind(BuilderHelper.link(ArrayOfMapFactory.class).
                to(Map[].class).
                in(PerLookup.class.getName()).
                buildFactory(Singleton.class.getName()));
        
        config.bind(BuilderHelper.link(ArrayOfSimpleServiceFactory.class).
                to(SimpleService[].class).
                in(PerLookup.class.getName()).
                buildFactory(Singleton.class.getName()));
        
        config.addActiveDescriptor(ArrayInjectee.class);

    }

}
