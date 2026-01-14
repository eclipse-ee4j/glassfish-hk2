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
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.junit.Assert;
import org.junit.Test;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Singleton;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.junit.Before;

public class Hk2SingletonCreationTest {

    private static CompletableFuture<Void> firstReferenceCreationStarted;
    private static CompletableFuture<Void> firstReferenceCreated;

    static class ConcurrentlyCreatedService {

        ConcurrentlyCreatedService() throws Exception {
            firstReferenceCreationStarted.complete(null);
            firstReferenceCreated.join();
            Thread.sleep(100);
        }
    }

    static class ConcurrentlyInitializedService {

        @PostConstruct
        public void init() throws Exception {
            firstReferenceCreationStarted.complete(null);
            firstReferenceCreated.join();
            Thread.sleep(100);
        }
    }

    private ServiceLocator createServiceLocator() {
        return ServiceLocatorUtilities
                .bind("test-locator", new AbstractBinder() {
                    @Override
                    protected void configure() {
                        bind(ConcurrentlyCreatedService.class)
                                .to(ConcurrentlyCreatedService.class)
                                .in(Singleton.class)
                                .proxy(false);
                        bind(ConcurrentlyInitializedService.class)
                                .to(ConcurrentlyInitializedService.class)
                                .in(Singleton.class)
                                .proxy(false);
                    }
                });
    }

    @Before
    public void initTest() {
        firstReferenceCreationStarted = new CompletableFuture<>();
        firstReferenceCreated = new CompletableFuture<>();
    }

    @Test
    public void testSingletonsCreatedConcurrently() throws Exception {
        testConcurrentSingletonRetrieval(ConcurrentlyCreatedService.class);
    }

    @Test
    public void testSingletonsInitializedConcurrently() throws Exception {
        testConcurrentSingletonRetrieval(ConcurrentlyInitializedService.class);
    }
    private <SERVICE_CLASS> void testConcurrentSingletonRetrieval(Class<SERVICE_CLASS> serviceClass) throws MultiException, InterruptedException {
        ServiceLocator parentLocator = createServiceLocator();
        ServiceLocator childLocator = ServiceLocatorFactory.getInstance()
                .create("child-test-locator", parentLocator);

        AtomicReference<SERVICE_CLASS> firstReference = new AtomicReference<>();

        Thread thread = new Thread(() -> firstReference.set(parentLocator
                .getService(serviceClass)));

        thread.start();

        firstReferenceCreationStarted.join();
        firstReferenceCreated.complete(null);

        System.out.println("Before second getService");

        SERVICE_CLASS secondInstance = childLocator
                .getService(serviceClass);

        thread.join(); // first instance created

        Assert.assertSame(firstReference.get(), secondInstance); // this fails but is shouldn't
    }

}
