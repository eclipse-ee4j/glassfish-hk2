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

package org.glassfish.hk2.tests.locator.justintime;

import java.lang.reflect.Field;
import java.util.List;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.hk2.utilities.InjecteeImpl;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class JustInTimeTest {
    private final static String TEST_NAME = "JustInTimeTest";
    private final static ServiceLocator locator = LocatorHelper.create(TEST_NAME, new JustInTimeModule());

    /**
     * Tests that if I forgot to add a service, I can add it just in time
     */
    @Test
    public void testJustInTimeResolution() {
        InjectedThriceService threeTimes = locator.getService(InjectedThriceService.class);
        Assert.assertNotNull(threeTimes);
        Assert.assertTrue(threeTimes.isValid());

        // Make sure the resolver was only called once
        SimpleServiceJITResolver jitResolver = locator.getService(SimpleServiceJITResolver.class);
        Assert.assertNotNull(jitResolver);

        Assert.assertEquals("Expected 1 JIT resolution, but got " + jitResolver.getNumTimesCalled(), 1, jitResolver.getNumTimesCalled());

    }

    /**
     * In this test the resolver itself has resolution problems.  We make sure this does not
     * mess up the other resolver, and that once the resolution problem of the resolver has
     * been fixed that it can do its job properly.
     */
    @Test
    public void testDoubleTroubleResolution() {
        try {
            locator.getService(DoubleTroubleService.class);
            Assert.fail("DoubleTrouble depends on Service2 which should not be available yet");
        } catch (MultiException me) {
            // Good
        }

        // SimpleService3 will fix the DoubleTrouble JIT resolver
        ServiceLocatorUtilities.addOneDescriptor(locator, BuilderHelper.link(SimpleService3.class).build());

        Assert.assertNotNull(locator.getService(DoubleTroubleService.class));
    }

    /**
     * This test ensures that a direct lookup (with {@link ServiceLocator#getInjecteeDescriptor(org.glassfish.hk2.api.Injectee)})
     * works properly
     */
    @Test
    public void testJITInLookup() {
        InjecteeImpl injectee = new InjecteeImpl(SimpleService.class);

        ActiveDescriptor<?> ad = locator.getInjecteeDescriptor(injectee);
        Assert.assertNotNull(ad);
    }
    
    /**
     * Tests the get method of Provider
     */
    @Test
    public void testProviderGet() {
        ServiceLocator locator = getProviderLocator();
        
        IterableProviderService ips = locator.getService(IterableProviderService.class);
        Assert.assertNotNull(ips);
        
        Assert.assertNotNull(locator.getService(SimpleService4.class));
        
        ips.checkGet();
    }
    
    /**
     * Tests the getHandle method of Provider
     */
    @Test
    public void testProviderGetHandle() {
        ServiceLocator locator = getProviderLocator();
        
        IterableProviderService ips = locator.getService(IterableProviderService.class);
        Assert.assertNotNull(ips);
        
        Assert.assertNotNull(locator.getService(SimpleService4.class));
        
        ips.checkGetHandle();
    }
    
    /**
     * Tests the getHandle method of Provider
     */
    @Test
    public void testProviderIterator() {
        ServiceLocator locator = getProviderLocator();
        
        IterableProviderService ips = locator.getService(IterableProviderService.class);
        Assert.assertNotNull(ips);
        
        Assert.assertNotNull(locator.getService(SimpleService4.class));
        
        ips.checkIterator();
    }
    
    /**
     * Tests the getSize method of Provider
     */
    @Test
    public void testProviderSize() {
        ServiceLocator locator = getProviderLocator();
        
        IterableProviderService ips = locator.getService(IterableProviderService.class);
        Assert.assertNotNull(ips);
        
        Assert.assertNotNull(locator.getService(SimpleService4.class));
        
        ips.checkSize();
    }

    @Test
    public void testMaliciousResolver() {
        ServiceLocator locator = getProviderLocator();

        Assert.assertNull(locator.getService(UnimplementedContract.class));
    }
    
    /**
     * Tests the getSize method of Provider
     */
    @Test
    public void testProviderHandleIterator() {
        ServiceLocator locator = getProviderLocator();
        
        IterableProviderService ips = locator.getService(IterableProviderService.class);
        Assert.assertNotNull(ips);
        
        Assert.assertNotNull(locator.getService(SimpleService4.class));
        
        ips.checkHandleIterator();
    }
    
    /**
     * Makes sure that JIT is only called once from a Provider.get and
     * that the parent is properly set
     */
    @Test
    public void testProperInjecteeFromIterableProviderAndOnlyOne() {
        ServiceLocator locator = ServiceLocatorFactory.getInstance().create(null);
        
        ServiceLocatorUtilities.addClasses(locator,
                JITRecorder.class,
                IterableProviderService.class);
        
        JITRecorder recorder = locator.getService(JITRecorder.class);
        recorder.clear();
        
        IterableProviderService ips = locator.getService(IterableProviderService.class);
        
        Assert.assertEquals(0, recorder.getInjectees().size());
        
        ips.checkUnimplementedGet();
        
        List<Injectee> injectees = recorder.getInjectees();
        Assert.assertEquals(1, injectees.size());
        
        Injectee onlyInjectee = injectees.get(0);
        
        // Make sure the parent is correct
        Field field = (Field) onlyInjectee.getParent();
        Assert.assertEquals("unimplementedContract", field.getName());
    }
    
    private static ServiceLocator getProviderLocator() {
        ServiceLocator locator = ServiceLocatorFactory.getInstance().create(null);
        
        ServiceLocatorUtilities.addClasses(locator,
                IterableProviderService.class,
                EvilJITResolver.class,
                SimpleService4JITResolver.class);
        
        return locator;
    }

}
