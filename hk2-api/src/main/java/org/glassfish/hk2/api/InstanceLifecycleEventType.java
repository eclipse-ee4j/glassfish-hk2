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

package org.glassfish.hk2.api;

/**
 * This describes the type of lifecycle events
 * that can happen in the system
 * 
 * @author jwells
 *
 */
public enum InstanceLifecycleEventType {
    /**
     * This lifecycle event is called before an object will be
     * created
     */
    PRE_PRODUCTION,
    
    /**
     * This lifecycle event is called after an object has been
     * created
     */
    POST_PRODUCTION,
    
    /**
     * This lifecycle event is called prior to an object being
     * destroyed
     */
    PRE_DESTRUCTION

}
