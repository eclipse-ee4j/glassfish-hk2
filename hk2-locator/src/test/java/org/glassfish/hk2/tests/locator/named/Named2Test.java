/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
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

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.inject.Named;

import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.UnsatisfiedDependencyException;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class Named2Test {
    /**
     * Tests that a named qualifier on an injection point
     * must be satisfied
     */
    @Test
    public void testNamedQualifierMustBeSatisfied() {
        ServiceLocator locator = LocatorHelper.getServiceLocator(RosalindService.class,
                Rosalind.class,
                Romeo.class,
                RosalindBasisService.class);
        
        // This makes sure that Rosalind is a proper service and that someone can get it
        Assert.assertNotNull(locator.getService(RosalindBasisService.class));
        
        try {
            locator.getService(RosalindService.class);
            Assert.fail("Should not have worked, Rosalind service needs a name, but there is no service with that name");
        }
        catch (MultiException me) {
            // Expected
            Injectee foundUnsatisfied = null;
            for (Throwable th : me.getErrors()) {
                if (th instanceof UnsatisfiedDependencyException) {
                    foundUnsatisfied = ((UnsatisfiedDependencyException) th).getInjectee();
                }
            }
            
            Assert.assertNotNull(foundUnsatisfied);
            
            Assert.assertEquals(CitizenOfVerona.class, foundUnsatisfied.getRequiredType());
            Set<Annotation> annotations = foundUnsatisfied.getRequiredQualifiers();
            Assert.assertEquals(1, annotations.size());
            
            for (Annotation annotation : annotations) {
                Assert.assertEquals(Named.class, annotation.annotationType());
                
                Named named = (Named) annotation;
                Assert.assertEquals(NamedTest.ROSALIND, named.value());
            }
        }
    }

}
