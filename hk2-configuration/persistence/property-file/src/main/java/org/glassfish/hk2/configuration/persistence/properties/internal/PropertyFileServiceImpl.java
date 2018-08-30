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

package org.glassfish.hk2.configuration.persistence.properties.internal;

import java.beans.PropertyChangeEvent;

import javax.inject.Inject;

import org.glassfish.hk2.configuration.hub.api.Hub;
import org.glassfish.hk2.configuration.hub.api.Instance;
import org.glassfish.hk2.configuration.hub.api.WriteableBeanDatabase;
import org.glassfish.hk2.configuration.hub.api.WriteableType;
import org.glassfish.hk2.configuration.persistence.properties.PropertyFileBean;
import org.glassfish.hk2.configuration.persistence.properties.PropertyFileHandle;
import org.glassfish.hk2.configuration.persistence.properties.PropertyFileService;
import org.jvnet.hk2.annotations.Service;

/**
 * @author jwells
 *
 */
@Service
public class PropertyFileServiceImpl implements PropertyFileService {
    private final static int MAX_TRIES = 10000;
    
    @Inject
    private Hub hub;

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.persistence.properties.PropertyFileService#createPropertyHandleOfSpecificType(java.lang.String, java.lang.String)
     */
    @Override
    public PropertyFileHandle createPropertyHandleOfSpecificType(
            String specificTypeName, String defaultInstanceName) {
        return new PropertyFileHandleImpl(specificTypeName, null, defaultInstanceName, hub);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.persistence.properties.PropertyFileService#createPropertyHandleOfSpecificType(java.lang.String)
     */
    @Override
    public PropertyFileHandle createPropertyHandleOfSpecificType(
            String specificTypeName) {
        return new PropertyFileHandleImpl(specificTypeName, null, null, hub);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.persistence.properties.PropertyFileService#createPropertyHandleOfAnyType(java.lang.String, java.lang.String)
     */
    @Override
    public PropertyFileHandle createPropertyHandleOfAnyType(
            String defaultTypeName, String defaultInstanceName) {
        return new PropertyFileHandleImpl(null, defaultTypeName, defaultInstanceName, hub);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.persistence.properties.PropertyFileService#createPropertyHandleOfAnyType()
     */
    @Override
    public PropertyFileHandle createPropertyHandleOfAnyType() {
        return new PropertyFileHandleImpl(null, null, null, hub);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.persistence.properties.PropertyFileService#addPropertyFileBean(org.glassfish.hk2.configuration.persistence.properties.PropertyFileBean)
     */
    @Override
    public void addPropertyFileBean(PropertyFileBean propertyFileBean) {
        boolean success = false;
        for (int lcv = 0; lcv < MAX_TRIES; lcv++) {
            WriteableBeanDatabase wbd = hub.getWriteableDatabaseCopy();
            
            WriteableType wt = wbd.findOrAddWriteableType(PropertyFileBean.TYPE_NAME);
            
            Instance oldInstance = wt.getInstance(PropertyFileBean.INSTANCE_NAME);
            if (oldInstance != null) {
                PropertyFileBean oldBean = (PropertyFileBean) oldInstance.getBean();
                wt.modifyInstance(PropertyFileBean.INSTANCE_NAME, propertyFileBean,
                        new PropertyChangeEvent(propertyFileBean,
                                "typeMapping",
                                oldBean.getTypeMapping(),
                                propertyFileBean.getTypeMapping()));
            }
            else {
                wt.addInstance(PropertyFileBean.INSTANCE_NAME, propertyFileBean);
            }
            
            try {
                wbd.commit();
                success = true;
                break;
            }
            catch (IllegalStateException ise) {
                // try again
            }
        }
        
        if (!success) {
            throw new IllegalStateException("Could not update hub with propertyFileBean " + propertyFileBean);
        }
    }
    
    @Override
    public void removePropertyFileBean() {
        boolean success = false;
        for (int lcv = 0; lcv < MAX_TRIES; lcv++) {
            WriteableBeanDatabase wbd = hub.getWriteableDatabaseCopy();
            
            wbd.removeType(PropertyFileBean.TYPE_NAME);
            
            try {
                wbd.commit();
                success = true;
                break;
            }
            catch (IllegalStateException ise) {
                // try again
            }
        }
        
        if (!success) {
            throw new IllegalStateException("Could not update hub to remove the propertyFileBean");
        }
    }

}
