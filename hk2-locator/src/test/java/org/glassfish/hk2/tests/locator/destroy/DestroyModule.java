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

package org.glassfish.hk2.tests.locator.destroy;

import javax.inject.Singleton;

import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.PerLookup;
import org.glassfish.hk2.tests.locator.utilities.TestModule;
import org.glassfish.hk2.utilities.BuilderHelper;

/**
 * @author jwells
 *
 */
public class DestroyModule implements TestModule {

    /* (non-Javadoc)
     * @see org.glassfish.hk2.Module#configure(org.glassfish.hk2.BinderFactory)
     */
    @Override
    public void configure(DynamicConfiguration configurator) {
        configurator.bind(BuilderHelper.link(Foo.class).in(PerLookup.class.getName()).build());
        configurator.bind(BuilderHelper.link(Bar.class).in(PerLookup.class.getName()).build());
        configurator.bind(BuilderHelper.link(Baz.class).in(PerLookup.class.getName()).build());
        configurator.bind(BuilderHelper.link(Qux.class).in(PerLookup.class.getName()).build());
        
        configurator.bind(BuilderHelper.link(Registrar.class).in(Singleton.class.getName()).build());
        
        // This is for the factory destruction test
        configurator.bind(BuilderHelper.link(SprocketFactory.class, true).
                to(Sprocket.class).
                in(PerLookup.class.getName()).buildFactory(Singleton.class.getName()));
        configurator.addActiveDescriptor(Widget.class);
        
        // This is for the multiple service handle destroy test
        configurator.bind(BuilderHelper.link(SingletonWithPerLookupInjection.class.getName()).
                in(Singleton.class.getName()).
                build());
        configurator.bind(BuilderHelper.link(PerLookupWithDestroy.class.getName()).
                in(PerLookup.class.getName()).
                build());
    }

}
