/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.jvnet.hk2.jmx.api;

import javax.management.ObjectName;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Filter;
import org.jvnet.hk2.annotations.Contract;

/**
 * This service must be implemented in order to specify those
 * descriptors that might be reflected into JMX
 * 
 * @author jwells
 *
 */
@Contract
public interface HK2JmxSpecifier {
    /**
     * Gets the string for connecting to the JmxServer
     * 
     * @return The name of the JmxServer to connect to, or null for the platform server
     */
    public String getJmxServerName();
    
    /**
     * Returns true for descriptors that should possibly have
     * instances put into JMX.  May be an implementation
     * of IndexedFilter
     *   
     * @return The non-null filter to determine what descriptors
     * should be considered for adding to Jmx
     */
    public Filter getJmxDescriptorFilter();
    
    /**
     * Gets the Object that should be used either directly
     * or to be modelled.
     * 
     * @param descriptor The descriptor from which this instance was created
     * @param instance The instance created
     * @return The object to either be placed directly into JMX or to be
     * modelled.  If this returns null then this instance will not be put into
     * Jmx
     */
    public Object getJmxObject(ActiveDescriptor<?> descriptor, Object instance);
    
    /**
     * Returns the policy to use for adding the next JmxObject
     * @return
     */
    public String getAnalysisPolicy();
    
    public ObjectName getObjectName(Object instance);
    
    public String getObjectNamePolicy();
    
    public String getDomainName();
}
