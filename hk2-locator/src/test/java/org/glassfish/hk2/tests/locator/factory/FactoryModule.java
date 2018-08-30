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

package org.glassfish.hk2.tests.locator.factory;

import java.util.Date;

import javax.inject.Singleton;

import org.glassfish.hk2.api.DescriptorType;
import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.Context;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.tests.locator.utilities.TestModule;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.hk2.utilities.DescriptorImpl;

/**
 * @author jwells
 *
 */
public class FactoryModule implements TestModule {

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Module#configure(org.glassfish.hk2.api.Configuration)
     */
    @Override
    public void configure(DynamicConfiguration configurator) {
        configurator.addActiveDescriptor(ErrorServiceImpl.class);
        configurator.bind(BuilderHelper.link(DateFactory.class).to(Date.class).buildFactory());
        configurator.bind(BuilderHelper.link(DateInjectee.class).in(Singleton.class).build());
        
        configurator.bind(BuilderHelper.link(
                FruitFactory.class).
                to(Apple.class).
                in(FruitScope.class.getName()).
                buildFactory(Singleton.class.getName()));
        // Apple is not in the list, but its factory is
        
        // Also bind the custom scope
        configurator.bind(
                BuilderHelper.link(FruitContext.class).
                to(Context.class).
                in(Singleton.class.getName()).
                build());
        
        // Now for our named factories.  They produce the same type (President)
        // but they each do it with a different name
        
        // Washington
        configurator.bind(BuilderHelper.link(
                WashingtonFactory.class).
                to(President.class).
                in(Singleton.class.getName()).
                named(FactoryTest.WASHINGTON_NAME).
                buildFactory(Singleton.class));
        
        // Jefferson
        configurator.bind(BuilderHelper.link(
                JeffersonFactory.class).
                to(President.class).
                in(Singleton.class.getName()).
                named(FactoryTest.JEFFERSON_NAME).
                buildFactory());
        
        // In the following test the Factory is put in
        // *without* using the buildFactory method, in order
        // to ensure that works properly.  Instead we build up
        // the factory with the normal build mechanism
        
        // First the class service
        configurator.bind(BuilderHelper.link(WidgetFactory.class.getName()).
                to(Factory.class.getName()).
                build());
        
        // Second the per-lookup method
        DescriptorImpl di = BuilderHelper.link(WidgetFactory.class.getName()).
                to(Widget.class.getName()).build();
        di.setDescriptorType(DescriptorType.PROVIDE_METHOD);
        configurator.bind(di);
        
        // For the test below we have an abstract factory that is in a proxiable scope
        // and which is producing something from a proxiable scope
        // First we add the context, then we add the factories
        configurator.bind(BuilderHelper.link(ProxiableSingletonContext.class.getName()).
                to(Context.class.getName()).
                in(Singleton.class.getName()).build());
        configurator.bind(BuilderHelper.link(AdamsFactory.class).
                to(AdamsVP.class).
                in(ProxiableSingleton.class.getName()).
                buildFactory(ProxiableSingleton.class.getName()));
        configurator.bind(BuilderHelper.link(JeffersonVPFactory.class).
                to(JeffersonVP.class).
                in(ProxiableSingleton.class.getName()).
                buildFactory(ProxiableSingleton.class.getName()));
        configurator.bind(BuilderHelper.link(BurrVPFactory.class).
                to(BurrVP.class).
                in(ProxiableSingleton.class.getName()).
                buildFactory(ProxiableSingleton.class.getName()));
    }

}
