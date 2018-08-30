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

package org.glassfish.hk2.utilities;

import java.util.HashSet;
import java.util.Set;

import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.DescriptorType;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.IndexedFilter;
import org.glassfish.hk2.api.PerLookup;
import org.glassfish.hk2.api.PopulatorPostProcessor;
import org.glassfish.hk2.api.ServiceLocator;

/**
 * This post-processor removes duplicate descriptors from the
 * set of descriptors being added to the service registry.
 * <p>
 * It is often the case when using a classpath that the same jar
 * file can appear on the path more than once.  For example this
 * is often done when patching.  However, if this jar contains
 * HK2 descriptor files in it, that can mean duplicate services
 * that are not intended to be duplicated.  This service removes
 * all duplicate descriptors from the set to be added to HK2
 * 
 * @author jwells
 *
 */
@PerLookup
public class DuplicatePostProcessor implements PopulatorPostProcessor {

	private final DuplicatePostProcessorMode mode;
    private final HashSet<DescriptorImpl> strictDupSet = new HashSet<DescriptorImpl>();
    private final HashSet<ImplOnlyKey> implOnlyDupSet = new HashSet<ImplOnlyKey>();
    
    /**
     * Creates a DuplicatePostProcessor with the STRICT mode
     * for determining duplicates
     */
    public DuplicatePostProcessor() {
    	this(DuplicatePostProcessorMode.STRICT);
    }
    
    /**
     * Creates a DuplicatePostProcessor with the 
     * @param mode
     */
    public DuplicatePostProcessor(DuplicatePostProcessorMode mode) {
    	this.mode = mode;
    }
    
    /**
     * Returns the mode of this DuplicatePostProcessorMode of this processor
     * 
     * @return The mode
     */
    public DuplicatePostProcessorMode getMode() {
    	return mode;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.bootstrap.PopulatorPostProcessor#process(org.glassfish.hk2.utilities.DescriptorImpl)
     */
    @Override
    public DescriptorImpl process(ServiceLocator serviceLocator, DescriptorImpl descriptorImpl) {
    	switch (mode) {
    	case STRICT:
    		return strict(serviceLocator, descriptorImpl);
    	case IMPLEMENTATION_ONLY:
    		return implementationOnly(serviceLocator, descriptorImpl);
    	default:
    		throw new AssertionError("UnkownMode: " + mode);
    	}
    }
    
    private DescriptorImpl implementationOnly(ServiceLocator serviceLocator, final DescriptorImpl descriptorImpl) {
    	final String impl = descriptorImpl.getImplementation();
    	if (impl == null) return descriptorImpl;
    	
    	ImplOnlyKey key = new ImplOnlyKey(descriptorImpl);
    	
    	if (implOnlyDupSet.contains(key)) {
    		return null;
    	}
    	implOnlyDupSet.add(key);
    	
    	if (serviceLocator.getBestDescriptor(new Filter() {

			@Override
			public boolean matches(Descriptor d) {
				if (d.getImplementation().equals(impl) && d.getDescriptorType().equals(descriptorImpl.getDescriptorType())) {
					return true;
				}
				
				return false;
			}
    		
    	}) != null) {
    		return null;
    	}
    	
    	return descriptorImpl;    	
    }
    
    private DescriptorImpl strict(ServiceLocator serviceLocator, DescriptorImpl descriptorImpl) {
    	if (strictDupSet.contains(descriptorImpl)) {
            return null;
        }
        strictDupSet.add(descriptorImpl);
        
        Set<String> contracts = descriptorImpl.getAdvertisedContracts();
        String contract = null;
        for (String candidate : contracts) {
            if (candidate.equals(descriptorImpl.getImplementation())) {
                // Prefer this one over anything else
                contract = candidate;
                break;
            }
            
            contract = candidate;
        }
        
        final String fContract = contract;
        final String fName = descriptorImpl.getName();
        final DescriptorImpl fDescriptorImpl = descriptorImpl;
        
        if (serviceLocator.getBestDescriptor(new IndexedFilter() {

            @Override
            public boolean matches(Descriptor d) {
                return fDescriptorImpl.equals(d);
            }

            @Override
            public String getAdvertisedContract() {
                return fContract;
            }

            @Override
            public String getName() {
                return fName;
            }
            
        }) != null) {
            // Already in the locator, do not add again
            return null;
        }
        
        return descriptorImpl;
    	
    }
    
    @Override
    public String toString() {
    	return "DuplicateCodeProcessor(" + mode + "," + System.identityHashCode(this) + ")";
    }

    /**
     * Key use for implementation only (along with descriptor
     * type, otherwise factories eliminate themselves)
     * 
     * @author jwells
     */
    private final static class ImplOnlyKey {
        private final String impl;
        private final DescriptorType type;
        private final int hash;
        
        private ImplOnlyKey(Descriptor desc) {
            impl = desc.getImplementation();
            type = desc.getDescriptorType();
            
            hash = impl.hashCode() ^ type.hashCode();
        }
        
        @Override
        public int hashCode() {
            return hash;
        }
        
        @Override
        public boolean equals(Object o) {
            if (o == null) return false;
            if (!(o instanceof ImplOnlyKey)) return false;
            ImplOnlyKey other = (ImplOnlyKey) o;
            
            return other.impl.equals(impl) && other.type.equals(type);
        }
        
        @Override
        public String toString() {
            return "ImplOnlyKey(" + impl + "," + type + "," + System.identityHashCode(this) + ")";
        }
        
    }
}
