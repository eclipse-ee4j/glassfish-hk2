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

import org.glassfish.hk2.api.PerLookup;

/**
 * This receiver will receive a specific kind of HttpRequest
 * where the elements of the request have these types:
 * <OL>
 * <LI>The rank of the request is an integer</LI>
 * <LI>The id of the request is a long</LI>
 * <LI>The requested action is a String</LI>
 * </OL>
 * <p>
 * The getHttpRequest method of this class is annotated with the
 * @author jwells
 *
 */
@PerLookup
public class HttpEventReceiver {
    private int lastRank;
    private long lastId;
    private String lastAction;
    
    /**
     * This method will get called back with the
     * information filled in from the request, either
     * from the Alternate injection resolver or from
     * the system provided three-thirty resolver
     * 
     * @param rank the rank, parameter zero of the HttpRequest (from the alternate)
     * @param id the id, parameter one of the HttpRequest (from the alternate)
     * @param action the action, parameter two of the HttpRequest (from the alternate)
     * @param logger a logger to send interesting messages to
     */
    @AlternateInject
    public void receiveRequest(
            @HttpParameter int rank,
            @HttpParameter(1) long id,
            @HttpParameter(2) String action,
            Logger logger) {
        lastRank = rank;
        lastId = id;
        lastAction = action;
        
        logger.log("I got a message of rank " + lastRank + " and id " + lastId + " and action " + action);
    }

    /**
     * @return the lastRank
     */
    public int getLastRank() {
        return lastRank;
    }

    /**
     * @return the lastId
     */
    public long getLastId() {
        return lastId;
    }

    /**
     * @return the lastAction
     */
    public String getLastAction() {
        return lastAction;
    }
}
