/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.tests.operation.basic;

import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author jwells
 *
 */
@BasicOperationScope
public class BasicOperationLifecycleMethods {
    private static HashMap<Object, Boolean> closedMap = new HashMap<Object, Boolean>();
    
    private Object id;
    
    @PostConstruct
    public synchronized void postConstruct() {
        id = new Object();
        closedMap.put(id, Boolean.FALSE);
    }
    
    @PreDestroy
    public synchronized void preDestroy() {
        closedMap.put(id, Boolean.TRUE);
    }
    
    public synchronized Object getId() {
        return id;
    }
    
    public static boolean isClosed(Object id) {
        return closedMap.get(id);
    }
}
