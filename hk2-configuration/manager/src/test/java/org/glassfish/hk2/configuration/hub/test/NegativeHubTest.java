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

package org.glassfish.hk2.configuration.hub.test;

import java.beans.PropertyChangeEvent;
import java.util.HashMap;

import org.glassfish.hk2.configuration.hub.api.Hub;
import org.glassfish.hk2.configuration.hub.api.ManagerUtilities;
import org.glassfish.hk2.configuration.hub.api.WriteableBeanDatabase;
import org.glassfish.hk2.configuration.hub.api.WriteableType;
import org.junit.Before;
import org.junit.Test;
import org.jvnet.hk2.testing.junit.HK2Runner;

/**
 * Some Hub error case tests
 * @author jwells
 *
 */
public class NegativeHubTest extends HK2Runner {
    private final static String ERROR_TYPE = "ErrorType";
    private final static String ERROR_NAME = "ErrorName";
    private Hub hub;
    
    @Before
    public void before() {
        super.before();
        
        // This is necessary to make running in an IDE easier
        ManagerUtilities.enableConfigurationHub(testLocator);
        
        this.hub = testLocator.getService(Hub.class);
    }
    
    /**
     * Null in addType is a fail
     */
    @Test(expected=IllegalArgumentException.class)
    public void testNullTypeInAdd() {
        WriteableBeanDatabase wbd = hub.getWriteableDatabaseCopy();
        wbd.addType(null);
    }
    
    /**
     * Null in removeType is a fail
     */
    @Test(expected=IllegalArgumentException.class)
    public void testNullTypeInRemove() {
        WriteableBeanDatabase wbd = hub.getWriteableDatabaseCopy();
        wbd.removeType(null);
    }
    
    /**
     * Null in findOrAddType is a fail
     */
    @Test(expected=IllegalArgumentException.class)
    public void testNullTypeInFindOrAdd() {
        WriteableBeanDatabase wbd = hub.getWriteableDatabaseCopy();
        wbd.findOrAddWriteableType(null);
    }
    
    /**
     * Use addType after commit
     */
    @Test(expected=IllegalStateException.class)
    public void testInvalidStatePostCommitAdd() {
        WriteableBeanDatabase wbd = hub.getWriteableDatabaseCopy();
        wbd.commit();
        
        wbd.addType(ERROR_TYPE);
    }
    
    /**
     * Use addType after commit
     */
    @Test(expected=IllegalStateException.class)
    public void testInvalidStatePostCommitRemove() {
        WriteableBeanDatabase wbd = hub.getWriteableDatabaseCopy();
        wbd.commit();
        
        wbd.removeType(ERROR_TYPE);
    }
    
    /**
     * Use addType after commit
     */
    @Test(expected=IllegalStateException.class)
    public void testInvalidStatePostCommitFind() {
        WriteableBeanDatabase wbd = hub.getWriteableDatabaseCopy();
        wbd.commit();
        
        wbd.findOrAddWriteableType(ERROR_TYPE);
    }
    
    /**
     * Try to commit twice
     */
    @Test(expected=IllegalStateException.class)
    public void testDoubleCommit() {
        WriteableBeanDatabase wbd = hub.getWriteableDatabaseCopy();
        wbd.commit();
        
        wbd.commit();
    }
    
    /**
     * Null as name for addInstance
     */
    @Test(expected=IllegalArgumentException.class)
    public void testAddInstanceWithNullKey() {
        WriteableBeanDatabase wbd = hub.getWriteableDatabaseCopy();
        WriteableType wt = wbd.addType(ERROR_TYPE);
        wt.addInstance(null, new HashMap<String, Object>());
    }
    
    /**
     * Null as bean for addInstance
     */
    @Test(expected=IllegalArgumentException.class)
    public void testAddInstanceWithNullValue() {
        WriteableBeanDatabase wbd = hub.getWriteableDatabaseCopy();
        WriteableType wt = wbd.addType(ERROR_TYPE);
        wt.addInstance(ERROR_NAME, null);
    }
    
    /**
     * Null as key and bean for addInstance
     */
    @Test(expected=IllegalArgumentException.class)
    public void testAddInstanceWithNullBoth() {
        WriteableBeanDatabase wbd = hub.getWriteableDatabaseCopy();
        WriteableType wt = wbd.addType(ERROR_TYPE);
        wt.addInstance(null, null);
    }
    
    /**
     * Null as name for addInstance
     */
    @Test(expected=IllegalArgumentException.class)
    public void testRemoveInstanceWithNullKey() {
        WriteableBeanDatabase wbd = hub.getWriteableDatabaseCopy();
        WriteableType wt = wbd.addType(ERROR_TYPE);
        wt.removeInstance(null);
    }
    
    /**
     * Null as name for modifyInstance
     */
    @Test(expected=IllegalArgumentException.class)
    public void testModifyInstanceWithNullKey() {
        WriteableBeanDatabase wbd = hub.getWriteableDatabaseCopy();
        WriteableType wt = wbd.addType(ERROR_TYPE);
        wt.modifyInstance(null, new HashMap<String, Object>());
    }
    
    /**
     * Null as bean for modifyInstance
     */
    @Test(expected=IllegalArgumentException.class)
    public void testModifyInstanceWithNullValue() {
        WriteableBeanDatabase wbd = hub.getWriteableDatabaseCopy();
        WriteableType wt = wbd.addType(ERROR_TYPE);
        wt.modifyInstance(ERROR_NAME, null);
    }
    
    /**
     * Null as key and bean for modifyInstance
     */
    @Test(expected=IllegalArgumentException.class)
    public void testModifyInstanceWithNullBoth() {
        WriteableBeanDatabase wbd = hub.getWriteableDatabaseCopy();
        WriteableType wt = wbd.addType(ERROR_TYPE);
        wt.modifyInstance(null, null);
    }
    
    /**
     * Attempt to modify an instance that does not exist
     */
    @Test(expected=IllegalStateException.class)
    public void testModifyInstanceThatDoesNotExist() {
        WriteableBeanDatabase wbd = hub.getWriteableDatabaseCopy();
        WriteableType wt = wbd.addType(ERROR_TYPE);
        HashMap<String, Object> bean = new HashMap<String, Object>();
        
        wt.modifyInstance(ERROR_NAME, bean,
                new PropertyChangeEvent(bean, ERROR_NAME, "", ""));
    }

}
