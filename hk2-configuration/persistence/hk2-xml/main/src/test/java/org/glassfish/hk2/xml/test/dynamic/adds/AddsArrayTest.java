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

package org.glassfish.hk2.xml.test.dynamic.adds;

import java.beans.PropertyChangeEvent;
import java.net.URL;
import java.util.List;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.configuration.hub.api.Change;
import org.glassfish.hk2.configuration.hub.api.Change.ChangeCategory;
import org.glassfish.hk2.configuration.hub.api.Hub;
import org.glassfish.hk2.configuration.hub.api.Instance;
import org.glassfish.hk2.configuration.hub.api.Type;
import org.glassfish.hk2.xml.api.XmlHandleTransaction;
import org.glassfish.hk2.xml.api.XmlRootHandle;
import org.glassfish.hk2.xml.api.XmlService;
import org.glassfish.hk2.xml.test.arrays.Employees;
import org.glassfish.hk2.xml.test.basic.beans.Commons;
import org.glassfish.hk2.xml.test.basic.beans.Employee;
import org.glassfish.hk2.xml.test.basic.beans.Financials;
import org.glassfish.hk2.xml.test.basic.beans.OtherData;
import org.glassfish.hk2.xml.test.dynamic.adds.AddsTest.RecordingBeanUpdateListener;
import org.glassfish.hk2.xml.test.utilities.Utilities;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests adding the root and children
 * 
 * @author jwells
 *
 */
public class AddsArrayTest {
    private final static String DAVE = "Dave";
    private final static String EMPLOYEE_TYPE = "/employees/employee";
    private final static String OTHER_DATA_TYPE = "/employees/other-data";
    private final static String DAVE_INSTANCE = "employees.Dave";
    
    private final static String ATT_SYMBOL = "ATT";
    private final static String NASDAQ = "Nasdaq";
    
    private final static long ALICE_ID = 12L;
    private final static long BOB_ID = 14L;
    private final static long CAROL_ID = 16L;
    
    private final static String DATA1 = "Spiner";
    private final static String DATA2 = "10100101";  // A5
    
    /**
     * Tests that we can call createAndAdd successfully on a root with no required elements
     */
    @Test // @org.junit.Ignore
    public void testCreateAndAdd() {
        ServiceLocator locator = Utilities.createLocator();
        XmlService xmlService = locator.getService(XmlService.class);
        
        XmlRootHandle<Employees> rootHandle = xmlService.createEmptyHandle(Employees.class);
        Assert.assertNull(rootHandle.getRoot());
        
        rootHandle.addRoot();
        Employees root = rootHandle.getRoot();
        
        Assert.assertNotNull(root);
        Assert.assertNull(root.getFinancials());
        Assert.assertEquals(0, root.getEmployees().length);
        Assert.assertNull(root.getCompanyName());
    }
    
    private void addToExistingTree(ServiceLocator locator, Hub hub, XmlRootHandle<Employees> rootHandle, boolean inRegistry, boolean inHub) {
        Employees employees = rootHandle.getRoot();
        
        employees.addEmployee(DAVE);
        
        Employee daveDirect = employees.lookupEmployee(DAVE);
        Assert.assertNotNull(daveDirect);
        
        if (inRegistry) {
            Employee daveService = locator.getService(Employee.class, DAVE);
            Assert.assertNotNull(daveService);
        }
        else {
            Assert.assertNull(locator.getService(Employee.class, DAVE));
        }
        
        if (inHub) {
            Assert.assertNotNull(hub.getCurrentDatabase().getInstance(EMPLOYEE_TYPE, DAVE_INSTANCE));
        }
        else {
            Assert.assertNull(hub.getCurrentDatabase().getInstance(EMPLOYEE_TYPE, DAVE_INSTANCE));
        }
    }
    
    /**
     * Tests that we can add to an existing tree with just a basic add (no copy or overlay)
     */
    @Test // @org.junit.Ignore
    public void testAddToExistingTree() throws Exception {
        ServiceLocator locator = Utilities.createLocator();
        XmlService xmlService = locator.getService(XmlService.class);
        Hub hub = locator.getService(Hub.class);
        
        URL url = getClass().getClassLoader().getResource(Commons.ACME1_FILE);
        
        XmlRootHandle<Employees> rootHandle = xmlService.unmarshal(url.toURI(), Employees.class);
        
        addToExistingTree(locator, hub, rootHandle, true, true);
    }
    
    /**
     * Tests that we can add to an existing tree with just a basic add (no copy or overlay) not in Hub
     * @throws Exception
     */
    @Test // @org.junit.Ignore
    public void testAddToExistingTreeNoHub() throws Exception {
        ServiceLocator locator = Utilities.createLocator();
        XmlService xmlService = locator.getService(XmlService.class);
        Hub hub = locator.getService(Hub.class);
        
        URL url = getClass().getClassLoader().getResource(Commons.ACME1_FILE);
        
        XmlRootHandle<Employees> rootHandle = xmlService.unmarshal(url.toURI(), Employees.class, true, false);
        
        addToExistingTree(locator, hub, rootHandle, true, false);
    }
    
    /**
     * Tests that we can add to an existing tree with just a basic add (no copy or overlay) not in ServiceLocator
     * @throws Exception
     */
    @Test // @org.junit.Ignore
    public void testAddToExistingTreeNoHk2Service() throws Exception {
        ServiceLocator locator = Utilities.createLocator();
        XmlService xmlService = locator.getService(XmlService.class);
        Hub hub = locator.getService(Hub.class);
        
        URL url = getClass().getClassLoader().getResource(Commons.ACME1_FILE);
        
        XmlRootHandle<Employees> rootHandle = xmlService.unmarshal(url.toURI(), Employees.class, false, true);
        
        addToExistingTree(locator, hub, rootHandle, false, true);
    }
    
    /**
     * Tests that we can add to an existing tree with just a basic add (no copy or overlay) not in ServiceLocator
     * or Hub
     * @throws Exception
     */
    @Test // @org.junit.Ignore
    public void testAddToExistingTreeNoHk2ServiceOrHub() throws Exception {
        ServiceLocator locator = Utilities.createLocator();
        XmlService xmlService = locator.getService(XmlService.class);
        Hub hub = locator.getService(Hub.class);
        
        URL url = getClass().getClassLoader().getResource(Commons.ACME1_FILE);
        
        XmlRootHandle<Employees> rootHandle = xmlService.unmarshal(url.toURI(), Employees.class, false, false);
        
        addToExistingTree(locator, hub, rootHandle, false, false);
    }
    
    
    
    /**
     * Tests that we can add to an existing tree with just a basic add
     * with an unkeyed field
     */
    @Test // @org.junit.Ignore
    public void testAddToExistingTreeUnKeyed() throws Exception {
        ServiceLocator locator = Utilities.createLocator();
        XmlService xmlService = locator.getService(XmlService.class);
        Hub hub = locator.getService(Hub.class);
        
        URL url = getClass().getClassLoader().getResource(Commons.ACME1_FILE);
        
        XmlRootHandle<Employees> rootHandle = xmlService.unmarshal(url.toURI(), Employees.class);
        Employees employees = rootHandle.getRoot();
        
        employees.addOtherData(0);
        
        OtherData found = null;
        for (OtherData other : employees.getOtherData()) {
            Assert.assertNull(found);
            found = other;
        }
        
        Assert.assertNotNull(found);
        
        OtherData otherService = locator.getService(OtherData.class);
        Assert.assertNotNull(otherService);
        
        Assert.assertEquals(found, otherService);
        
        Type type = hub.getCurrentDatabase().getType(OTHER_DATA_TYPE);
        
        Instance foundInstance = null;
        for (Instance i : type.getInstances().values()) {
            Assert.assertNull(foundInstance);
            foundInstance = i;
        }
        
        Assert.assertNotNull(foundInstance);
    }
    
    /**
     * Tests that we can add to an existing tree with just a basic add
     * with an direct stanza
     */
    @Test // @org.junit.Ignore
    public void testAddToExistingTreeDirect() throws Exception {
        ServiceLocator locator = Utilities.createLocator();
        XmlService xmlService = locator.getService(XmlService.class);
        Hub hub = locator.getService(Hub.class);
        
        URL url = getClass().getClassLoader().getResource(Commons.ACME2_FILE);
        
        XmlRootHandle<Employees> rootHandle = xmlService.unmarshal(url.toURI(), Employees.class);
        Employees employees = rootHandle.getRoot();
        
        Assert.assertNull(employees.getFinancials());
        
        employees.addFinancials();
        
        Financials financials = employees.getFinancials();
        
        Assert.assertNotNull(financials);
        Assert.assertNull(financials.getExchange());
        Assert.assertNull(financials.getSymbol());
        
        Assert.assertNotNull(hub.getCurrentDatabase().getInstance(Commons.FINANCIALS_TYPE, Commons.FINANCIALS_INSTANCE));
    }
    
    private static Employee createEmployee(XmlService xmlService, String name, long id) {
        Employee employee = xmlService.createBean(Employee.class);
        
        employee.setName(name);
        employee.setId(id);
        
        return employee;
    }
    
    private static OtherData createOtherData(XmlService xmlService, String data) {
        OtherData other = xmlService.createBean(OtherData.class);
        
        other.setData(data);
        
        return other;
    }
    
    private static void checkEmployee(Employee employee, String name, long id) {
        Assert.assertNotNull(employee);
        Assert.assertEquals(name, employee.getName());
        Assert.assertEquals(id, employee.getId());
    }
    
    private static void checkOtherData(OtherData other, String data) {
        Assert.assertNotNull(other);
        Assert.assertEquals(data, other.getData());
    }
    
    private static void checkFinancials(Financials fin, String exchange, String symbol) {
        Assert.assertNotNull(fin);
        Assert.assertEquals(exchange, fin.getExchange());
        Assert.assertEquals(symbol, fin.getSymbol());
    }

    /**
     * Creates an entire tree unassociated with a root then sets it as
     * the root
     */
    @Test // @org.junit.Ignore
    public void testAddOneLevelComplexRoot() {
        ServiceLocator locator = Utilities.createLocator();
        XmlService xmlService = locator.getService(XmlService.class);
        
        XmlRootHandle<Employees> rootHandle = xmlService.createEmptyHandle(Employees.class);
        Assert.assertNull(rootHandle.getRoot());
        
        Employees employees = xmlService.createBean(Employees.class);
        Financials financials = xmlService.createBean(Financials.class);
        
        financials.setExchange(NASDAQ);
        financials.setSymbol(ATT_SYMBOL);
        
        employees.setFinancials(financials);
        
        Employee alice = createEmployee(xmlService, Commons.ALICE, ALICE_ID);
        Employee bob = createEmployee(xmlService, Commons.BOB, BOB_ID);
        Employee carol = createEmployee(xmlService, Commons.CAROL, CAROL_ID);
        
        employees.addEmployee(alice);
        employees.addEmployee(carol);
        employees.addEmployee(bob, 1);
        
        OtherData data1 = createOtherData(xmlService, DATA1);
        OtherData data2 = createOtherData(xmlService, DATA2);
        
        employees.addOtherData(data2);
        employees.addOtherData(data1, 0);
        
        rootHandle.addRoot(employees);
        
        Employees root = rootHandle.getRoot();
        
        Assert.assertNotNull(root);
        
        checkFinancials(root.getFinancials(), NASDAQ, ATT_SYMBOL);
        
        checkEmployee(root.getEmployees()[0], Commons.ALICE, ALICE_ID);
        checkEmployee(root.getEmployees()[1], Commons.BOB, BOB_ID);
        checkEmployee(root.getEmployees()[2], Commons.CAROL, CAROL_ID);
        
        checkOtherData(root.getOtherData()[0], DATA1);
        checkOtherData(root.getOtherData()[1], DATA2);
        
        checkEmployee(locator.getService(Employee.class, Commons.ALICE), Commons.ALICE, ALICE_ID);
        checkEmployee(locator.getService(Employee.class, Commons.BOB), Commons.BOB, BOB_ID);
        checkEmployee(locator.getService(Employee.class, Commons.CAROL), Commons.CAROL, CAROL_ID);
        
        int lcv = 0;
        for (OtherData other : locator.getAllServices(OtherData.class)) {
            if (lcv == 0) {
                checkOtherData(other, DATA1);
            }
            else if (lcv == 1){
                checkOtherData(other, DATA2);
            }
            else {
                Assert.fail("Too many OtherData");
            }
            lcv++;
        }
        
        Assert.assertEquals(2, lcv);
        
        checkFinancials(locator.getService(Financials.class), NASDAQ, ATT_SYMBOL);
    }
    
    /**
     * Tests that we can add to an existing tree and do adds and removes of the
     * same bean in a single transaction
     */
    @Test
    // @org.junit.Ignore
    public void testAddRemoveAddRemoveInOneTransaction() throws Exception {
        ServiceLocator locator = Utilities.createLocator(RecordingBeanUpdateListener.class);
        XmlService xmlService = locator.getService(XmlService.class);
        
        URL url = getClass().getClassLoader().getResource(Commons.ACME1_FILE);
        
        XmlRootHandle<Employees> rootHandle = xmlService.unmarshal(url.toURI(), Employees.class);
        Employees employees = rootHandle.getRoot();
        
        boolean success = false;
        XmlHandleTransaction<Employees> transaction = rootHandle.lockForTransaction();
        try {
            employees.removeEmployee(Commons.DAVE);
            employees.addEmployee(Commons.DAVE);
            employees.removeEmployee(Commons.DAVE);
            employees.addEmployee(Commons.DAVE);
            employees.removeEmployee(Commons.DAVE);
            
            success = true;
        }
        finally {
            if (success) {
                transaction.commit();
            }
            else {
                transaction.abandon();
            }
        }
        
        Assert.assertNull(employees.lookupEmployee(Commons.DAVE));
        
        RecordingBeanUpdateListener listener = locator.getService(RecordingBeanUpdateListener.class);
        Assert.assertNotNull(listener);
        
        List<Change> committed = listener.latestCommit;
        
        Assert.assertEquals(8, committed.size());
        
        for (int lcv = 0; lcv < committed.size(); lcv++) {
            Change currentChange = committed.get(lcv);
            
            if (lcv == 0 || lcv == 4) {
                Assert.assertEquals(ChangeCategory.ADD_INSTANCE, currentChange.getChangeCategory());
                Assert.assertEquals(Commons.EMPLOYEE_TYPE, currentChange.getChangeType().getName());
                Assert.assertEquals(Commons.DAVE_EMPLOYEE_INSTANCE, currentChange.getInstanceKey());
            }
            else if (lcv == 1 || lcv == 2 || lcv == 5 || lcv == 6) {
                // First modify is for the add, second is for the remove.  They properly happen
                // in the opposite order (the add gets the MODIFY *after* the add change event has
                // happened, while the remove gets the MODIFY event *before* the remove change event
                // has happened
                Assert.assertEquals(ChangeCategory.MODIFY_INSTANCE, currentChange.getChangeCategory());
                Assert.assertEquals(Commons.EMPLOYEES_TYPE, currentChange.getChangeType().getName());
                Assert.assertEquals(Commons.EMPLOYEES_INSTANCE_NAME, currentChange.getInstanceKey());
                List<PropertyChangeEvent> changed = currentChange.getModifiedProperties();
                Assert.assertEquals(1, changed.size());
                
                Assert.assertEquals(Commons.EMPLOYEE_TAG, changed.get(0).getPropertyName());
            }
            else if (lcv == 3 || lcv == 7) {
                Assert.assertEquals(ChangeCategory.REMOVE_INSTANCE, currentChange.getChangeCategory());
                Assert.assertEquals(Commons.EMPLOYEE_TYPE, currentChange.getChangeType().getName());
                Assert.assertEquals(Commons.DAVE_EMPLOYEE_INSTANCE, currentChange.getInstanceKey());
            }
            else {
                Assert.fail("Too many entries? " + lcv);
            }
        }
    }
}
