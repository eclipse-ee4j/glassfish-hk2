/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2018 Payara Foundation
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

package org.glassfish.hk2.tests.operation.shutdown;

import org.glassfish.hk2.api.ProxyCtl;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.extras.operation.OperationHandle;
import org.glassfish.hk2.extras.operation.OperationManager;
import org.glassfish.hk2.tests.operation.basic.BasicOperationScope;
import org.glassfish.hk2.tests.operation.basic.BasicOperationScopeContext;
import org.glassfish.hk2.tests.operation.basic.OperationsTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class OperationShutdownTest {
    /**
     * Tests that the destroy of ServiceHandle works
     */
    @Test // @org.junit.Ignore
    public void testServiceHandleDestroyWorks() {
        ServiceLocator locator = OperationsTest.createLocator(BasicOperationScopeContext.class,
                Registrar.class,
                PerLookupClassShutdown.class,
                OperationalServiceWithPerLookupService.class);
        
        OperationManager operationManager = locator.getService(OperationManager.class);
        
        try (OperationHandle<BasicOperationScope> operationHandle = operationManager.createOperation(OperationsTest.BASIC_OPERATION_ANNOTATION)) {
            operationHandle.resume();
            
            ServiceHandle<OperationalServiceWithPerLookupService> handle =
                    locator.getServiceHandle(OperationalServiceWithPerLookupService.class);
            
            OperationalServiceWithPerLookupService parent = handle.getService();
            if (parent instanceof ProxyCtl) {
                parent = (OperationalServiceWithPerLookupService) ((ProxyCtl) parent).__make();
            }
            
            Assert.assertFalse(handle.getService().isClosed());
            
            PerLookupClassShutdown plcs = handle.getService().getPerLookupService();
            Assert.assertNotNull(plcs);
            
            Registrar registrar = locator.getService(Registrar.class);
            Assert.assertFalse(registrar.isShutDown(plcs));
            
            handle.destroy();
            
            Assert.assertTrue(parent.isClosed());
            Assert.assertTrue(registrar.isShutDown(plcs));
        }
        
    }
    
    /**
     * Tests that the destroy gets called when an operation is closed, even for
     * a factory created service that was proxied
     */
    @Test // @org.junit.Ignore
    public void testFactoryDestructionWorksOnCloseOperation() {
        ServiceLocator locator = OperationsTest.createLocator(BasicOperationScopeContext.class,
                SingletonWithFactoryCreatedService.class,
                OperationScopeFactory.class);
        
        OperationManager operationManager = locator.getService(OperationManager.class);
        
        OperationHandle<BasicOperationScope> operationHandle = operationManager.createOperation(OperationsTest.BASIC_OPERATION_ANNOTATION);
        operationHandle.resume();
        
        SingletonWithFactoryCreatedService singleton = locator.getService(SingletonWithFactoryCreatedService.class);
        CreatedByFactory factoryCreated = singleton.getFactoryCreated();
        Assert.assertTrue(factoryCreated instanceof ProxyCtl);
        
        // Forces underlying creation
        factoryCreated.createMe();
        
        OperationScopeFactory osf = locator.getService(OperationScopeFactory.class);
        
        CreatedByFactory unwrapped = (CreatedByFactory) ((ProxyCtl) factoryCreated).__make();
        
        Assert.assertFalse(osf.hasBeenDestroyed(unwrapped));
        
        operationHandle.close();
        
        Assert.assertTrue(osf.hasBeenDestroyed(unwrapped));
    }
    
    /**
     * Tests that the destroy gets called when an operation is closed, even for
     * a factory created service that was proxied and created with a ServiceHandle
     */
    @Test // @org.junit.Ignore
    public void testFactoryDestructionWorksOnCloseOperationWithServiceHandle() {
        ServiceLocator locator = OperationsTest.createLocator(BasicOperationScopeContext.class,
                SingletonWithFactoryCreatedService.class,
                OperationScopeFactory.class);
        
        OperationManager operationManager = locator.getService(OperationManager.class);
        
        OperationHandle<BasicOperationScope> operationHandle = operationManager.createOperation(OperationsTest.BASIC_OPERATION_ANNOTATION);
        operationHandle.resume();
        
        ServiceHandle<SingletonWithFactoryCreatedService> singleton = locator.getServiceHandle(SingletonWithFactoryCreatedService.class);
        CreatedByFactory factoryCreated = singleton.getService().getFactoryCreated();
        Assert.assertTrue(factoryCreated instanceof ProxyCtl);
        
        // Forces underlying creation
        factoryCreated.createMe();
        
        OperationScopeFactory osf = locator.getService(OperationScopeFactory.class);
        
        CreatedByFactory unwrapped = (CreatedByFactory) ((ProxyCtl) factoryCreated).__make();
        
        Assert.assertFalse(osf.hasBeenDestroyed(unwrapped));
        
        operationHandle.close();
        
        Assert.assertTrue(osf.hasBeenDestroyed(unwrapped));
        
        // Ensures doubles do not happen
        singleton.destroy();
    }
    
    /**
     * Tests that the destroy gets called when an operation is closed, even for
     * a factory created service that was not proxied
     */
    @Test // @org.junit.Ignore
    public void testFactoryDestructionWorksOnCloseOperationNotProxied() {
        ServiceLocator locator = OperationsTest.createLocator(BasicOperationScopeContext.class,
                OperationScopeWithFactoryCreatedService.class,
                OperationScopeFactory.class);
        
        OperationManager operationManager = locator.getService(OperationManager.class);
        
        OperationHandle<BasicOperationScope> operationHandle = operationManager.createOperation(OperationsTest.BASIC_OPERATION_ANNOTATION);
        operationHandle.resume();
        
        OperationScopeWithFactoryCreatedService opScoped = locator.getService(OperationScopeWithFactoryCreatedService.class);
        CreatedByFactory factoryCreated = opScoped.getCreatedByFactory();
        Assert.assertFalse(factoryCreated instanceof ProxyCtl);
        
        // Puts it into the create map in the factory
        factoryCreated.createMe();
        
        OperationScopeFactory osf = locator.getService(OperationScopeFactory.class);
        
        Assert.assertFalse(osf.hasBeenDestroyed(factoryCreated));
        
        operationHandle.close();
        
        Assert.assertTrue(osf.hasBeenDestroyed(factoryCreated));
    }
    
    /**
     * Tests that the destroy gets called when an operation is closed, even for
     * a factory created service that was not proxied and created with ServiceHandle
     */
    @Test // @org.junit.Ignore
    public void testFactoryDestructionWorksOnCloseOperationNotProxiedWithServiceHandle() {
        ServiceLocator locator = OperationsTest.createLocator(BasicOperationScopeContext.class,
                OperationScopeWithFactoryCreatedService.class,
                OperationScopeFactory.class);
        
        OperationManager operationManager = locator.getService(OperationManager.class);
        
        OperationHandle<BasicOperationScope> operationHandle = operationManager.createOperation(OperationsTest.BASIC_OPERATION_ANNOTATION);
        operationHandle.resume();
        
        ServiceHandle<OperationScopeWithFactoryCreatedService> opScoped = locator.getServiceHandle(OperationScopeWithFactoryCreatedService.class);
        CreatedByFactory factoryCreated = opScoped.getService().getCreatedByFactory();
        Assert.assertFalse(factoryCreated instanceof ProxyCtl);
        
        // Puts it into the create map in the factory
        factoryCreated.createMe();
        
        OperationScopeFactory osf = locator.getService(OperationScopeFactory.class);
        
        Assert.assertFalse(osf.hasBeenDestroyed(factoryCreated));
        
        operationHandle.close();
        
        Assert.assertTrue(osf.hasBeenDestroyed(factoryCreated));
        
        opScoped.destroy();
    }

}
