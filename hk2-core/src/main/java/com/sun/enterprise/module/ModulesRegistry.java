/*
 * Copyright (c) 2007, 2018 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.enterprise.module;

import java.io.PrintStream;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.PopulatorPostProcessor;
import org.glassfish.hk2.api.ServiceLocator;
import org.jvnet.hk2.annotations.Contract;

/**
 * @author Sanjeeb.Sahoo@Sun.COM
 */
@Contract
public interface ModulesRegistry extends ModuleChangeListener {
    /**
     * Creates a new child {@link ModulesRegistry} in this {@link ModulesRegistry}.
     */
    ModulesRegistry createChild();

    /**
     * Creates an uninitialized {@link ServiceLocator}
     *
     */
    ServiceLocator newServiceLocator() throws MultiException;

    /**
     * Creates the default {@link ServiceLocator} from all the modules in this registry
     * Calling this method has the same effect of calling {@link #createServiceLocator("default")}
     *
     * @param name
     *      Determines which inhabitants descriptors are loaded.
     *      (so that different parallel habitats can be
     *      created over the same modules registry.)
     */
    ServiceLocator createServiceLocator() throws MultiException;

    /**
     * Creates a {@link ServiceLocator} from all the modules in this registry
     * Cal;ling this method has the same effect of calling {@link #newServiceLocator()} followed by
     * {@link #populateServiceLocator(String, org.glassfish.hk2.api.ServiceLocator, java.util.List}.
     *
     * @param name
     *      Determines which inhabitants descriptors are loaded.
     *      (so that different parallel habitats can be
     *      created over the same modules registry.)
     */
    ServiceLocator createServiceLocator(String name) throws MultiException;


    /**
     * Creates a {@link ServiceLocator} with the provided parent.
     *  
     *
     * @param serviceLocator
     * @param name
     * @param postProcessors
     * @return
     */
	ServiceLocator createServiceLocator(ServiceLocator serviceLocator,
                                        String name, List<PopulatorPostProcessor> postProcessors);
	
    /**
     * Populates a {@link ServiceLocator} from all the modules in this registry.
     *
     * <p>
     * Default {@link InhabitantsParser} is used.
     *
     *
     * @param name
     *      Determines which inhabitants descriptors are loaded.
     *      (so that different parallel habitats can be
     *      created over the same modules registry.)
     * @param h
     *      Habitat to initialize, null if it should be created
     *
     * @param postProcessors
     * @return initialized Habitat
     */
    void populateServiceLocator(String name, ServiceLocator h, List<PopulatorPostProcessor> postProcessors) throws MultiException;

    /**
     * Add a new <code>Repository</code> to this registry. From now on
     * the repository will be used to procure requested module not yet registered
     * in this registry instance. Repository can be searched in a particular
     * order (to accomodate performance requirements like looking at local
     * repositories first), a search order (1 to 100) can be specified when
     * adding a repository to the registry (1 is highest priority).
     * @param repository new repository to attach to this registry
     * @param weight int value from 1 to 100 to specify the search order
     */
    void addRepository(Repository repository, int weight);

    /**
     * Add a new <code>Repository</code> to this registry. From now on
     * the repository will be used to procure requested nodule not
     * registered in this instance.
     * @param repository new repository to attach to this registry
     */
    void addRepository(Repository repository);

    /**
     * Remove a repository from the list of attached repositories to
     * this instances. After this call, the <code>Repository</code>
     * name will not be used to procure missing modules any
     * longer
     * @param name name of the repository to remove
     */
    void removeRepository(String name);

    /**
     * Get a repository from the list of attached repositories
     *
     * @param name name of the repository to return
     * @return the repository or null if not found
     */
    Repository getRepository(String name);

    /**
     * Returns the <code>HK2Module</code> instance giving a name and version
     * constraints.
     *
     * @param name the module name
     * @param version the module version. Caller should specify a correct version.
     * @return the module instance or null if none can be found
     * @throws ResolveError if the module dependencies cannot be resolved
     */
    HK2Module makeModuleFor(String name, String version) throws ResolveError;

    /**
     * Returns the <code>HK2Module</code> instance giving a name and version
     * constraints.
     *
     * @param name the module name
     * @param version the module version. Caller should specify a correct version.
     * @param resolve should the module be resolved or not
     * @return the module instance or null if none can be found
     * @throws ResolveError if the module dependencies cannot be resolved
     */
    HK2Module makeModuleFor(String name, String version, boolean resolve) throws ResolveError;

    /**
     * Find and return a loaded HK2Module that has the package name in its list
 of exported interfaces.
     *
     * @param packageName the requested implementation package name.
     * @return the <code>HK2Module</code> instance implementing the package
     * name or null if not found.
     * @throws ResolveError if the module dependencies cannot be resolved
     */
    HK2Module makeModuleFor(String packageName) throws ResolveError;

    /**
     * Returns the list of shared Modules registered in this instance.
     *
     * <p>
     * The returned list will not include the modules defined in the ancestor
     * {@link ModulesRegistry}s.
     *
     * @return an umodifiable list of loaded modules
     */
    Collection<HK2Module> getModules();

    /**
     * Returns the list of shared Modules registered in this instance whose name
     * matches the given name
     *
     * <p>
     * The returned list will not include the modules defined in the ancestor
     * {@link ModulesRegistry}s.
     *
     * @return an umodifiable list of loaded modules having names that match
     * the given name
     */
    Collection<HK2Module> getModules(String moduleName);

    /**
     * Detaches all the modules from this registry. The modules are not
     * deconstructed when calling this method.
     */
    void detachAll();

    /**
     * Registers a new DefaultModuleDefinition in this registry. Using this module
     * definition, the registry will be capable of created shared and private
     * <code>HK2Module</code> instances.
     */
    HK2Module add(ModuleDefinition info) throws ResolveError;

    /**
     * Registers a new DefaultModuleDefinition in this registry. Using this module
     * definition, the registry will be capable of created shared and private
     * <code>HK2Module</code> instances.
     * @param info ModuleDefinition representing the new module content
     * @param resolve should the new module be resolved or not
     */
    HK2Module add(ModuleDefinition info, boolean resolve) throws ResolveError;

    /**
     * Print a Registry dump to the logger
     * @param logger the logger to dump on
     */
    void print(Logger logger);

    /**
     * Add a <code>ModuleLifecycleListener</code> to this registry. The listener
     * will be notified for each module startup and shutdown.
     * @param listener the listener implementation
     */
    void register(ModuleLifecycleListener listener);

    /**
     * Removes an <code>ModuleLifecycleListener</code> from this registry.
     * Notification of module startup and shutdown will not be emitted to this
     * listener any longer.
     * @param listener the listener to unregister
     */
    void unregister(ModuleLifecycleListener listener);

    /**
     * Shuts down this module's registry, apply housekeeping tasks
     *
     */
    void shutdown();

    void dumpState(PrintStream writer);

    <T> Iterable<Class<? extends T>> getProvidersClass(Class<T> serviceClass);

    /**
     * Returns a collection of HK2Module containing at least one implementation
 of the passed service interface class.
     *
     * @param serviceClass the service interface class
     * @return a collection of module
     */
    Iterable<HK2Module> getModulesProvider(Class serviceClass);

    /**
     * Registers a running service, this is useful when other components need
     * to have access to a provider of a service without having to create
     * a new instance and initialize it.
     * @param serviceClass the service interface
     * @param provider the provider of that service.
     */
    <T> void registerRunningService(Class<T> serviceClass, T provider);

    /**
     * Removes a running service, this is useful when a service instance is no longer
     * available as a provider of a service.
     */
    <T> boolean unregisterRunningService(Class<T> serviceClass, T provider);

    /**
     * Returns all running services implementation of the passed service
     * interface
     * @param serviceClass the service interface
     * @return the list of providers of that service.
     */
    <T> List<T> getRunningServices(Class<T> serviceClass);

    void setParentClassLoader(ClassLoader parent);

    ClassLoader getParentClassLoader();

    /**
     * Returns a ClassLoader capable of loading classes from a set of modules identified
     * by their module definition
     *
     * @param parent the parent class loader for the returned class loader instance
     * @param defs module definitions for all modules this classloader should be
     *        capable of loading classes from
     * @return class loader instance
     * @throws com.sun.enterprise.module.ResolveError if one of the provided module
     *         definition cannot be resolved
     */
    ClassLoader getModulesClassLoader(ClassLoader parent,
                                      Collection<ModuleDefinition> defs)
        throws ResolveError;

    /**
     * Returns a ClassLoader capable of loading classes from a set of modules identified
     * by their module definition and also load new urls.
     *
     * @param parent the parent class loader for the returned class loader instance
     * @param defs module definitions for all modules this classloader should be
     *        capable of loading
     * @param urls urls to be added to the module classloader
     * @return class loader instance
     * @throws com.sun.enterprise.module.ResolveError if one of the provided module
     *         definition cannot be resolved
     */
    ClassLoader getModulesClassLoader(ClassLoader parent,
                                      Collection<ModuleDefinition> defs,
                                      URL[] urls) throws ResolveError;

    /**
     * Finds the {@link HK2Module} that owns the given class.
     *
     * @return
     *      null if the class is loaded outside the module system.
     */
    HK2Module find(Class clazz);

    /**
     * Gets the {@link HK2Module} that provides the provider of the given name.
     */
    HK2Module getProvidingModule(String providerClassName);

	public ServiceLocator newServiceLocator(ServiceLocator parent)
			throws MultiException;

	public void populateConfig(ServiceLocator serviceLocator)
			throws MultiException;
}
