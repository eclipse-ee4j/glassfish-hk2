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

package org.glassfish.hk2.api;

/**
 * This is an interface that is also implemented by the
 * {@link org.aopalliance.intercept.MethodInvocation} object
 * passed to the invoke method of {@link org.aopalliance.intercept.MethodInterceptor}
 * and the {@link org.aopalliance.intercept.ConstructorInvocation} object
 * passed to the invoke method of {@link org.aopalliance.intercept.ConstructorInterceptor}.
 * It allows HK2 users of the AOP interception feature to easily pass
 * data between interceptors on the same invocatio stack 
 * 
 * @author jwells
 *
 */
public interface HK2Invocation {
    /**
     * Sets the user data to be associated with the invocation
     * frame.  The key may not be null.  If data is null
     * then the data associated with the key is removed
     * 
     * @param key a non-null identifier for user data to be
     * associated with the invocation frame
     * @param data possibly null data.  If non-null it
     * will replace any value previously associated with
     * the key.  If null it will remove the key from the
     * map
     */
    public void setUserData(String key, Object data);
    
    /**
     * Gets the user data associated with the given key
     * 
     * @param key The key for which to get data.  May
     * not be null
     * @return The user data previously associated with
     * this key, or null if there was no user data 
     * associated with this key
     */
    public Object getUserData(String key);

}
