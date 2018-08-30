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

package org.glassfish.examples.http.test;

import junit.framework.Assert;

import org.glassfish.examples.http.HttpEventReceiver;
import org.glassfish.examples.http.HttpServer;
import org.glassfish.examples.http.RequestProcessor;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;

import org.junit.Before;
import org.junit.Test;

/**
 * The test that ensures that this example works properly
 *
 * @author jwells
 */
public class CustomResolverTest {
    private ServiceLocator locator;

    /**
     * For Junit, does this before every test
     */
    @Before
    public void doBefore() {
        locator = ServiceLocatorFactory.getInstance().create("CustomResolverTest");

        Populator.populate(locator);
    }

    private void doRequest(int rank, long id, String event) {
        HttpServer httpServer = locator.getService(HttpServer.class);

        httpServer.startRequest("" + rank, "" + id, event);

        RequestProcessor processor = locator.getService(RequestProcessor.class);

        HttpEventReceiver receiver = processor.processHttpRequest();

        httpServer.finishRequest();

        // And now test that we got what we should have
        Assert.assertEquals(rank, receiver.getLastRank());
        Assert.assertEquals(id, receiver.getLastId());
        Assert.assertEquals(event, receiver.getLastAction());
    }

    /**
     * Runs some requests through our fake HttpServer
     */
    @Test
    public void testSomeRequests() {
        doRequest(50, 1, "FirstRequest");
        doRequest(100, 2, "SecondRequest");
        doRequest(1000, 3, "ThirdRequest");
    }
}
