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

package org.glassfish.hk2.tests.locator.locator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.DescriptorType;
import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.HK2Loader;
import org.glassfish.hk2.api.PerLookup;

/**
 * @author jwells
 *
 */
public class ForeignDescriptor implements Descriptor {
    private String implementation;
    private final Set<String> advertisedContracts = new HashSet<String>();
    private Set<String> qualifiers = new HashSet<String>();
    private Map<String, List<String>> metadata =
            new HashMap<String, List<String>>();
    private int rank;
    
    /**
     * Setter for implementation
     * @param implementation
     */
    public void setImplementation(String implementation) {
        this.implementation = implementation;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Descriptor#getImplementation()
     */
    @Override
    public String getImplementation() {
        return implementation;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Descriptor#getAdvertisedContracts()
     */
    @Override
    public Set<String> getAdvertisedContracts() {
        return advertisedContracts;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Descriptor#getScope()
     */
    @Override
    public String getScope() {
        return PerLookup.class.getName();
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Descriptor#getName()
     */
    @Override
    public String getName() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Descriptor#getQualifiers()
     */
    @Override
    public Set<String> getQualifiers() {
        return qualifiers;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Descriptor#getDescriptorType()
     */
    @Override
    public DescriptorType getDescriptorType() {
        return DescriptorType.CLASS;
    }
    
    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Descriptor#getDescriptorVisibility()
     */
    @Override
    public DescriptorVisibility getDescriptorVisibility() {
        return DescriptorVisibility.NORMAL;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Descriptor#getMetadata()
     */
    @Override
    public Map<String, List<String>> getMetadata() {
        return metadata;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Descriptor#getLoader()
     */
    @Override
    public HK2Loader getLoader() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Descriptor#getRanking()
     */
    @Override
    public int getRanking() {
        return rank;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Descriptor#setRanking(int)
     */
    @Override
    public int setRanking(int ranking) {
        int retVal = rank;
        rank = ranking;
        return retVal;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Descriptor#getServiceId()
     */
    @Override
    public Long getServiceId() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Descriptor#getLocatorId()
     */
    @Override
    public Long getLocatorId() {
        return null;
    }

    @Override
    public Boolean isProxiable() {
        return null;
    }

    @Override
    public String getClassAnalysisName() {
        return null;
    }

    @Override
    public Boolean isProxyForSameScope() {
        return null;
    }

}
