/*
 * Copyright (c) 2007, 2024 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2020 Payara Services Ltd.
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

package org.jvnet.hk2.osgiadapter;

import static org.jvnet.hk2.osgiadapter.FelixPrettyPrinter.prettyPrintFelixMessage;
import static org.jvnet.hk2.osgiadapter.Logger.logger;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.DescriptorFileFinder;
import org.glassfish.hk2.api.PopulatorPostProcessor;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.bootstrap.HK2Populator;
import org.glassfish.hk2.bootstrap.impl.URLDescriptorFileFinder;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.packageadmin.RequiredBundle;

import com.sun.enterprise.module.LifecyclePolicy;
import com.sun.enterprise.module.HK2Module;
import com.sun.enterprise.module.ModuleChangeListener;
import com.sun.enterprise.module.ModuleDefinition;
import com.sun.enterprise.module.ModuleDependency;
import com.sun.enterprise.module.ModuleMetadata;
import com.sun.enterprise.module.ModuleState;
import com.sun.enterprise.module.ModulesRegistry;
import com.sun.enterprise.module.ResolveError;
import com.sun.enterprise.module.bootstrap.BootException;

/**
 * @author Sanjeeb.Sahoo@Sun.COM
 */
public class OSGiModuleImpl implements HK2Module {
    private final ReentrantLock lock = new ReentrantLock();
    private volatile Bundle bundle; // made volatile as it is accessed from multiple threads

    private ModuleDefinition md;

    private AbstractOSGiModulesRegistryImpl registry;

    private boolean isTransientlyActive = false;


    List<ActiveDescriptor> activeDescriptors;

    /* TODO (Sahoo): Change hk2-apt to generate an equivalent BundleActivator
       corresponding to LifecyclerPolicy class. That way, LifecyclePolicy class
       will be invoked even when underlying OSGi bundle is stopped or started
       using any OSGi bundle management tool.
     */
    private LifecyclePolicy lifecyclePolicy;
    private static final Enumeration<URL> EMPTY_URLS = new Enumeration<URL>() {

        @Override
        public boolean hasMoreElements() {
            return false;
        }

        @Override
        public URL nextElement() {
            throw new NoSuchElementException();
        }
    };

    public OSGiModuleImpl(AbstractOSGiModulesRegistryImpl registry, Bundle bundle, ModuleDefinition md) {
        this.registry = registry;
        this.bundle = bundle;
        this.md = md;
    }

    @Override
    public ModuleDefinition getModuleDefinition() {
        return md;
    }

    @Override
    public String getName() {
        return md.getName();
    }

    @Override
    public ModulesRegistry getRegistry() {
        return registry;
    }

    @Override
    public ModuleState getState() {
        // We don't cache the module state locally. Instead we always map
        // the underlying bundle's state to HK2 state. This avoids us
        // from having to register a listener with OSGi to be updated with
        // bundle state transitions.
        return mapBundleStateToModuleState(bundle);
    }

    /* package */ static ModuleState mapBundleStateToModuleState(Bundle bundle)
    {
        ModuleState state;
        switch (bundle.getState())
        {
            case Bundle.INSTALLED:
            case Bundle.UNINSTALLED:
                state = ModuleState.NEW;
                break;
            case Bundle.RESOLVED:
            case Bundle.STARTING:
            case Bundle.STOPPING:
                state = ModuleState.RESOLVED;
                break;
            case Bundle.ACTIVE:
                state = ModuleState.READY;
                break;
            default:
                throw new RuntimeException(
                        "Does not know how to handle bundle with state [" +
                                bundle.getState() + "]");
        }

        return state;
    }

    @Override
    public void resolve() throws ResolveError {
        // Since OSGi bundle does not have a separate resolve method,
        // we use the same implementation as start();
        lock.lock();
        try {
            start();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void start() throws ResolveError {
        lock.lock();
        try {
            int state = bundle.getState();
            if (((Bundle.STARTING | Bundle.ACTIVE | Bundle.STOPPING) & state) != 0) {
                if (logger.isLoggable(Level.FINER)) {
                    logger.logp(Level.FINER, "OSGiModuleImpl", "start",
                            "Ignoring start of bundle {0} as it is in {1} state",
                            new Object[]{bundle, toString(bundle.getState())} );
                }
                return;
            }
            if (registry.getPackageAdmin().getBundleType(bundle) == PackageAdmin.BUNDLE_TYPE_FRAGMENT) {
                if (logger.isLoggable(Level.FINER)) {
                    logger.logp(Level.FINER, "OSGiModuleImpl", "start",
                            "Ignoring start of bundle {0} as it is a fragment bundle",
                            new Object[]{bundle} );
                }
                return;
            }
            try {
                startBundle();
                isTransientlyActive = true;
                if (logger.isLoggable(Level.FINE)) {
                    logger.logp(Level.FINE, "OSGiModuleImpl",
                            "start", "Started bundle {0}", bundle);
                }
            } catch (BundleException e) {
                throw new ResolveError(
                        "Failed to start " + this + prettyPrintFelixMessage(registry.getBundleContext(), e.getMessage()),
                        e);
            }

            // TODO(Sahoo): Remove this when hk2-apt generates equivalent BundleActivator
            // if there is a LifecyclePolicy, then instantiate and invoke.
            if (md.getLifecyclePolicyClassName()!=null) {
                try {
                    Class<LifecyclePolicy> lifecyclePolicyClass =
                            (Class<LifecyclePolicy>) bundle.loadClass(md.getLifecyclePolicyClassName());
                    lifecyclePolicy = lifecyclePolicyClass.newInstance();
                } catch(ClassNotFoundException e) {
                    throw new ResolveError("ClassNotFound : " + e.getMessage(), e);
                } catch(java.lang.InstantiationException | IllegalAccessException e) {
                    throw new ResolveError(e);
                }
            }
            if (lifecyclePolicy!=null) {
                lifecyclePolicy.start(this);
            }
        } finally {
            lock.unlock();
        }
    }

    private void startBundle() throws BundleException {
        BundleException exception = null;
        for(int attempt = 1; attempt <= 3; attempt++) {
            try {
                if (attempt > 1) {
                    if (logger.isLoggable(Level.FINER)) {
                        logger.log(Level.FINER, "Retrying start of {0} due to lock race condition", bundle);
                    }
                    jitter();
                }
                bundle.start(Bundle.START_TRANSIENT);
                return;
            } catch (BundleException be) {
                exception = be;
                // When starting bundles in multiple threads, Apache Felix may run in locking race
                // condition throwing "Unable to acquire global lock" on one of competing threads.
                // Best course of action is to retry bundle start rather than fail unrecoverably.
                if (!be.getMessage().contains("Unable to acquire global lock")) {
                    break;
                }
            }
        }
        throw exception;
    }

    private void jitter() {
        try {
            Thread.sleep(new Random().nextInt(20));
        } catch (InterruptedException ie) {
            // nevermind
        }
    }

    private String toString(int state)
    {
        String value;
        switch (state) {
            case Bundle.STARTING:
                value = "STARTING";
                break;
            case Bundle.STOPPING:
                value = "STOPPING";
                break;
            case Bundle.INSTALLED:
                value = "INSTALLED";
                break;
            case Bundle.UNINSTALLED:
                value = "UNINSTALLED";
                break;
            case Bundle.RESOLVED:
                value = "RESOLVED";
                break;
            case Bundle.ACTIVE:
                value = "ACTIVE";
                break;
            default:
                value = "UNKNOWN STATE [" + state + "]";
                logger.warning("No mapping exist for bundle state " + state);
        }
        return value;
    }

    @Override
    public boolean stop() {
        lock.lock();
        try {
            detach();
        // Don't refresh packages, as we are not uninstalling the bundle.
//        registry.getPackageAdmin().refreshPackages(new Bundle[]{bundle});
            return true;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void detach() {
        if (bundle.getState() != Bundle.ACTIVE) {
            if (logger.isLoggable(Level.FINER)) {
                logger.logp(Level.FINER, "OSGiModuleImpl", "detach",
                        "Ignoring stop of bundle {0} as it is in {1} state",
                        new Object[]{bundle, toString(bundle.getState())} );
            }
            return;
        }

        if (lifecyclePolicy!=null) {
            lifecyclePolicy.stop(this);
            lifecyclePolicy=null;
        }

        try {
            bundle.stop();
            if (logger.isLoggable(Level.FINE))
            {
                logger.logp(Level.FINE, "OSGiModuleImpl", "detach", "Stopped bundle = {0}", new Object[]{bundle});
            }
//            bundle.uninstall();
        } catch (BundleException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void uninstall() {
        // This method is called when the hk2-osgi-adapter module is stopped.
        // During that time, we need to stop all the modules, hence no sticky check is
        // performed in this method.
        try {
            bundle.uninstall();
        } catch (BundleException e) {
            throw new RuntimeException(e);
        }
        registry.remove(this);
        this.registry = null;
    }

    @Override
    public void refresh() {
        URI location = md.getLocations()[0];
        File f = new File(location);
        if (f.lastModified() > bundle.getLastModified()) {
            try {
                bundle.update();
                registry.getPackageAdmin().refreshPackages(new Bundle[]{bundle});
            } catch (BundleException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public ModuleMetadata getMetadata() {
        return md.getMetadata();
    }

    @Override
    public <T> Iterable<Class<? extends T>> getProvidersClass(
            Class<T> serviceClass) {
        return (Iterable)getProvidersClass(serviceClass.getName());
    }

    @Override
    public Iterable<Class> getProvidersClass(String name) {
        List<Class> r = new ArrayList<>();
        for( String provider : getMetadata().getEntry(name).providerNames) {
            try {
                r.add(getClassLoader().loadClass(provider));
            } catch (ClassNotFoundException e) {
                logger.log(Level.SEVERE, "Failed to load "+provider+" from "+getName(),e);
            }
        }
        return r;
    }

    @Override
    public boolean hasProvider(Class serviceClass) {
        String name = serviceClass.getName();
        return getMetadata().getEntry(name).hasProvider();
    }

    @Override
    public void addListener(ModuleChangeListener listener) {
        registry.addModuleChangeListener(listener, this);
    }

    @Override
    public void removeListener(ModuleChangeListener listener) {
        registry.removeModuleChangeListener(listener);
    }

    @Override
    public void dumpState(PrintStream writer) {
        writer.print(toString());
    }

    /**
     * Parses all the inhabitants descriptors of the given name in this module.
     * @return
     */
    List<ActiveDescriptor> parseInhabitants(String name, ServiceLocator serviceLocator, List<PopulatorPostProcessor> populatorPostProcessors) throws IOException, BootException {

        DescriptorFileFinder dff = null;


        final String path = "META-INF/hk2-locator/" + name;
        URL entry = bundle.getEntry(path);

        if (entry != null) {
            dff = new URLDescriptorFileFinder(entry);
        }


        if (dff != null) {

        	final OSGiModuleImpl module = this;

            ArrayList<PopulatorPostProcessor> allPostProcessors = new ArrayList<>();
            allPostProcessors.add(new OsgiPopulatorPostProcessor(module));
            if (populatorPostProcessors != null) {
              allPostProcessors.addAll(populatorPostProcessors);
            }
    	    this.activeDescriptors = HK2Populator.populate(serviceLocator, dff, allPostProcessors);
        }

        return this.activeDescriptors;
    }

    /**
     * This method is used as the parent loader of the class loader that we return in {@link #getClassLoader}
     */
    private ClassLoader getParentLoader() {
        return Bundle.class.getClassLoader();
    }

    @Override
    public ClassLoader getClassLoader() {
        /*
         * This is a delegating class loader.
         * It always delegates to OSGi's bundle's class loader.
         * ClassLoader.defineClass() is never called in the context of this class.
         * There will never be a class for which getClassLoader()
         * would return this class loader.
         * It overrides loadClass(), getResource() and getResources() as opposed to
         * their findXYZ() equivalents so that the OSGi export control mechanism
         * is enforced even for classes and resources available in the system/boot
         * class loader.
         */
        return new ClassLoader(getParentLoader()) {
            private final ReentrantLock lock = new ReentrantLock();
            @Override
            protected Class<?> loadClass(final String name, boolean resolve) throws ClassNotFoundException {
                lock.lock();
                try {
                    return bundle.loadClass(name);
                } finally {
                    lock.unlock();
                }

            }

            @Override
            public URL getResource(String name) {
                URL result = bundle.getResource(name);

                if (result != null) return result;
                return null;
            }

            @Override
            public Enumeration<URL> getResources(String name) throws IOException {
                Enumeration<URL> resources = bundle.getResources(name);
                if (resources==null) {
                    // This check is needed, because ClassLoader.getResources()
                    // expects us to return an empty enumeration.
                    resources = EMPTY_URLS;
                }

                return resources;
            }

            @Override
            public String toString() {
                return "Class Loader for Bundle [" + bundle.toString() + " ]";
            }
        };
    }

    @Override
    public void addImport(HK2Module module) {
        throw new UnsupportedOperationException("This method can't be implemented in OSGi environment");
    }

    @Override
    public HK2Module addImport(ModuleDependency dependency) {
        throw new UnsupportedOperationException("This method can't be implemented in OSGi environment");
    }

    @Override
    public boolean isSticky() {
        return true; // all modules are always sticky
    }

    @Override
    public void setSticky(boolean sticky) {
        // NOOP: It's not required in OSGi.
    }

    @Override
    public List<HK2Module> getImports() {
        List<HK2Module> result = new ArrayList<>();
        RequiredBundle[] requiredBundles =
                registry.getPackageAdmin().getRequiredBundles(bundle.getSymbolicName());
        if (requiredBundles!=null) {
            for(RequiredBundle rb : requiredBundles) {
                HK2Module m = registry.getModule(rb.getBundle());
                if (m!=null) {
                    // module is known to the module system
                    result.add(m);
                } else {
                    // module is not known to us - may be the OSgi bundle depends on a native
                    // OSGi bundle
                }
            }

        }
        return result;
    }

    @Override
    public boolean isShared() {
        return true; // all OSGi bundles are always shared.
    }

    public Bundle getBundle() {
        return bundle;
    }

    public boolean isTransientlyActive() {
        return isTransientlyActive;
    }

    @Override
    public String toString() {
        return "OSGiModuleImpl:: Bundle = [" + bundle
                + "], State = [" + getState() + "]";
    }

    @Override
    public int hashCode() {
        return bundle.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof OSGiModuleImpl) {
            return bundle.equals(OSGiModuleImpl.class.cast(obj).bundle);
        }
        return false;
    }

    protected void setBundle(Bundle bundle) {
        /*
         * This method is purposefully not made synchronized as this can be called like this:
         *
         * thread #1: is calling this.init() and has held the lock, but is waiting for Obr to deploy.
         * thread #2: is deploying some bundles using Obr and as part of that is trying to call setBundle on this module.
         */
        if (this.bundle != null && this.bundle != bundle) {
            throw new RuntimeException("setBundle called with bundle [" + bundle + "] where as module [" + this +
                    "] is already associated with bundle [" + this.bundle + "]");
        } else {
            this.bundle = bundle;

            logger.logp(Level.INFO, "OSGiModuleImpl", "setBundle", "module [{0}] is now associated with bundle [{1}]",
                    new Object[]{this, bundle});
        }
    }

}

