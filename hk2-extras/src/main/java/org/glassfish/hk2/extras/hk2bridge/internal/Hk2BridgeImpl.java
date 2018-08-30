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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.DynamicConfigurationListener;
import org.glassfish.hk2.api.DynamicConfigurationService;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.Visibility;
import org.glassfish.hk2.extras.ExtrasUtilities;

/**
 * @author jwells
 *
 */
@Singleton
@Visibility(DescriptorVisibility.LOCAL)
public class Hk2BridgeImpl implements DynamicConfigurationListener {
    private final ServiceLocator local;
    private ServiceLocator remote;
    private Filter filter;
    
    private List<ActiveDescriptor<?>> mirroredDescriptors = new ArrayList<ActiveDescriptor<?>>();
    
    @Inject
    private Hk2BridgeImpl(ServiceLocator local) {
        this.local = local;
        
    }
    
    public synchronized void setRemote(ServiceLocator remote) {
        this.remote = remote;
        this.filter = new NoLocalNoRemoteFilter(remote.getLocatorId());
        
        List<ActiveDescriptor<?>> newDescriptors = local.getDescriptors(filter);
        
        handleChange(newDescriptors);
    }
    
    @SuppressWarnings("unchecked")
    private synchronized void handleChange(List<ActiveDescriptor<?>> newDescriptors) {
        if (remote == null) return;
        
        HashSet<ActiveDescriptor<?>> toRemove = new HashSet<ActiveDescriptor<?>>(mirroredDescriptors);
        toRemove.removeAll(newDescriptors);
        
        HashSet<ActiveDescriptor<?>> toAdd = new HashSet<ActiveDescriptor<?>>(newDescriptors);
        toAdd.removeAll(mirroredDescriptors);
        
        DynamicConfigurationService remoteDCS = remote.getService(DynamicConfigurationService.class);
        DynamicConfiguration config = remoteDCS.createDynamicConfiguration();
        
        boolean dirty = false;
        for (ActiveDescriptor<?> removeMe : toRemove) {
            Filter removeFilter = new RemoveFilter(removeMe.getLocatorId(), removeMe.getServiceId());
            config.addUnbindFilter(removeFilter);
            dirty = true;
        }
        
        for (ActiveDescriptor<?> addMe : toAdd) {
            CrossOverDescriptor<Object> cod = new CrossOverDescriptor<Object>(local, (ActiveDescriptor<Object>) addMe);
            config.addActiveDescriptor(cod);
            dirty = true;
        }
        
        if (dirty) {
            config.commit();
        }
        
        mirroredDescriptors = newDescriptors;
    }
    
    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.DynamicConfigurationListener#configurationChanged()
     */
    @Override
    public void configurationChanged() {
        List<ActiveDescriptor<?>> newDescriptors = local.getDescriptors(filter);
        
        handleChange(newDescriptors);
    }
    
    private static class NoLocalNoRemoteFilter implements Filter {
        private final long remoteLocatorId;
        
        private NoLocalNoRemoteFilter(long remoteId) {
            remoteLocatorId = remoteId;
        }

        /* (non-Javadoc)
         * @see org.glassfish.hk2.api.Filter#matches(org.glassfish.hk2.api.Descriptor)
         */
        @Override
        public boolean matches(Descriptor d) {
            if (DescriptorVisibility.LOCAL.equals(d.getDescriptorVisibility())) {
                return false;
            }
            
            Set<Long> previousVisits = getMetadataLongsSet(d, ExtrasUtilities.HK2BRIDGE_LOCATOR_ID);
            
            if (previousVisits.contains(new Long(remoteLocatorId))) {
                // cycle!
                return false;
            }
            
            return true;
        }
    }
    
    @PreDestroy
    private void preDestroy() {
        handleChange(Collections.<ActiveDescriptor<?>>emptyList());
    }
    
    private static class RemoveFilter implements Filter {
        private final long localLocatorId;
        private final long localServiceId;
        
        private RemoveFilter(long localLocatorId, long localServiceId) {
            this.localLocatorId = localLocatorId;
            this.localServiceId = localServiceId;
        }

        /* (non-Javadoc)
         * @see org.glassfish.hk2.api.Filter#matches(org.glassfish.hk2.api.Descriptor)
         */
        @Override
        public boolean matches(Descriptor d) {
            List<Long> locatorIds = getMetadataLongsList(d, ExtrasUtilities.HK2BRIDGE_LOCATOR_ID);
            int index = -1;
            int lcv = 0;
            for (Long locatorId : locatorIds) {
                if (localLocatorId == locatorId) {
                    index = lcv;
                    break;
                }
                lcv++;
            }
            if (index == -1) return false;
            
            List<Long> serviceIds = getMetadataLongsList(d, ExtrasUtilities.HK2BRIDGE_SERVICE_ID);
            Long serviceId = serviceIds.get(index);
            
            return (serviceId == localServiceId);
        }
    }
    
    /**
     * Gets all of the longs encoded into this descriptors metadata
     * field
     * 
     * @param d
     * @param field
     * @return
     */
    private static Set<Long> getMetadataLongsSet(Descriptor d, String field) {
        Set<Long> retVal = new HashSet<Long>();
        
        List<String> metadataValues = d.getMetadata().get(field);
        if (metadataValues == null) return retVal;
        
        for (String metadataValue : metadataValues) {
            try {
                Long val = new Long(metadataValue);
                retVal.add(val);
            }
            catch (NumberFormatException nfe) {
                // Do nothing, just skip it
            }
        }
        
        return retVal;
    }
    
    private final static List<Long> EMPTY_LIST = Collections.emptyList();
    
    /**
     * Gets all of the longs encoded into this descriptors metadata
     * field
     * 
     * @param d
     * @param field
     * @return
     */
    private static List<Long> getMetadataLongsList(Descriptor d, String field) {
        List<String> metadataValues = d.getMetadata().get(field);
        if (metadataValues == null) return EMPTY_LIST;
        
        List<Long> retVal = new ArrayList<Long>(metadataValues.size());
        
        for (String metadataValue : metadataValues) {
            try {
                Long val = new Long(metadataValue);
                retVal.add(val);
            }
            catch (NumberFormatException nfe) {
                // Do nothing, just skip it
            }
        }
        
        return retVal;
    }

    
}
