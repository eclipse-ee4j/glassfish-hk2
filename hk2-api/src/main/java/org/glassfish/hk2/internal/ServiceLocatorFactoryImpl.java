/*
 * Copyright (c) 2012, 2024 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.internal;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.concurrent.locks.ReentrantLock;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.api.ServiceLocatorListener;
import org.glassfish.hk2.extension.ServiceLocatorGenerator;
import org.glassfish.hk2.osgiresourcelocator.ServiceLoader;
import org.glassfish.hk2.utilities.reflection.Logger;

/**
 * The implementation of the {@link ServiceLocatorFactory} that looks
 * in the OSGi service registry or the META-INF/services for the implementation
 * to use.  Failing those things, it uses the standard default locator
 * generator, which is found in auto-depends, which is the 99.9% case
 * 
 * @author jwells
 */
public class ServiceLocatorFactoryImpl extends ServiceLocatorFactory {
    private final static String DEBUG_SERVICE_LOCATOR_PROPERTY = "org.jvnet.hk2.properties.debug.service.locator.lifecycle";
    private final static boolean DEBUG_SERVICE_LOCATOR_LIFECYCLE = AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
        @Override
        public Boolean run() {
            return Boolean.parseBoolean(System.getProperty(DEBUG_SERVICE_LOCATOR_PROPERTY, "false"));
        }
            
    });
    
    private static final ReentrantLock sLock = new ReentrantLock();
    private static int name_count = 0;
    private static final String GENERATED_NAME_PREFIX = "__HK2_Generated_";
    
    private final static class DefaultGeneratorInitializer {
        private final static ServiceLocatorGenerator defaultGenerator = getGeneratorSecure();
    }
    
    private final ReentrantLock lock = new ReentrantLock();
    private final HashMap<String, ServiceLocator> serviceLocators = new HashMap<String, ServiceLocator>();
    private final HashSet<ServiceLocatorListener> listeners = new HashSet<ServiceLocatorListener>();
    
    private static ServiceLocatorGenerator getGeneratorSecure() {
        return AccessController.doPrivileged(new PrivilegedAction<ServiceLocatorGenerator>() {

            @Override
            public ServiceLocatorGenerator run() {
                try {
                    return getGenerator();
                }
                catch (Throwable th) {
                    Logger.getLogger().warning("Error finding implementation of hk2:", th);
                    return null;
                }
            }
              
          });
        
    }

    /**
     * This will create a new set of name to locator mappings
     */
    public ServiceLocatorFactoryImpl() {
    }
  
  private static Iterable<? extends ServiceLocatorGenerator> getOSGiSafeGenerators() {
      try {
          return ServiceLoader.lookupProviderInstances(ServiceLocatorGenerator.class);
      }
      catch (Throwable th) {
          // The bundle providing ServiceLoader need not be on the classpath
          return null;
      }
  }
  
  private static ServiceLocatorGenerator getGenerator() {
      Iterable<? extends ServiceLocatorGenerator> generators = getOSGiSafeGenerators();
      if (generators != null) {
          // non-null indicates we are in OSGi environment
          // So, we will return whatever we find. If we don't find anything here, then we assume it is a
          // configuration error and return null.
          // Since org.glassfish.hk2.osgiresourcelocator.ServiceLoader never throws ServiceConfigurationError,
          // there is no need to catch it and try next item in the iterator.
          final Iterator<? extends ServiceLocatorGenerator> iterator = generators.iterator();
          return iterator.hasNext() ? iterator.next() : null;
      }
      
      // We are in non-OSGi environment, let's use JDK ServiceLoader instead.
      // Make sure we use our current loader to locate the service as opposed to some arbitrary TCL
      final ClassLoader classLoader = ServiceLocatorFactoryImpl.class.getClassLoader();
      Iterator<ServiceLocatorGenerator> providers = java.util.ServiceLoader.load(ServiceLocatorGenerator.class,
              classLoader).iterator();
      while (providers.hasNext()) {
          try {
              return providers.next();
          } catch (ServiceConfigurationError sce) {
              // This can happen. See the exception javadoc for more details.
              Logger.getLogger().debug("ServiceLocatorFactoryImpl", "getGenerator", sce);
                  // We will try the next one
          }
      }
      
      Logger.getLogger().warning("Cannot find a default implementation of the HK2 ServiceLocatorGenerator");
      return null;
  }

  /* (non-Javadoc)
   * @see org.glassfish.hk2.api.ServiceLocatorFactory#create(java.lang.String, org.glassfish.hk2.api.Module)
   */
  @Override
  public ServiceLocator create(String name) {
      return create(name, null, null, CreatePolicy.RETURN);
  }

  /* (non-Javadoc)
   * @see org.glassfish.hk2.api.ServiceLocatorFactory#find(java.lang.String)
   */
  @Override
  public ServiceLocator find(String name) {
    try {
      lock.lock();
      return serviceLocators.get(name);
    } finally {
      lock.unlock();
    }
  }

  /* (non-Javadoc)
   * @see org.glassfish.hk2.api.ServiceLocatorFactory#destroy(java.lang.String)
   */
  @Override
  public void destroy(String name) {
      destroy(name, null);
  }
  
  private void destroy(String name, ServiceLocator locator) {
      ServiceLocator killMe = null;
    
      try {
          lock.lock();
          if (name != null) {
              killMe = serviceLocators.remove(name);
          }
          
          if (DEBUG_SERVICE_LOCATOR_LIFECYCLE) {
              Logger.getLogger().debug("ServiceFactoryImpl destroying locator with name " + name + " and locator " + locator +
                      " with found locator " + killMe,
                      new Throwable());
          }
          
          if (killMe == null) {
              killMe = locator;
          }
          
          if (killMe != null) {
              for (ServiceLocatorListener listener : listeners) {
                  try {
                      listener.locatorDestroyed(killMe);
                  }
                  catch (Throwable th) {
                      Logger.getLogger().debug(getClass().getName(), "destroy " + listener, th);
                  }
              }
          }
      } finally {
          lock.unlock();
      }
    
      if (killMe != null) {
          killMe.shutdown();
      }
  }
  
  public void destroy(ServiceLocator locator) {
      if (locator == null) return;
      
      destroy(locator.getName(), locator);
  }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ServiceLocatorFactory#create(java.lang.String, org.glassfish.hk2.api.Module, org.glassfish.hk2.api.ServiceLocator)
     */
    @Override
    public ServiceLocator create(String name,
            ServiceLocator parent) {
        return create(name, parent, null, CreatePolicy.RETURN);
    }
    
    private static String getGeneratedName() {
        sLock.lock();
        try {
            return GENERATED_NAME_PREFIX + name_count++;
        } finally {
            sLock.unlock();
        }
        
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ServiceLocatorFactory#create(java.lang.String, org.glassfish.hk2.api.ServiceLocator, org.glassfish.hk2.extension.ServiceLocatorGenerator)
     */
    @Override
    public ServiceLocator create(String name, ServiceLocator parent,
            ServiceLocatorGenerator generator) {
 
        return create(name, parent, generator, CreatePolicy.RETURN);
    }
    
    private void callListenerAdded(ServiceLocator added) {
        for (ServiceLocatorListener listener : listeners) {
            try {
                listener.locatorAdded(added);
            }
            catch (Throwable th) {
                Logger.getLogger().debug(getClass().getName(), "create " + listener, th);
            }
        }
    }
    
    @Override
    public ServiceLocator create(String name, ServiceLocator parent,
            ServiceLocatorGenerator generator, CreatePolicy policy) {
        if (DEBUG_SERVICE_LOCATOR_LIFECYCLE) {
            Logger.getLogger().debug("ServiceFactoryImpl given create of " + name + " with parent " + parent +
                    " with generator " + generator + " and policy " + policy, new Throwable());
        }
        lock.lock();
        try {
            ServiceLocator retVal;

            if (name == null) {
                name = getGeneratedName();
                ServiceLocator added = internalCreate(name, parent, generator);
                callListenerAdded(added);
                if (DEBUG_SERVICE_LOCATOR_LIFECYCLE) {
                    Logger.getLogger().debug("ServiceFactoryImpl added untracked listener " + added);
                }
                return added;
            }

            retVal = serviceLocators.get(name);
            if (retVal != null) {
                if (policy == null || CreatePolicy.RETURN.equals(policy)) {
                    if (DEBUG_SERVICE_LOCATOR_LIFECYCLE) {
                        Logger.getLogger().debug("ServiceFactoryImpl added found listener under RETURN policy of " + retVal);
                    }
                    return retVal;
                }
                
                if (policy.equals(CreatePolicy.DESTROY)) {
                    destroy(retVal);
                }
                else {
                    throw new IllegalStateException(
                            "A ServiceLocator named " + name + " already exists");
                }
            }
            retVal = internalCreate(name, parent, generator);
            serviceLocators.put(name, retVal);
            
            callListenerAdded(retVal);

            if (DEBUG_SERVICE_LOCATOR_LIFECYCLE) {
                Logger.getLogger().debug("ServiceFactoryImpl created locator " + retVal);
            }
            return retVal;
        } finally {
            lock.unlock();
        }
    }

    private ServiceLocator internalCreate(String name, ServiceLocator parent, ServiceLocatorGenerator generator) {
        if (generator == null) {
            if (DefaultGeneratorInitializer.defaultGenerator == null) {
                throw new IllegalStateException("No generator was provided and there is no default generator registered");
            }
            generator = DefaultGeneratorInitializer.defaultGenerator;
        }
        return generator.create(name, parent);
    }

    @Override
    public void addListener(ServiceLocatorListener listener) {
        if (listener == null) throw new IllegalArgumentException();
        
        lock.lock();
        try {
            if (listeners.contains(listener)) return;
            
            try {
                HashSet<ServiceLocator> currentLocators = new HashSet<ServiceLocator>(serviceLocators.values());
                listener.initialize(Collections.unmodifiableSet(currentLocators));
            }
            catch (Throwable th) {
                // Not added to the set of listeners
                Logger.getLogger().debug(getClass().getName(), "addListener " + listener, th);
                return;
            }
            
            listeners.add(listener);
        } finally {
            lock.unlock();
        }
        
    }

    @Override
    public void removeListener(ServiceLocatorListener listener) {
        if (listener == null) throw new IllegalArgumentException();
        
        lock.lock();
        try {
            listeners.remove(listener);
        } finally {
            lock.unlock();
        }
        
    }

    

}
