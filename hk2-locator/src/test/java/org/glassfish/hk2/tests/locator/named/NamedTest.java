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

package org.glassfish.hk2.tests.locator.named;

import java.util.List;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.IndexedFilter;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class NamedTest {
    private final static String TEST_NAME = "NamedTest";
    private final static ServiceLocator locator = LocatorHelper.create(TEST_NAME, new NamedModule());
    
    /** Did my heart love till now? */
    public final static String ROMEO = "Romeo";
    /** O! she doth teach the torches to burn bright */
    public final static String JULIET = "Juliet";
    /** Queen Mab */
    public final static String MERCUTIO = "Mercutio";
    /** Romeos' cousin */
    public final static String BENVOLIO = "Benvolio";
    /** A rose by any other name */
    public final static String ROSE = "Rose";
    /** Romeo's first girlfriend */
    public final static String ROSALIND = "Rosalind";
    
    /**
     * Tests that I can differentiate between citizens
     */
    @Test
    public void getAutoNamedService() {
        CitizenOfVerona romeo = locator.getService(CitizenOfVerona.class, ROMEO);
        Assert.assertEquals(ROMEO, romeo.getName());
        
        CitizenOfVerona juliet = locator.getService(CitizenOfVerona.class, JULIET);
        Assert.assertEquals(JULIET, juliet.getName());
    }
    
    /**
     * Tests that I can inject via name on fields, methods and constructors
     */
    @Test
    public void getInjectedViaName() {
        Verona v = locator.getService(Verona.class);
        
        Assert.assertEquals(ROMEO, v.getRomeo().getName());
        Assert.assertEquals(JULIET, v.getJuliet().getName());
        Assert.assertEquals(MERCUTIO, v.getMercutio().getName());
        Assert.assertEquals(BENVOLIO, v.getBenvolio().getName());
    }
    
    /**
     * Tests that the same name of different types will both return
     */
    @Test
    public void getMultiNamed() {
        List<ActiveDescriptor<?>> roses = locator.getDescriptors(BuilderHelper.createNameFilter(ROSE));
        Assert.assertEquals(2, roses.size());
        
        int lcv = 0;
        for (ActiveDescriptor<?> rose : roses) {
            switch (lcv) {
            case 0:
                Assert.assertTrue(rose.getImplementation().equals(Centifolia.class.getName()));
                break;
            case 1:
                Assert.assertTrue(rose.getImplementation().equals(Damask.class.getName()));
                break;
            }
            
            lcv++;
        }
    }
    
    /**
     * Tests that the same name of different types will both return
     */
    @Test
    public void getMultiNamedQualifiedWithType() {
        List<ActiveDescriptor<?>> roses = locator.getDescriptors(
                BuilderHelper.createNameAndContractFilter(Centifolia.class.getName(), ROSE));
        Assert.assertEquals(1, roses.size());
        
        int lcv = 0;
        for (ActiveDescriptor<?> rose : roses) {
            switch (lcv) {
            case 0:
                Assert.assertTrue(rose.getImplementation().equals(Centifolia.class.getName()));
                break;
            }
            
            lcv++;
        }
        
        roses = locator.getDescriptors(
                BuilderHelper.createNameAndContractFilter(Damask.class.getName(), ROSE));
        Assert.assertEquals(1, roses.size());
        
        lcv = 0;
        for (ActiveDescriptor<?> rose : roses) {
            switch (lcv) {
            case 0:
                Assert.assertTrue(rose.getImplementation().equals(Damask.class.getName()));
                break;
            }
            
            lcv++;
        }
    }
    
    /**
     * Tests that you can use an Index filter with both values returning null
     */
    @Test
    public void getIndexedFilterWithBothIndexesNull() {
        List<ActiveDescriptor<?>> capulets = locator.getDescriptors(new DoubleNullIndexFilter(true));
        Assert.assertEquals(1, capulets.size());
        
        int lcv = 0;
        for (ActiveDescriptor<?> capulet : capulets) {
            switch (lcv) {
            case 0:
                Assert.assertTrue(capulet.getImplementation().equals(Juliet.class.getName()));
                break;
            }
            
            lcv++;
        }
        
        List<ActiveDescriptor<?>> montagues = locator.getDescriptors(new DoubleNullIndexFilter(false));
        Assert.assertEquals(3, montagues.size());
        
        lcv = 0;
        for (ActiveDescriptor<?> montague : montagues) {
            switch (lcv) {
            case 0:
                Assert.assertTrue(montague.getImplementation().equals(Romeo.class.getName()));
                break;
            case 1:
                Assert.assertTrue(montague.getImplementation().equals(Mercutio.class.getName()));
                break;
            case 2:
                Assert.assertTrue(montague.getImplementation().equals(Benvolio.class.getName()));
                break;
            }
            
            lcv++;
        }
    }
    
    private static class DoubleNullIndexFilter implements IndexedFilter {
        private final boolean capulet;
        
        private DoubleNullIndexFilter(boolean capulet) {
            this.capulet = capulet;
        }

        /* (non-Javadoc)
         * @see org.glassfish.hk2.api.Filter#matches(org.glassfish.hk2.api.Descriptor)
         */
        @Override
        public boolean matches(Descriptor d) {
            if (capulet) {
                if (d.getQualifiers().contains(Capulet.class.getName())) {
                    return true;
                }
            }
            else {
                if (d.getQualifiers().contains(Montague.class.getName())) {
                    return true;
                }
            }
            
            return false;
        }

        /* (non-Javadoc)
         * @see org.glassfish.hk2.api.IndexedFilter#getAdvertisedContract()
         */
        @Override
        public String getAdvertisedContract() {
            // Both indexes return null
            return null;
        }

        /* (non-Javadoc)
         * @see org.glassfish.hk2.api.IndexedFilter#getName()
         */
        @Override
        public String getName() {
            // Both indexes return null
            return null;
        }
        
    }

}
