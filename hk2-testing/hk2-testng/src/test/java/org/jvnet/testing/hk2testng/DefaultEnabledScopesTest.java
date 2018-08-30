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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
@HK2
public class DefaultEnabledScopesTest {
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

        assertThat(ImmediateServiceImpl.waitForStart(20 * 1000)).isTrue();
    }

    /**
     * Tests that per thread scope is working by default
     *
     * @throws InterruptedException
     */
    @Test
    public void assertPerThreadScopeWorks() throws InterruptedException {
        ConcurrentHashMap<Long, PerThreadService> results = new ConcurrentHashMap<Long, PerThreadService>();

        for (int lcv = 0; lcv < 3; lcv++) {
            Thread t = new Thread(new LookupThread(locator, results));
            t.start();
        }

        while (results.size() < 3) {
            Thread.sleep(5);
        }


        for (Map.Entry<Long, PerThreadService> entry : results.entrySet()) {
            assertThat(entry.getKey()).isEqualTo(entry.getValue().getId());
        }
    }

    /**
     * Tests that inheritable thread scope is working by default
     *
     * @throws InterruptedException
     */
    @Test
    public void assertInheritableThreadScopeWorks() throws InterruptedException {
        ConcurrentHashMap<Long, InheritableThreadService> results = new ConcurrentHashMap<Long, InheritableThreadService>();

        for (int lcv = 0; lcv < 3; lcv++) {
            Thread t = new Thread(new LookupInheritableThread(locator, results));
            t.start();
        }

        while (results.size() < 3) {
            Thread.sleep(5);
        }

        for (Map.Entry<Long, InheritableThreadService> entry : results.entrySet()) {
            assertThat(entry.getKey()).isEqualTo(entry.getValue().getId());
        }
    }

    /**
     * Tests that reification errors are rethrown by default
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

        try {
            locator.getService(GenericInterface.class);
            Assert.fail("Should have failed because reification failures are rethrown by default");
        }
        catch (MultiException me) {
            assertThat(me.toString()).contains(SimpleService.class.getName());
        }
    }

    private final static class LookupThread implements Runnable {
        private final ServiceLocator locator;
        private final ConcurrentHashMap<Long, PerThreadService> addResult;

        private LookupThread(ServiceLocator locator, ConcurrentHashMap<Long, PerThreadService> addResult) {
            this.locator = locator;
            this.addResult = addResult;
        }

        @Override
        public void run() {
            long threadId = Thread.currentThread().getId();

            PerThreadService pts = locator.getService(PerThreadService.class);

            addResult.put(threadId, pts);
        }

    }

    private final static class LookupInheritableThread implements Runnable {

        private final ServiceLocator locator;
        private final ConcurrentHashMap<Long, InheritableThreadService> addResult;

        private LookupInheritableThread(ServiceLocator locator, ConcurrentHashMap<Long, InheritableThreadService> addResult) {
            this.locator = locator;
            this.addResult = addResult;
        }

        @Override
        public void run() {
            long threadId = Thread.currentThread().getId();

            InheritableThreadService pts = locator.getService(InheritableThreadService.class);

            addResult.put(threadId, pts);
        }

    }

}
