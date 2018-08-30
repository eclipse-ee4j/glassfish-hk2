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

package org.glassfish.hk2.extras.hk2bridge.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.extras.ExtrasUtilities;
import org.glassfish.hk2.utilities.AbstractActiveDescriptor;

/**
 * @author jwells
 *
 */
public class CrossOverDescriptor<T> extends AbstractActiveDescriptor<T> {
    private final ServiceLocator remoteLocator;
    private final ActiveDescriptor<T> remote;
    private boolean remoteReified;
    
    public CrossOverDescriptor(ServiceLocator local, ActiveDescriptor<T> localService) {
        super(localService);
        
        this.remoteLocator = local;
        this.remote = localService;
        remoteReified = localService.isReified();
        
        if (remoteReified) {
            setScopeAsAnnotation(remote.getScopeAsAnnotation());
        }
        else {
            setScope(remote.getScope());
        }
        
        addMetadata(ExtrasUtilities.HK2BRIDGE_LOCATOR_ID, Long.toString(local.getLocatorId()));
        addMetadata(ExtrasUtilities.HK2BRIDGE_SERVICE_ID, Long.toString(localService.getServiceId()));
    }

    /**
     * This method returns true if this descriptor has been reified
     * (class loaded).  If this method returns false then the other methods
     * in this interface will throw an IllegalStateException.  Once this
     * method returns true it may be
     * 
     * @return true if this descriptor has been reified, false otherwise
     */
    public boolean isReified() {
        return true;
    }
    
    private synchronized void checkState() {
        if (remoteReified) return;
        remoteReified = true;
        
        if (remote.isReified()) return;
        remoteLocator.reifyDescriptor(remote);
    }
    
    @Override
    public Class<?> getImplementationClass() {
        checkState();
        
        return remote.getImplementationClass();
    }

    @Override
    public Type getImplementationType() {
        checkState();
        
        return remote.getImplementationType();
    }

    @Override
    public void setImplementationType(Type t) {
        checkState();
        throw new AssertionError("Can not set type of remove descriptor");    
    }
    
    @Override
    public Set<Type> getContractTypes() {
        checkState();
        
        return remote.getContractTypes();
    }
    
    @Override
    public Annotation getScopeAsAnnotation() {
        checkState();
        
        return remote.getScopeAsAnnotation();
    }
    
    @Override
    public Class<? extends Annotation> getScopeAnnotation() {
        checkState();
        
        return remote.getScopeAnnotation();
    }
    
    @Override
    public Set<Annotation> getQualifierAnnotations() {
        checkState();
        
        return remote.getQualifierAnnotations();
    }
    
    @Override
    public List<Injectee> getInjectees() {
        checkState();
        
        return remote.getInjectees();
    }
    
    @Override
    public Long getFactoryServiceId() {
        checkState();
        
        return remote.getFactoryServiceId();
    }
    
    @Override
    public Long getFactoryLocatorId() {
        checkState();
        
        return remote.getFactoryLocatorId();
    }
    
    @Override
    public T create(ServiceHandle<?> root) {
        checkState();
        
        return remoteLocator.getService(remote, root);
    }
    
    @Override
    public void dispose(T instance) {
        checkState();
        
        remote.dispose(instance);
    }

}
