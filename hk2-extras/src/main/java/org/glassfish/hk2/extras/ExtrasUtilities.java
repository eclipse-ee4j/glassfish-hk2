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

package org.glassfish.hk2.extras;

import org.glassfish.hk2.api.DuplicateServiceException;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.messaging.TopicDistributionService;
import org.glassfish.hk2.extras.events.internal.DefaultTopicDistributionService;
import org.glassfish.hk2.extras.hk2bridge.internal.Hk2BridgeImpl;
import org.glassfish.hk2.extras.interception.internal.DefaultInterceptionService;
import org.glassfish.hk2.extras.operation.internal.OperationManagerImpl;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.hk2.utilities.DescriptorImpl;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;

/**
 * These are utilities for the extra features of hk2.
 * Generally they allow for 
 * @author jwells
 *
 */
public class ExtrasUtilities {
    /**
     * This will be put into the metadata of a descriptor that is bridged from another service locator.
     * The value will be the locator id of the service locator from which the service originates
     */
    public final static String HK2BRIDGE_LOCATOR_ID = "org.jvnet.hk2.hk2bridge.locator.id";
    
    /**
     * This will be put into the metadata of a descriptor that is bridged from another service locator.
     * The value will be the service id of the descriptor from which the service originates
     */
    public final static String HK2BRIDGE_SERVICE_ID = "org.jvnet.hk2.hk2bridge.service.id";
    
    /**
     * This method adds in a default implementation of the {@link org.glassfish.hk2.api.InterceptionService}
     * which uses annotations to denote which services should intercept other services.  For more
     * information see the org.glassfish.hk2.extras.interception package.  This method is
     * idempotent, if the service is already available it will not add it
     * 
     * @param locator The locator to add the default interception service implementation to.  May not be null
     */
    public static void enableDefaultInterceptorServiceImplementation(ServiceLocator locator) {
        if (locator.getBestDescriptor(BuilderHelper.createContractFilter(DefaultInterceptionService.class.getName())) == null) {
            try {
                ServiceLocatorUtilities.addClasses(locator, true, DefaultInterceptionService.class);
            }
            catch (MultiException me) {
                if (!isDupException(me)) throw me;
            }
        }
    }
    
    /**
     * This method adds in the infrastructure necessary to enable HK2 Operations support.
     * For more information see {@link org.glassfish.hk2.extras.operation.OperationManager}.
     * This method is idempotent, if the service is already available it will not add it
     * 
     * @param locator The locator to add the infrastructure needed to use the HK2
     * Operations support
     */
    public static void enableOperations(ServiceLocator locator) {
        if (locator.getBestDescriptor(BuilderHelper.createContractFilter(OperationManagerImpl.class.getName())) != null) return;
        
        try {
            ServiceLocatorUtilities.addClasses(locator, true, OperationManagerImpl.class);
        }
        catch (MultiException me) {
            if (!isDupException(me)) throw me;
        }
    }
    
    private final static String BRIDGE_NAME_PREFIX = "LocatorBridge(";
    private final static String COMMA = ",";
    private final static String BRIDGE_NAME_POSTFIX = ")";
    
    private static String getBridgeName(ServiceLocator into, ServiceLocator from) {
        return BRIDGE_NAME_PREFIX + from.getLocatorId() + COMMA + into.getLocatorId() + BRIDGE_NAME_POSTFIX;
    }
    
    private static void checkParentage(ServiceLocator a, ServiceLocator b) {
        ServiceLocator originalA = a;
        
        while (a != null) {
            if (a.getLocatorId() == b.getLocatorId()) {
                throw new IllegalStateException("Locator " + originalA + " is a child of or is the same as locator " + b);
            }
            
            a = a.getParent();
        }
    }
    
    /**
     * This method will bridge all non-local services from the
     * from ServiceLocator into the into ServiceLocator.  Changes
     * to the set of services in the from ServiceLocator will be
     * reflected in the into ServiceLocator.  The two ServiceLocators
     * involved must not have a parent/child relationship
     * 
     * @param into The non-null ServiceLocator that will have services added
     * to it from the from ServiceLocator
     * @param from The non-null ServiceLocator that will add services to the
     * into ServiceLocator
     */
    public static void bridgeServiceLocator(ServiceLocator into, ServiceLocator from) {
        checkParentage(into, from);
        checkParentage(from, into);
        
        String bridgeName = getBridgeName(into, from);
        if (from.getService(Hk2BridgeImpl.class, bridgeName) != null) {
            throw new IllegalStateException("There is already a bridge from locator " + from.getName() + " to " + into.getName());
        }
        
        DescriptorImpl di = BuilderHelper.createDescriptorFromClass(Hk2BridgeImpl.class);
        di.setName(bridgeName);
        
        ServiceLocatorUtilities.addOneDescriptor(from, di);
        
        Hk2BridgeImpl bridge = from.getService(Hk2BridgeImpl.class, bridgeName);
        
        // Kick it off
        bridge.setRemote(into);
    }
    
    /**
     * This method will remove all non-local services from the
     * from ServiceLocator into the into ServiceLocator.  The service
     * locator will no longer be related by this bridge
     * The two ServiceLocators involved must not have a parent/child relationship
     * 
     * @param into The non-null ServiceLocator that will have services added
     * to it from the from ServiceLocator
     * @param from The non-null ServiceLocator that will add services to the
     * into ServiceLocator
     */
    public static void unbridgeServiceLocator(ServiceLocator into, ServiceLocator from) {
        checkParentage(into, from);
        checkParentage(from, into);
        
        String bridgeName = getBridgeName(into, from);
        
        ServiceHandle<Hk2BridgeImpl> handle = from.getServiceHandle(Hk2BridgeImpl.class, bridgeName);
        if (handle == null) return;
        
        handle.destroy();
        
        ServiceLocatorUtilities.removeFilter(from, BuilderHelper.createNameAndContractFilter(
                Hk2BridgeImpl.class.getName(),bridgeName));
    }
    
    /**
     * This method will enable the default topic distribution service.
     * <p>
     * The default distribution service distributes messages on the
     * same thread as the caller of {@link org.glassfish.hk2.api.messaging.Topic#publish(Object)}.
     * Objects to be distributed to will be
     * held with SoftReferences, and hence if they go out of scope they
     * will not be distributed to.  Only services created AFTER the topic
     * distribution service is enabled will be distributed to.
     * <p>
     * This method is idempotent, so that if there is already a
     * TopicDistributionService with the default name is available this method
     * will do nothing
     *
     * @param locator The service locator to enable topic distribution on.  May not be null
     */
    public static void enableTopicDistribution(ServiceLocator locator) {
        if (locator == null) throw new IllegalArgumentException();

        if (locator.getService(TopicDistributionService.class, TopicDistributionService.HK2_DEFAULT_TOPIC_DISTRIBUTOR) != null) {
            // Will not add it a second time
            return;
        }

        try {
            ServiceLocatorUtilities.addClasses(locator, true, DefaultTopicDistributionService.class);
        }
        catch (MultiException me) {
            if (!isDupException(me)) throw me;
        }
    }
    
    private static boolean isDupException(MultiException me) {
        boolean atLeastOne = false;
        
        for (Throwable error : me.getErrors()) {
            atLeastOne = true;
            
            if (!(error instanceof DuplicateServiceException)) return false;
        }
        
        return atLeastOne;
    }

}
