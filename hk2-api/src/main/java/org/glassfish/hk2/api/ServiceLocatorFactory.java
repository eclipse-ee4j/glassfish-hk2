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

package org.glassfish.hk2.api;

import org.glassfish.hk2.extension.ServiceLocatorGenerator;
import org.glassfish.hk2.internal.ServiceLocatorFactoryImpl;

/**
 * This factory can be used to create new named ServiceLocators
 * 
 * @author jwells
 */
public abstract class ServiceLocatorFactory {
  private static ServiceLocatorFactory INSTANCE = new ServiceLocatorFactoryImpl();
  
  /**
   * This will return a factory where the ServiceLocatorGenerator
   * is discovered from the META-INF/services of the process
   * 
   * @return The factory to use to create service locators
   */
  public static ServiceLocatorFactory getInstance() {
    return INSTANCE;
  }
  
  /**
   * Creates (or finds) a ServiceLocator.
   * <p>
   * If there is already a ServiceLocator with the given
   * name then this method will return that locator.
   * 
   * @param name The name of this service locator.  Passing a null
   * name will result in a newly created service locator with a
   * generated name and that will not be tracked by the system
   * @return The created or found named ServiceLocator
   */
  public abstract ServiceLocator create(String name);
  
  /**
   * Creates or finds a ServiceLocator.
   * <p>
   * If there is already a ServiceLocator with the given
   * name then this method will return that ServiceLocator.  The
   * parent argument will be ignored in that case
   * 
   * @param name The name of this service locator.  Passing a null
   * name will result in a newly created service locator with a
   * generated name and that will not be tracked by the system
   * @param parent The parent of this ServiceLocator.  Services can
   * be found in the parent (and all grand-parents).  May be null
   * if the returned ServiceLocator should not be parented
   * @return The created or found named ServiceLocator
   */
  public abstract ServiceLocator create(String name,
          ServiceLocator parent);
  
  /**
   * Creates or finds a ServiceLocator.
   * <p>
   * If there is already a ServiceLocator with the given
   * name then this method will return that ServiceLocator.  The
   * parent argument will be ignored in that case.
   * If a null name is given then a new ServiceLocator with a
   * generated name will be returned.
   * 
   * @param name The name of this service locator.  Passing a null
   * name will result in a newly created service locator with a
   * generated name and that will not be tracked by the system
   * @param parent The parent of this ServiceLocator.  Services can
   * be found in the parent (and all grand-parents).  May be null
   * if the returned ServiceLocator should not be parented
   * @param generator An implementation of the generator interface that
   * can be used to provide an implementation of ServiceLocator.  If
   * null then the generator used will be discovered from the OSGi
   * service registry or from META-INF/services
   * @return The created or found named ServiceLocator
   */
  public abstract ServiceLocator create(String name,
          ServiceLocator parent,
          ServiceLocatorGenerator generator);
  
  /**
   * Creates a ServiceLocator.
   * <p>
   * If there is already a ServiceLocator with the given
   * name then this method will honor the given CreatePolicy.
   * return that ServiceLocator.  The policies are<UL>
   * <LI>RETURN: Return the existing locator</LI>
   * <LI>DESTOY: Destroy the existing locator</LI>
   * <LI>ERROR: Throw an IllegalStateException exception</LI>
   * </UL>
   * 
   * @param name The name of this service locator.  Passing a null
   * name will result in a newly created service locator with a
   * generated name and that will not be tracked by the system
   * @param parent The parent of this ServiceLocator.  Services can
   * be found in the parent (and all grand-parents).  May be null
   * if the returned ServiceLocator should not be parented
   * @param generator An implementation of the generator interface that
   * can be used to provide an implementation of ServiceLocator.  If
   * null then the generator used will be discovered from the OSGi
   * service registry or from META-INF/services
   * @param policy The policy that should be used if there is an
   * existing locator with the non-null name.  If null the policy
   * of RETURN will be used
   * @return The created or found named ServiceLocator
   */
  public abstract ServiceLocator create(String name,
          ServiceLocator parent,
          ServiceLocatorGenerator generator,
          CreatePolicy policy);
  
  /**
   * Finds the ServiceLocator with this name
   * 
   * @param name May not be null, is the name of the ServiceLocator to find
   * @return The ServiceLocator with the given name, or null if there
   *   is no ServiceLocator with that name
   */
  public abstract ServiceLocator find(String name);
  
  /**
   * Removes the ServiceLocator with this name
   * <p>
   * All services associated with this ServiceLocator will be shutdown
   * 
   * @param name The name of the ServiceLocator to destroy
   */
  public abstract void destroy(String name);
  
  /**
   * Removes the given ServiceLocator
   * <p>
   * All services associated with this ServiceLocator will be shutdown
   * 
   * @param locator The ServiceLocator to destroy.  If null this will do nothing.
   * If the ServiceLocator given was already destroyed this will do nothing
   */
  public abstract void destroy(ServiceLocator locator);
  
  /**
   * Adds a service listener to the unordered set of listeners that
   * will be notified when named listeners are added or removed
   * from the system.  If this listener is already registered
   * this method does nothing
   * 
   * @param listener The non-null listener to add to the system
   */
  public abstract void addListener(ServiceLocatorListener listener);
  
  /**
   * Removes a service listener from the set of listeners that
   * are notified when named listeners are added or removed
   * from the system
   * 
   * @param listener The non-null listener to remove from the system
   */
  public abstract void removeListener(ServiceLocatorListener listener);
  
  /**
   * Tells the create method what to do if an existing ServiceLocator
   * with the given name exists
   * 
   * @author jwells
   *
   */
  public enum CreatePolicy {
      /** Return the existing ServiceLocator */
      RETURN,
      
      /** Destroy the existing ServiceLocator */
      DESTROY,
      
      /** Throw an IllegalStateException */
      ERROR
  }

}
