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

import java.util.List;

import junit.framework.Assert;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.junit.Test;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.reflection.Pretty;

/**
 * @author jwells
 *
 */
public class DestroyTest {
    private final static String TEST_NAME = "DestroyTest";
    private final static ServiceLocator locator = LocatorHelper.create(TEST_NAME, new DestroyModule());
    
    /**
     * Tests that things are destroyed in opposite order from where they began
     */
    @Test
    public void testDestructionOrder() {
        ServiceHandle<Foo> fooHandle = locator.getServiceHandle(Foo.class);
        Assert.assertNotNull(fooHandle);
        
        Foo foo = fooHandle.getService();
        Assert.assertNotNull(foo);
        
        // Kill them all!  Mwmuaaaaaa
        fooHandle.destroy();
        
        Registrar registrar = locator.getService(Registrar.class);
        
        List<Object> births = registrar.getBirths();
        List<Object> deaths = registrar.getDeaths();
        
        Assert.assertEquals("Did not get all the postConstructs we expected: " + Pretty.collection(births), 4, births.size());
        Assert.assertEquals("Got invalid birth order: " + Pretty.collection(births), Qux.class, births.get(0).getClass());
        Assert.assertEquals("Got invalid birth order: " + Pretty.collection(births), Baz.class, births.get(1).getClass());
        Assert.assertEquals("Got invalid birth order: " + Pretty.collection(births), Bar.class, births.get(2).getClass());
        Assert.assertEquals("Got invalid birth order: " + Pretty.collection(births), Foo.class, births.get(3).getClass());
        
        Assert.assertEquals("Did not get all the preDestroys we expected: " + Pretty.collection(deaths), 4, deaths.size());
        Assert.assertEquals("Got invalid birth order: " + Pretty.collection(births), Qux.class, deaths.get(3).getClass());
        Assert.assertEquals("Got invalid birth order: " + Pretty.collection(births), Baz.class, deaths.get(2).getClass());
        Assert.assertEquals("Got invalid birth order: " + Pretty.collection(births), Bar.class, deaths.get(1).getClass());
        Assert.assertEquals("Got invalid birth order: " + Pretty.collection(births), Foo.class, deaths.get(0).getClass());
    }
    
    /**
     * Tests the destroy on a per-lookup factory produced object is called
     */
    @Test
    public void testFactoryCreatedServiceDestruction() {
        ServiceHandle<Widget> widgetHandle = locator.getServiceHandle(Widget.class);
        Assert.assertNotNull(widgetHandle);
        
        Widget widget = widgetHandle.getService();
        Assert.assertNotNull(widget);
        
        widgetHandle.destroy();
        
        Assert.assertTrue(widget.isDestroyed());
        
        try {
            widget.badUse();
            Assert.fail("The underlying sprocket should be closed and hence throw");
        }
        catch (IllegalStateException ise) {
            // This is good
        }
        
        // Now test that the factory itself was not destroyed
        SprocketFactory sprocketFactory = locator.getService(SprocketFactory.class);
        Assert.assertNotNull(sprocketFactory);
        
        Assert.assertFalse(sprocketFactory.isDestroyed());
        
        Assert.assertSame(sprocketFactory, widget.getSprocketFactory());
    }
    
    /**
     * Tests a real destruction of a singleton factory but ensure it still works
     * (with a different factory) after the explicit user destruction
     */
    @Test
    public void testUserDestructionOfFactory() {
        ServiceHandle<SprocketFactory> sprocketFactoryHandle1 = locator.getServiceHandle(SprocketFactory.class);
        Assert.assertNotNull(sprocketFactoryHandle1);
        
        SprocketFactory sprocketFactory1 = sprocketFactoryHandle1.getService();
        
        Widget widget1 = locator.getService(Widget.class);
        Assert.assertNotNull(widget1);
        
        Assert.assertSame(sprocketFactory1, widget1.getSprocketFactory());
        
        Assert.assertFalse(sprocketFactory1.isDestroyed());
        
        sprocketFactoryHandle1.destroy();
        
        Assert.assertTrue(sprocketFactory1.isDestroyed());
        
        // Now ensure we can still get a widget!
        Widget widget2 = locator.getService(Widget.class);
        Assert.assertNotNull(widget2);
        
        Assert.assertNotSame(sprocketFactory1, widget2.getSprocketFactory());
    }
    
    @Test @org.junit.Ignore
    public void testNotOriginalServiceHandleDestruction() {
        SingletonWithPerLookupInjection swpli = locator.getService(SingletonWithPerLookupInjection.class);
        Assert.assertFalse(swpli.isDestroyed());
        
        PerLookupWithDestroy plwd = swpli.getPerLookup();
        Assert.assertFalse(plwd.isDestroyed());
        
        ServiceHandle<SingletonWithPerLookupInjection> handle =
                locator.getServiceHandle(SingletonWithPerLookupInjection.class);
        
        handle.destroy();
        
        Assert.assertTrue(swpli.isDestroyed());
        Assert.assertFalse(swpli.wasPerLookupDestroyed());
        
        Assert.assertTrue(plwd.isDestroyed());
    }
    
    /**
     * Tests that a descriptor that has been unbound
     * cannot be used after it has been removed
     */
    @Test(expected=IllegalStateException.class) // @org.junit.Ignore
    public void testDisposedDescriptorCannotBeUsedToCreateAService() {
        ServiceLocator locator = LocatorHelper.create();
        ActiveDescriptor<?> desc = ServiceLocatorUtilities.addClasses(locator, SimpleService.class).get(0);
        
        ServiceHandle<?> handle = locator.getServiceHandle(desc);
        Assert.assertNotNull(handle.getService());
        
        // Remove it
        ServiceLocatorUtilities.removeOneDescriptor(locator, desc);
        
        // Should error out, descriptor has been removed
        handle.getService();
    }

}
