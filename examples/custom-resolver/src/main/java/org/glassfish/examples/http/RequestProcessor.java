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

package org.glassfish.examples.http;

import javax.inject.Inject;

import org.glassfish.hk2.api.PerLookup;

/**
 * This is a class that processes an event as it comes.  In this implementation
 * it just gets an HttpEventReceiver and would possibly do something with it.
 * The HttpEventReciever of course has the information from the latest HttpRequest.
 * 
 * @author jwells
 *
 */
@PerLookup
public class RequestProcessor {
    @Inject
    private HttpEventReceiver eventReciever;
    
    /**
     * This implementation processes the HttpRequest and returns
     * its HttpEventReceiver (mainly so that it can be properly
     * tested)
     * 
     * @return The HttpEventReceiver for this request
     */
    public HttpEventReceiver processHttpRequest() {
        return eventReciever;
    }
}
