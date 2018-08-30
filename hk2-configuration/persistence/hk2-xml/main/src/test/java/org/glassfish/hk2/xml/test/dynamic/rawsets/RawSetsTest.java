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

package org.glassfish.hk2.xml.test.dynamic.rawsets;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.configuration.hub.api.BeanDatabase;
import org.glassfish.hk2.configuration.hub.api.Change;
import org.glassfish.hk2.configuration.hub.api.Change.ChangeCategory;
import org.glassfish.hk2.configuration.hub.api.Hub;
import org.glassfish.hk2.configuration.hub.api.Instance;
import org.glassfish.hk2.xml.api.XmlHk2ConfigurationBean;
import org.glassfish.hk2.xml.api.XmlRootHandle;
import org.glassfish.hk2.xml.api.XmlService;
import org.glassfish.hk2.xml.test.basic.beans.Commons;
import org.glassfish.hk2.xml.test.basic.beans.Museum;
import org.glassfish.hk2.xml.test.beans.AuthorizationProviderBean;
import org.glassfish.hk2.xml.test.beans.DiagnosticsBean;
import org.glassfish.hk2.xml.test.beans.DomainBean;
import org.glassfish.hk2.xml.test.beans.JMSServerBean;
import org.glassfish.hk2.xml.test.beans.MachineBean;
import org.glassfish.hk2.xml.test.beans.SSLManagerBean;
import org.glassfish.hk2.xml.test.beans.SSLManagerBeanCustomizer;
import org.glassfish.hk2.xml.test.beans.SecurityManagerBean;
import org.glassfish.hk2.xml.test.dynamic.merge.MergeTest;
import org.glassfish.hk2.xml.test.dynamic.overlay.ChangeDescriptor;
import org.glassfish.hk2.xml.test.dynamic.overlay.OverlayUtilities;
import org.glassfish.hk2.xml.test.utilities.Utilities;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class RawSetsTest {
    public final static String MUSEUM2_FILE = "museum2.xml";
    
    public final static String MUSEUM_TYPE = "/museum";
    public final static String MUSEUM_INSTANCE = "museum";
    
    private final static String JMS_SERVER_PROPERTY = "jms-server";
    private final static String MACHINE_PROPERTY = "machine";
    private final static String AUTHORIZATION_PROVIDER_PROPERTY = "authorization-provider";
    
    public final static String AGE_TAG = "age";
    
    public final static int ONE_OH_ONE_INT = 101;
    
    /**
     * Just verifies that the original state of the Museum
     * object from the file is as expected
     */
    @SuppressWarnings("unchecked")
    public static void verifyPreState(XmlRootHandle<Museum> rootHandle, Hub hub) {
        Museum museum = rootHandle.getRoot();
        
        Assert.assertEquals(Commons.HUNDRED_INT, museum.getId());
        Assert.assertEquals(Commons.BEN_FRANKLIN, museum.getName());
        Assert.assertEquals(Commons.HUNDRED_TEN_INT, museum.getAge());
        
        Instance instance = hub.getCurrentDatabase().getInstance(MUSEUM_TYPE, MUSEUM_INSTANCE);
        Map<String, Object> beanLikeMap = (Map<String, Object>) instance.getBean();
        
        Assert.assertEquals(Commons.BEN_FRANKLIN, beanLikeMap.get(Commons.NAME_TAG));
        Assert.assertEquals(Commons.HUNDRED_INT, beanLikeMap.get(Commons.ID_TAG));
        Assert.assertEquals(Commons.HUNDRED_TEN_INT, beanLikeMap.get(AGE_TAG));
    }
    
    /**
     * Tests that single fields can be modified
     * 
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @Test
    // @org.junit.Ignore
    public void testModifySingleProperty() throws Exception {
        ServiceLocator locator = Utilities.createLocator(UpdateListener.class);
        XmlService xmlService = locator.getService(XmlService.class);
        Hub hub = locator.getService(Hub.class);
        UpdateListener listener = locator.getService(UpdateListener.class);
        
        URL url = getClass().getClassLoader().getResource(Commons.MUSEUM1_FILE);
        
        XmlRootHandle<Museum> rootHandle = xmlService.unmarshal(url.toURI(), Museum.class);
        
        verifyPreState(rootHandle, hub);
        
        Museum museum = rootHandle.getRoot();
        
        // All above just verifying the pre-state
        museum.setAge(ONE_OH_ONE_INT);  // getting younger?
        
        Assert.assertEquals(Commons.HUNDRED_INT, museum.getId());
        Assert.assertEquals(Commons.BEN_FRANKLIN, museum.getName());
        Assert.assertEquals(ONE_OH_ONE_INT, museum.getAge());
        
        Instance instance = hub.getCurrentDatabase().getInstance(MUSEUM_TYPE, MUSEUM_INSTANCE);
        Map<String, Object> beanLikeMap = (Map<String, Object>) instance.getBean();
        
        Assert.assertEquals(Commons.BEN_FRANKLIN, beanLikeMap.get(Commons.NAME_TAG));
        Assert.assertEquals(Commons.HUNDRED_INT, beanLikeMap.get(Commons.ID_TAG));
        Assert.assertEquals(ONE_OH_ONE_INT, beanLikeMap.get(AGE_TAG));  // The test
        
        List<Change> changes = listener.getChanges();
        Assert.assertNotNull(changes);
        
        Assert.assertEquals(1, changes.size());
        
        for (Change change : changes) {
            Assert.assertEquals(ChangeCategory.MODIFY_INSTANCE, change.getChangeCategory());
        }
    }
    
    /**
     * Tests that a direct type can be added via set and then used dynamically
     * 
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @Test
    // @org.junit.Ignore
    public void testAddDirectTypeWithSet() throws Exception {
        ServiceLocator locator = Utilities.createLocator(UpdateListener.class,
                SSLManagerBeanCustomizer.class);
        XmlService xmlService = locator.getService(XmlService.class);
        Hub hub = locator.getService(Hub.class);
        
        URL url = getClass().getClassLoader().getResource(MergeTest.DOMAIN1_FILE);
        
        XmlRootHandle<DomainBean> rootHandle = xmlService.unmarshal(url.toURI(), DomainBean.class);
        
        MergeTest.verifyDomain1Xml(rootHandle, hub, locator);
        
        DomainBean domain = rootHandle.getRoot();
        SecurityManagerBean securityManager = domain.getSecurityManager();
        
        SSLManagerBean sslManager = xmlService.createBean(SSLManagerBean.class);
        
        securityManager.setSSLManager(sslManager);
        
        sslManager = securityManager.getSSLManager();
        
        Assert.assertEquals(securityManager, ((XmlHk2ConfigurationBean) sslManager)._getParent());
        Assert.assertEquals(SSLManagerBean.FORT_KNOX, sslManager.getSSLPrivateKeyLocation());
        
        Assert.assertEquals(sslManager, locator.getService(SSLManagerBean.class));
        
        Instance instance = hub.getCurrentDatabase().getInstance(MergeTest.SSL_MANAGER_TYPE, MergeTest.SSL_MANAGER_INSTANCE_NAME);
        Map<String, Object> beanLikeMap = (Map<String, Object>) instance.getBean();
        
        Assert.assertNotNull(beanLikeMap);
        Assert.assertTrue(beanLikeMap.isEmpty());
    }
    
    /**
     * Tests that a direct type can be removed via set
     * 
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testRemoveDirectTypeWithSet() throws Exception {
        ServiceLocator locator = Utilities.createLocator(UpdateListener.class,
                SSLManagerBeanCustomizer.class);
        XmlService xmlService = locator.getService(XmlService.class);
        Hub hub = locator.getService(Hub.class);
        
        URL url = getClass().getClassLoader().getResource(MergeTest.DOMAIN1_FILE);
        
        XmlRootHandle<DomainBean> rootHandle = xmlService.unmarshal(url.toURI(), DomainBean.class);
        
        MergeTest.verifyDomain1Xml(rootHandle, hub, locator);
        
        DomainBean domain = rootHandle.getRoot();
        
        domain.setSecurityManager(null);
        
        Assert.assertNull(domain.getSecurityManager());
        Assert.assertNull(locator.getService(SecurityManagerBean.class));
        
        Assert.assertNull(locator.getService(AuthorizationProviderBean.class));
        
        Assert.assertNull(hub.getCurrentDatabase().getInstance(MergeTest.SECURITY_MANAGER_TYPE, MergeTest.SECURITY_MANAGER_INSTANCE));
        Assert.assertNull(hub.getCurrentDatabase().getInstance(MergeTest.AUTHORIZATION_PROVIDER_TYPE, MergeTest.RSA_ATZ_PROV_NAME));
    }
    
    /**
     * Tests that setting null back to null works
     * 
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testNullToNull() throws Exception {
        ServiceLocator locator = Utilities.createLocator(UpdateListener.class,
                SSLManagerBeanCustomizer.class);
        XmlService xmlService = locator.getService(XmlService.class);
        Hub hub = locator.getService(Hub.class);
        
        URL url = getClass().getClassLoader().getResource(MergeTest.DOMAIN1_FILE);
        
        XmlRootHandle<DomainBean> rootHandle = xmlService.unmarshal(url.toURI(), DomainBean.class);
        
        MergeTest.verifyDomain1Xml(rootHandle, hub, locator);
        
        DomainBean domain = rootHandle.getRoot();
        SecurityManagerBean securityManagerBean = domain.getSecurityManager();
        
        // Null to null
        securityManagerBean.setSSLManager(null);
        
        // Verify nothing was changed
        MergeTest.verifyDomain1Xml(rootHandle, hub, locator);
    }
    
    /**
     * Tests that setting a bean to itself is ok (one
     * case of set to set that works)
     * 
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testSameToSame() throws Exception {
        ServiceLocator locator = Utilities.createLocator(UpdateListener.class,
                SSLManagerBeanCustomizer.class);
        XmlService xmlService = locator.getService(XmlService.class);
        Hub hub = locator.getService(Hub.class);
        
        URL url = getClass().getClassLoader().getResource(MergeTest.DOMAIN1_FILE);
        
        XmlRootHandle<DomainBean> rootHandle = xmlService.unmarshal(url.toURI(), DomainBean.class);
        
        MergeTest.verifyDomain1Xml(rootHandle, hub, locator);
        
        DomainBean domain = rootHandle.getRoot();
        SecurityManagerBean securityManagerBean = domain.getSecurityManager();
        
        // Setting it back to itself
        domain.setSecurityManager(securityManagerBean);
        
        // Verify nothing was changed
        MergeTest.verifyDomain1Xml(rootHandle, hub, locator);
    }
    
    /**
     * Tests that setting a bean to itself is ok (one
     * case of set to set that works)
     * 
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testArrayListSwap() throws Exception {
        ServiceLocator locator = Utilities.createLocator(UpdateListener.class,
                SSLManagerBeanCustomizer.class);
        XmlService xmlService = locator.getService(XmlService.class);
        Hub hub = locator.getService(Hub.class);
        
        URL url = getClass().getClassLoader().getResource(MergeTest.DOMAIN1_FILE);
        
        XmlRootHandle<DomainBean> rootHandle = xmlService.unmarshal(url.toURI(), DomainBean.class);
        DomainBean domain = rootHandle.getRoot();
        
        MergeTest.verifyDomain1Xml(rootHandle, hub, locator);
        
        JMSServerBean newBeans[] = new JMSServerBean[2];
        JMSServerBean oldBeans[] = domain.getJMSServers();
        
        // Swap zero and one index
        newBeans[0] = oldBeans[1];
        newBeans[1] = oldBeans[0];
        
        domain.setJMSServers(newBeans);
        
        JMSServerBean changedBeans[] = domain.getJMSServers();
        
        Assert.assertEquals(MergeTest.DAVE_NAME, changedBeans[0].getName());
        Assert.assertEquals(MergeTest.CAROL_NAME, changedBeans[1].getName());
        
        UpdateListener listener = locator.getService(UpdateListener.class);
        
        List<Change> changes = listener.getChanges();
        
        OverlayUtilities.checkChanges(changes,
                new ChangeDescriptor(ChangeCategory.MODIFY_INSTANCE,
                        MergeTest.DOMAIN_TYPE,    // type name
                        MergeTest.DOMAIN_INSTANCE,       // instance name
                        null,
                        JMS_SERVER_PROPERTY) // prop changed
        );
        
        // TODO Need an add and a delete
    }
    
    /**
     * Tests that setting things in a list is fine
     * 
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testListSetModificationsSwap() throws Exception {
        ServiceLocator locator = Utilities.createLocator(UpdateListener.class,
                SSLManagerBeanCustomizer.class);
        XmlService xmlService = locator.getService(XmlService.class);
        Hub hub = locator.getService(Hub.class);
        
        XmlRootHandle<DomainBean> rootHandle = xmlService.createEmptyHandle(DomainBean.class);
        rootHandle.addRoot();
        
        DomainBean domain = rootHandle.getRoot();
        
        MachineBean aliceBean = createMachineBean(xmlService, MergeTest.ALICE_NAME);
        MachineBean bobBean = createMachineBean(xmlService, MergeTest.BOB_NAME);
        MachineBean carolBean = createMachineBean(xmlService, MergeTest.CAROL_NAME);
        
        List<MachineBean> allBeans = new LinkedList<MachineBean>();
        allBeans.add(aliceBean);
        allBeans.add(bobBean);
        allBeans.add(carolBean);
        
        domain.setMachines(allBeans);
        
        // Ensures that modifying the original list doesn't affect anything
        allBeans.clear();
        
        UpdateListener listener = locator.getService(UpdateListener.class);
        
        List<Change> changes = listener.getChanges();
        
        OverlayUtilities.checkChanges(changes,
                new ChangeDescriptor(ChangeCategory.ADD_TYPE,
                        MergeTest.MACHINE_TYPE,    // type name
                        null,       // instance name
                        null) // prop changed
                , new ChangeDescriptor(ChangeCategory.ADD_INSTANCE,
                        MergeTest.MACHINE_TYPE,    // type name
                        MergeTest.ALICE_INSTANCE,       // instance name
                        MergeTest.ALICE_NAME)
                , new ChangeDescriptor(ChangeCategory.ADD_INSTANCE,
                        MergeTest.MACHINE_TYPE,    // type name
                        MergeTest.BOB_INSTANCE,       // instance name
                        MergeTest.BOB_NAME)
                , new ChangeDescriptor(ChangeCategory.ADD_INSTANCE,
                        MergeTest.MACHINE_TYPE,    // type name
                        MergeTest.CAROL_INSTANCE,       // instance name
                        MergeTest.CAROL_NAME)
                , new ChangeDescriptor(ChangeCategory.MODIFY_INSTANCE,
                        MergeTest.DOMAIN_TYPE,    // type name
                        MergeTest.DOMAIN_INSTANCE,       // instance name
                        null,
                        MACHINE_PROPERTY) // prop changed
        );
        
        Assert.assertNotNull(locator.getService(MachineBean.class, MergeTest.ALICE_NAME));
        Assert.assertNotNull(locator.getService(MachineBean.class, MergeTest.BOB_NAME));
        Assert.assertNotNull(locator.getService(MachineBean.class, MergeTest.CAROL_NAME));
        Assert.assertNull(locator.getService(MachineBean.class, MergeTest.DAVE_NAME));
        
        List<MachineBean> fromRootMachines = domain.getMachines();
        Assert.assertEquals(3, fromRootMachines.size());
        Assert.assertEquals(MergeTest.ALICE_NAME, fromRootMachines.get(0).getName());
        Assert.assertEquals(MergeTest.BOB_NAME, fromRootMachines.get(1).getName());
        Assert.assertEquals(MergeTest.CAROL_NAME, fromRootMachines.get(2).getName());
        
        checkHubInstanceWithName(hub, MergeTest.MACHINE_TYPE, MergeTest.ALICE_INSTANCE, MergeTest.ALICE_NAME);
        checkHubInstanceWithName(hub, MergeTest.MACHINE_TYPE, MergeTest.BOB_INSTANCE, MergeTest.BOB_NAME);
        checkHubInstanceWithName(hub, MergeTest.MACHINE_TYPE, MergeTest.CAROL_INSTANCE, MergeTest.CAROL_NAME);
        checkHubNoInstance(hub, MergeTest.MACHINE_TYPE, MergeTest.DAVE_INSTANCE);
        
        MachineBean daveBean = createMachineBean(xmlService, MergeTest.DAVE_NAME);
        
        // This next thing has everything: A move, a delete and an add
        allBeans.add(carolBean);
        allBeans.add(daveBean);
        allBeans.add(aliceBean);
        
        domain.setMachines(allBeans);
        
        Assert.assertNotNull(locator.getService(MachineBean.class, MergeTest.ALICE_NAME));
        Assert.assertNull(locator.getService(MachineBean.class, MergeTest.BOB_NAME));
        Assert.assertNotNull(locator.getService(MachineBean.class, MergeTest.CAROL_NAME));
        Assert.assertNotNull(locator.getService(MachineBean.class, MergeTest.DAVE_NAME));
        
        fromRootMachines = domain.getMachines();
        Assert.assertEquals(3, fromRootMachines.size());
        Assert.assertEquals(MergeTest.CAROL_NAME, fromRootMachines.get(0).getName());
        Assert.assertEquals(MergeTest.DAVE_NAME, fromRootMachines.get(1).getName());
        Assert.assertEquals(MergeTest.ALICE_NAME, fromRootMachines.get(2).getName());
        
        checkHubInstanceWithName(hub, MergeTest.MACHINE_TYPE, MergeTest.ALICE_INSTANCE, MergeTest.ALICE_NAME);
        checkHubNoInstance(hub, MergeTest.MACHINE_TYPE, MergeTest.BOB_INSTANCE);
        checkHubInstanceWithName(hub, MergeTest.MACHINE_TYPE, MergeTest.CAROL_INSTANCE, MergeTest.CAROL_NAME);
        checkHubInstanceWithName(hub, MergeTest.MACHINE_TYPE, MergeTest.DAVE_INSTANCE, MergeTest.DAVE_NAME);
        
        changes = listener.getChanges();
        
        OverlayUtilities.checkChanges(changes,
                new ChangeDescriptor(ChangeCategory.ADD_INSTANCE,
                        MergeTest.MACHINE_TYPE,    // type name
                        MergeTest.DAVE_INSTANCE,       // instance name
                        MergeTest.DAVE_NAME)
                , new ChangeDescriptor(ChangeCategory.REMOVE_INSTANCE,
                        MergeTest.MACHINE_TYPE,    // type name
                        MergeTest.BOB_INSTANCE,       // instance name
                        MergeTest.BOB_NAME)
                , new ChangeDescriptor(ChangeCategory.MODIFY_INSTANCE,
                        MergeTest.DOMAIN_TYPE,    // type name
                        MergeTest.DOMAIN_INSTANCE,       // instance name
                        null,
                        MACHINE_PROPERTY) // prop changed
        );
        
    }
    
    /**
     * Tests that setting things in a direct bean is fine
     * 
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testDirectSetModification() throws Exception {
        ServiceLocator locator = Utilities.createLocator(UpdateListener.class,
                SSLManagerBeanCustomizer.class);
        XmlService xmlService = locator.getService(XmlService.class);
        Hub hub = locator.getService(Hub.class);
        
        XmlRootHandle<DomainBean> rootHandle = xmlService.createEmptyHandle(DomainBean.class);
        rootHandle.addRoot();
        
        DomainBean domain = rootHandle.getRoot();
        
        SecurityManagerBean originalSM = xmlService.createBean(SecurityManagerBean.class);
        AuthorizationProviderBean aliceBean = createAuthorizationProviderBean(xmlService, MergeTest.ALICE_NAME);
        
        originalSM.addAuthorizationProvider(aliceBean);
        
        domain.setSecurityManager(originalSM);
        
        Assert.assertNotNull(locator.getService(AuthorizationProviderBean.class, MergeTest.ALICE_NAME));
        Assert.assertNull(locator.getService(AuthorizationProviderBean.class, MergeTest.BOB_NAME));
        
        checkHubInstanceWithName(hub, MergeTest.AUTHORIZATION_PROVIDER_TYPE,
                MergeTest.SECURITY_MANAGER_INSTANCE + "." + MergeTest.ALICE_NAME, MergeTest.ALICE_NAME);
        checkHubNoInstance(hub, MergeTest.AUTHORIZATION_PROVIDER_TYPE,
                MergeTest.SECURITY_MANAGER_INSTANCE + "." + MergeTest.BOB_NAME);
        
        SecurityManagerBean newSM = xmlService.createBean(SecurityManagerBean.class);
        AuthorizationProviderBean bobBean = createAuthorizationProviderBean(xmlService, MergeTest.BOB_NAME);
        
        newSM.addAuthorizationProvider(bobBean);
        
        domain.setSecurityManager(newSM);
        
        Assert.assertNull(locator.getService(AuthorizationProviderBean.class, MergeTest.ALICE_NAME));
        Assert.assertNotNull(locator.getService(AuthorizationProviderBean.class, MergeTest.BOB_NAME));
        
        checkHubInstanceWithName(hub, MergeTest.AUTHORIZATION_PROVIDER_TYPE,
                MergeTest.SECURITY_MANAGER_INSTANCE + "." + MergeTest.BOB_NAME, MergeTest.BOB_NAME);
        checkHubNoInstance(hub, MergeTest.AUTHORIZATION_PROVIDER_TYPE,
                MergeTest.SECURITY_MANAGER_INSTANCE + "." + MergeTest.ALICE_NAME);
        
        UpdateListener listener = locator.getService(UpdateListener.class);
        
        List<Change> changes = listener.getChanges();
        
        OverlayUtilities.checkChanges(changes,
                new ChangeDescriptor(ChangeCategory.ADD_INSTANCE,
                        MergeTest.AUTHORIZATION_PROVIDER_TYPE,    // type name
                        MergeTest.SECURITY_MANAGER_INSTANCE + "." + MergeTest.BOB_NAME,       // instance name
                        MergeTest.BOB_NAME)
                , new ChangeDescriptor(ChangeCategory.REMOVE_INSTANCE,
                        MergeTest.AUTHORIZATION_PROVIDER_TYPE,    // type name
                        MergeTest.SECURITY_MANAGER_INSTANCE + "." + MergeTest.ALICE_NAME,       // instance name
                        MergeTest.ALICE_NAME)
                , new ChangeDescriptor(ChangeCategory.MODIFY_INSTANCE,
                        MergeTest.SECURITY_MANAGER_TYPE,    // type name
                        MergeTest.SECURITY_MANAGER_INSTANCE,       // instance name
                        null,
                        AUTHORIZATION_PROVIDER_PROPERTY) // prop changed
        );
        
        
    }
    
    /**
     * Tests that setting things in a direct bean is fine
     * 
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testKeyedDirectSetModification() throws Exception {
        ServiceLocator locator = Utilities.createLocator(UpdateListener.class,
                SSLManagerBeanCustomizer.class);
        XmlService xmlService = locator.getService(XmlService.class);
        Hub hub = locator.getService(Hub.class);
        
        XmlRootHandle<DomainBean> rootHandle = xmlService.createEmptyHandle(DomainBean.class);
        rootHandle.addRoot();
        
        DomainBean domain = rootHandle.getRoot();
        
        DiagnosticsBean aliceBean = createDiagnosticsBean(xmlService, MergeTest.ALICE_NAME);
        
        domain.setDiagnostics(aliceBean);
        
        Assert.assertNotNull(locator.getService(DiagnosticsBean.class, MergeTest.ALICE_NAME));
        Assert.assertNull(locator.getService(DiagnosticsBean.class, MergeTest.BOB_NAME));
        
        checkHubInstanceWithName(hub, MergeTest.DIAGNOSTICS_TYPE,
                MergeTest.DOMAIN_INSTANCE + "." + MergeTest.ALICE_NAME, MergeTest.ALICE_NAME);
        checkHubNoInstance(hub, MergeTest.DIAGNOSTICS_TYPE, MergeTest.DOMAIN_INSTANCE + "." + MergeTest.BOB_NAME);
        
        DiagnosticsBean bobBean = createDiagnosticsBean(xmlService, MergeTest.BOB_NAME);
        
        domain.setDiagnostics(bobBean);
        
        Assert.assertNull(locator.getService(DiagnosticsBean.class, MergeTest.ALICE_NAME));
        Assert.assertNotNull(locator.getService(DiagnosticsBean.class, MergeTest.BOB_NAME));
        
        checkHubInstanceWithName(hub, MergeTest.DIAGNOSTICS_TYPE,
                MergeTest.DOMAIN_INSTANCE + "." + MergeTest.BOB_NAME, MergeTest.BOB_NAME);
        checkHubNoInstance(hub, MergeTest.DIAGNOSTICS_TYPE, MergeTest.DOMAIN_INSTANCE + "." + MergeTest.ALICE_NAME);
        
        UpdateListener listener = locator.getService(UpdateListener.class);
        
        List<Change> changes = listener.getChanges();
        
        OverlayUtilities.checkChanges(changes,
                new ChangeDescriptor(ChangeCategory.ADD_INSTANCE,
                        MergeTest.DIAGNOSTICS_TYPE,    // type name
                        MergeTest.DOMAIN_INSTANCE + "." + MergeTest.BOB_NAME,       // instance name
                        MergeTest.BOB_NAME)
                , new ChangeDescriptor(ChangeCategory.REMOVE_INSTANCE,
                        MergeTest.DIAGNOSTICS_TYPE,    // type name
                        MergeTest.DOMAIN_INSTANCE + "." + MergeTest.ALICE_NAME,       // instance name
                        MergeTest.ALICE_NAME)
                , new ChangeDescriptor(ChangeCategory.MODIFY_INSTANCE,
                        MergeTest.DOMAIN_TYPE,    // type name
                        MergeTest.DOMAIN_INSTANCE,       // instance name
                        null,
                        "diagnostics") // prop changed
        );
        
        
    }
    
    private static void checkHubNoInstance(Hub hub, String typeName, String instanceName) {
        BeanDatabase bd = hub.getCurrentDatabase();
        
        Instance instance = bd.getInstance(typeName, instanceName);
        Assert.assertNull(instance);
    }
    
    @SuppressWarnings("unchecked")
    private static void checkHubInstanceWithName(Hub hub, String typeName, String instanceName, String name) {
        BeanDatabase bd = hub.getCurrentDatabase();
        
        Instance instance = bd.getInstance(typeName, instanceName);
        Assert.assertNotNull(instance);
        
        Map<String, Object> bean = (Map<String, Object>) instance.getBean();
        Assert.assertNotNull(bean);
        
        Assert.assertEquals(name, bean.get(Commons.NAME_TAG));
    }
    
    private final static MachineBean createMachineBean(XmlService xmlService, String name) {
        MachineBean retVal = xmlService.createBean(MachineBean.class);
        retVal.setName(name);
       
        return retVal;
    }
    
    private final static AuthorizationProviderBean createAuthorizationProviderBean(XmlService xmlService, String name) {
        AuthorizationProviderBean retVal = xmlService.createBean(AuthorizationProviderBean.class);
        retVal.setName(name);
       
        return retVal;
    }
    
    public final static DiagnosticsBean createDiagnosticsBean(XmlService xmlService, String name) {
        DiagnosticsBean retVal = xmlService.createBean(DiagnosticsBean.class);
        retVal.setName(name);
       
        return retVal;
    }

}
