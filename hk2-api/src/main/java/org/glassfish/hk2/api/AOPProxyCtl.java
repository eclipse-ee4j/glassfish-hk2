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
 * This interface is implemented by all services that use the
 * AOP Alliance Interceptor feature of HK2.
 * <p>
 * Compare this to the {@link ProxyCtl} interface, which is
 * implemented by proxies used for {@link Proxiable} scopes or
 * service instances.
 * 
 * @author jwells
 *
 */
public interface AOPProxyCtl {
    /** The name of the method of this interface */
    public final static String UNDERLYING_METHOD_NAME = "__getUnderlyingDescriptor";
    
    /**
     * This method returns the ActiveDescriptor underlying the
     * service for which this object is a proxy
     * 
     * @return The underlying ActiveDescriptor for which this
     * object is a proxy.  May return null if there is no known
     * underlying descriptor
     */
    public ActiveDescriptor<?> __getUnderlyingDescriptor();

}
