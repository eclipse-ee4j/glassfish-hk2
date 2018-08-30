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

package org.jvnet.testing.hk2testng;

import javax.inject.Inject;
import static org.assertj.core.api.Assertions.assertThat;
import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.HK2Loader;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.jvnet.testing.hk2testng.service.GenericInterface;
import org.jvnet.testing.hk2testng.service.InheritableThreadService;
import org.jvnet.testing.hk2testng.service.PerThreadService;
import org.jvnet.testing.hk2testng.service.impl.ImmediateServiceImpl;
import org.jvnet.testing.hk2testng.service.impl.SimpleService;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author jwells
 *
 */
@HK2(enablePerThread = false, enableImmediate = false, enableLookupExceptions = false, enableInheritableThread = false)
public class NonDefaultEnabledScopesTest {
    @Inject
    private ServiceLocator locator;

    /**
     * Tests that immediate scope is working by default
     *
     * @throws InterruptedException
     */
    @Test
    public void assertImmediateScopeWorks() throws InterruptedException {
        ServiceLocatorUtilities.addClasses(locator, ImmediateServiceImpl.class);

        try {
            locator.getService(ImmediateServiceImpl.class);
            Assert.fail("No context available for ImmediateServiceImpl");
        }
        catch (MultiException me) {
            // success
        }
    }

    /**
     * Tests that per thread scope is working by default
     *
     * @throws InterruptedException
     */
    @Test
    public void assertPerThreadScopeWorks() throws InterruptedException {
        try {
            locator.getService(PerThreadService.class);
            Assert.fail("No context available for PerThreadService");
        }
        catch (MultiException me) {
            // success
        }
    }

    /**
     * Tests that inheritable thread scope is working by default
     *
     * @throws InterruptedException
     */
    @Test
    public void assertInheritableThreadScopeWorks() throws InterruptedException {
        try {
            locator.getService(InheritableThreadService.class);
            Assert.fail("No context available for InheritableThreadService");
        } catch (MultiException me) {
            // success
        }
    }

    /**
     * Tests that reification errors are not rethrown
     */
    @Test
    public void assertReifyExceptionsAreThrown() {
        Descriptor addMe = BuilderHelper.link(SimpleService.class.getName()).
                to(GenericInterface.class.getName()).
                andLoadWith(new HK2Loader() {

                    @Override
                    public Class<?> loadClass(String className)
                            throws MultiException {
                        throw new MultiException(new ClassNotFoundException("Could not find " + className));
                    }

                }).build();

        ServiceLocatorUtilities.addOneDescriptor(locator, addMe);

        GenericInterface gi = locator.getService(GenericInterface.class);
        assertThat(gi).isNull();
    }

}
