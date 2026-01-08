/*
 * Copyright (c) 2026 Contributors to Eclipse Foundation.
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

package org.glassfish.hk2.tests.locator.servicelocatorutilities;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Singleton;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

public class Hk2SingletonCreationTest {

    static final CountDownLatch firstInstanceCreationStartedLatch = new CountDownLatch(1);
    static final CountDownLatch secondInstanceCreatedLatch = new CountDownLatch(1);

    static class ConcurrentlyCreatedSingletonService {
        ConcurrentlyCreatedSingletonService() throws Exception {
            if (firstInstanceCreationStartedLatch.getCount() == 1) {
                firstInstanceCreationStartedLatch.countDown();
                secondInstanceCreatedLatch.await();
            }
        }
    }

    static final CountDownLatch firstInstanceInitializationStartedLatch = new CountDownLatch(1);
    static final CountDownLatch secondInstanceInitializedLatch = new CountDownLatch(1);

    static class ConcurrentlyInitializedSingletonService {
        @PostConstruct
        public void init() throws Exception {
            if (firstInstanceInitializationStartedLatch.getCount() == 1) {
                firstInstanceInitializationStartedLatch.countDown();
                secondInstanceInitializedLatch.await();
            }
        }
    }

    static class SequentiallyCreatedSingletonService {
    }

    static class TestBinder extends AbstractBinder {
        @Override
        protected void configure() {
            bind(ConcurrentlyCreatedSingletonService.class)
                    .to(ConcurrentlyCreatedSingletonService.class)
                    .in(Singleton.class)
                    .proxy(false);
            bind(ConcurrentlyInitializedSingletonService.class)
                    .to(ConcurrentlyInitializedSingletonService.class)
                    .in(Singleton.class)
                    .proxy(false);
            bind(SequentiallyCreatedSingletonService.class)
                    .to(SequentiallyCreatedSingletonService.class)
                    .in(Singleton.class)
                    .proxy(false);
        }
    }

    @Ignore("Ignored due to GH-523") @Test
    public void testConcurrentSingletonServiceCreation() throws Exception {
        ServiceLocator parentServiceLocator = ServiceLocatorUtilities
                .bind("mutliple-singleton-instances-parent-1", new TestBinder());
        ServiceLocator childServiceLocator = ServiceLocatorFactory.getInstance()
                .create("mutliple-singleton-instances-child-1", parentServiceLocator);

        AtomicReference<ConcurrentlyCreatedSingletonService> firstInstanceRef = new AtomicReference<>();

        Thread thread = new Thread(() -> firstInstanceRef.set(parentServiceLocator
                .getService(ConcurrentlyCreatedSingletonService.class)));

        thread.start();

        firstInstanceCreationStartedLatch.await();

        ConcurrentlyCreatedSingletonService secondInstance = childServiceLocator
                .getService(ConcurrentlyCreatedSingletonService.class);

        secondInstanceCreatedLatch.countDown();

        thread.join(); // first instance created

        Assert.assertSame(firstInstanceRef.get(), secondInstance); // this fails but is shouldn't
    }

    @Ignore("Ignored due to GH-523") @Test
    public void testConcurrentSingletonServiceInitialization() throws Exception {
        ServiceLocator parentServiceLocator = ServiceLocatorUtilities
                .bind("mutliple-singleton-instances-parent-2", new TestBinder());
        ServiceLocator childServiceLocator = ServiceLocatorFactory.getInstance()
                .create("mutliple-singleton-instances-child-2", parentServiceLocator);

        AtomicReference<ConcurrentlyInitializedSingletonService> firstInstanceRef = new AtomicReference<>();

        Thread thread = new Thread(() -> firstInstanceRef.set(parentServiceLocator
                .getService(ConcurrentlyInitializedSingletonService.class)));

        thread.start();

        firstInstanceInitializationStartedLatch.await();

        ConcurrentlyInitializedSingletonService secondInstance = childServiceLocator
                .getService(ConcurrentlyInitializedSingletonService.class);

        secondInstanceInitializedLatch.countDown();

        thread.join(); // first instance initialized

        Assert.assertSame(firstInstanceRef.get(), secondInstance); // this fails but is shouldn't
    }

    @Test
    public void testParentFirstSingletonServiceCreation() {
        ServiceLocator parentServiceLocator = ServiceLocatorUtilities
                .bind("parent-first-parent", new TestBinder());
        ServiceLocator childServiceLocator = ServiceLocatorFactory.getInstance()
                .create("parent-first-child", parentServiceLocator);

        SequentiallyCreatedSingletonService firstInstance = parentServiceLocator
                .getService(SequentiallyCreatedSingletonService.class);
        SequentiallyCreatedSingletonService secondInstance = childServiceLocator
                .getService(SequentiallyCreatedSingletonService.class);

        Assert.assertSame(firstInstance, secondInstance);
    }

    @Test
    public void testChildFirstSingletonServiceCreation() {
        ServiceLocator parentServiceLocator = ServiceLocatorUtilities
                .bind("child-first-parent", new TestBinder());
        ServiceLocator childServiceLocator = ServiceLocatorFactory.getInstance()
                .create("child-first-child", parentServiceLocator);

        SequentiallyCreatedSingletonService firstInstance = childServiceLocator
                .getService(SequentiallyCreatedSingletonService.class);
        SequentiallyCreatedSingletonService secondInstance = parentServiceLocator
                .getService(SequentiallyCreatedSingletonService.class);

        Assert.assertSame(firstInstance, secondInstance);
    }
}
