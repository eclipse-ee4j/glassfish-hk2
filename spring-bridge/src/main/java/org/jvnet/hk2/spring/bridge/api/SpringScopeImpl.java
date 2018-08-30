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

package org.jvnet.hk2.spring.bridge.api;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

/**
 * @author jwells
 *
 */
public class SpringScopeImpl implements Scope {
    private ServiceLocator locator;
    
    /**
     * Sets the service locator to use with this scope
     * @param locator The (non-null) locator to use for this scope
     */
    public synchronized void setServiceLocator(ServiceLocator locator) {
        this.locator = locator;
    }
    
    /**
     * This can be used to configure the name of the service locator
     * to use
     * 
     * @param name The name to be used.  If null an anonymous service
     * locator will be used
     */
    public synchronized void setServiceLocatorName(String name) {
        locator = ServiceLocatorFactory.getInstance().create(name);
    }
    
    /**
     * Returns the {@link ServiceLocator} associated with this
     * scope
     * 
     * @return The {@link ServiceLocator} to be used with this scope
     */
    public synchronized ServiceLocator getServiceLocator() {
        return locator;
    }
    
    private synchronized ServiceHandle<?> getServiceFromName(String id) {
        if (locator == null) throw new IllegalStateException(
                "ServiceLocator must be set");
        
        ActiveDescriptor<?> best = locator.getBestDescriptor(BuilderHelper.createTokenizedFilter(id));
        if (best == null) return null;
        
        return locator.getServiceHandle(best);
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.config.Scope#get(java.lang.String, org.springframework.beans.factory.ObjectFactory)
     */
    @Override
    public Object get(String contractName, ObjectFactory<?> factory) {
        ServiceHandle<?> serviceHandle = getServiceFromName(contractName);
        if (serviceHandle == null) return factory.getObject();
        
        return serviceHandle.getService();
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.config.Scope#getConversationId()
     */
    @Override
    public String getConversationId() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.config.Scope#registerDestructionCallback(java.lang.String, java.lang.Runnable)
     */
    @Override
    public void registerDestructionCallback(String arg0, Runnable arg1) {
        // TODO Not sure what to do with this

    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.config.Scope#remove(java.lang.String)
     */
    @Override
    public Object remove(String arg0) {
        // TODO:  One possibility is to truly keep the handles
        
        ServiceHandle<?> handle = getServiceFromName(arg0);
        if (handle == null) return null;
        
        handle.destroy();
        return null;
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.config.Scope#resolveContextualObject(java.lang.String)
     */
    @Override
    public Object resolveContextualObject(String arg0) {
        // TODO Auto-generated method stub
        return null;
    }

}
