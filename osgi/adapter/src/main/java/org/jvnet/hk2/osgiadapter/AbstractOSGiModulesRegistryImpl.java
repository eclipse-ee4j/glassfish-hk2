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

package org.jvnet.hk2.osgiadapter;

import com.sun.enterprise.module.*;
import com.sun.enterprise.module.bootstrap.BootException;
import com.sun.enterprise.module.common_impl.AbstractModulesRegistryImpl;
import com.sun.enterprise.module.common_impl.CompositeEnumeration;
import org.glassfish.hk2.api.*;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.utilities.DescriptorImpl;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.osgi.framework.*;
import org.osgi.service.packageadmin.PackageAdmin;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.logging.Level;

import static org.glassfish.hk2.utilities.ServiceLocatorUtilities.createDynamicConfiguration;
import static org.jvnet.hk2.osgiadapter.Logger.logger;

/**
 * @author sanjeeb.sahoo@oracle.com
 */
public abstract class AbstractOSGiModulesRegistryImpl extends AbstractModulesRegistryImpl {
    /**
     * OSGi BundleContext - used to install/uninstall, start/stop bundles
     */
    BundleContext bctx;
    protected PackageAdmin pa;
    private Map<ModuleChangeListener, BundleListener> moduleChangeListeners =
            new HashMap<ModuleChangeListener, BundleListener>();
    private Map<ModuleLifecycleListener, BundleListener> moduleLifecycleListeners =
            new HashMap<ModuleLifecycleListener, BundleListener>();

    protected AbstractOSGiModulesRegistryImpl(BundleContext bctx) {
        super(null);
        this.bctx = bctx;
        ServiceReference ref = bctx.getServiceReference(PackageAdmin.class.getName());
        pa = PackageAdmin.class.cast(bctx.getService(ref));
    }

    @Override
    public void shutdown() {
        modules.clear();

        for (Repository repo : repositories.values()) {
            try {
                repo.shutdown();
            } catch(Exception e) {
                java.util.logging.Logger.getAnonymousLogger().log(Level.SEVERE, "Error while closing repository " + repo, e);
                // swallows
            }
        }
        // don't try to stop the system bundle, as we may be embedded inside
        // something like Eclipse.
    }

    public List<ActiveDescriptor> parseInhabitants(
            HK2Module module, String name, ServiceLocator serviceLocator, List<PopulatorPostProcessor> postProcessors)
            throws IOException, BootException {

        OSGiModuleImpl osgiModuleImpl = (OSGiModuleImpl) module;

        List<ActiveDescriptor> activeDescriptors;

        Map<String, List<Descriptor>> descriptorMap = module.getModuleDefinition().getMetadata().getDescriptors();

        List<Descriptor> descriptors = descriptorMap.get(name);

        if (descriptors == null) {
            activeDescriptors = osgiModuleImpl.parseInhabitants(name, serviceLocator, postProcessors);

            if (activeDescriptors != null) {

                // use the copy constructor to create (nonactive) descriptor for serialization into the cache
                descriptors = new ArrayList<Descriptor>();
                for (Descriptor d : activeDescriptors) {
                    descriptors.add(new DescriptorImpl(d));
                }

                module.getModuleDefinition().getMetadata().addDescriptors(name, descriptors);

            }
        } else {
            activeDescriptors = new ArrayList<ActiveDescriptor>();

            DynamicConfiguration dcs = createDynamicConfiguration(serviceLocator);
            for (Descriptor descriptor : descriptors) {
                
                DescriptorImpl di = (descriptor instanceof DescriptorImpl) ? (DescriptorImpl) descriptor : new DescriptorImpl(descriptor) ;

                // set the hk2loader
                DescriptorImpl descriptorImpl = new OsgiPopulatorPostProcessor(osgiModuleImpl).process(serviceLocator, di);

                if (descriptorImpl != null) {
                    activeDescriptors.add(dcs.bind(descriptorImpl, false));
                }
            }

            dcs.commit();
        }

        return activeDescriptors;

    }

    public ModulesRegistry createChild() {
        throw new UnsupportedOperationException("Not Yet Implemented"); // TODO(Sahoo)
    }

    public synchronized void detachAll() {
        for (HK2Module m : modules.values()) {
            m.detach();
        }
    }

    /**
     * Sets the classloader parenting the class loaders created by the modules
     * associated with this registry.
     * @param parent parent class loader
     */
    public void setParentClassLoader(ClassLoader parent) {
        throw new UnsupportedOperationException("This method can't be implemented in OSGi environment");
    }

    /**
     * Returns the parent class loader parenting the class loaders created
     * by modules associated with this registry.
     * @return the parent classloader
     */
    public ClassLoader getParentClassLoader() {
        return Bundle.class.getClassLoader();
    }

    /**
     * Returns a ClassLoader capable of loading classes from a set of modules identified
     * by their module definition and also load new urls.
     *
     * @param parent the parent class loader for the returned class loader instance
     * @param mds module definitions for all modules this classloader should be
     *        capable of loading
     * @param urls urls to be added to the module classloader
     * @return class loader instance
     * @throws com.sun.enterprise.module.ResolveError if one of the provided module
     *         definition cannot be resolved
     */
    public ClassLoader getModulesClassLoader(final ClassLoader parent,
                                             Collection<ModuleDefinition> mds,
                                             URL[] urls) throws ResolveError {
        final List<ClassLoader> delegateCLs = new ArrayList<ClassLoader>();
        final List<HK2Module> delegateModules = new ArrayList<HK2Module>();
        for (ModuleDefinition md : mds) {
            HK2Module m = makeModuleFor(md.getName(), md.getVersion());
            delegateModules.add(m);
            delegateCLs.add(m.getClassLoader());
        }
        return new URLClassLoader(urls!=null?urls:new URL[0], parent) {
            /*
             * This is a delegating class loader.
             * This extends URLClassLoader, because web layer (Jasper to be specific)
             * expects it to be a URLClassLoader so that it can extract Classpath information
             * used for javac.
             * It always delegates to a chain of OSGi bundle's class loader.
             * ClassLoader.defineClass() is never called in the context of this class.
             * There will never be a class for which getClassLoader()
             * would return this class loader.
             */
            @Override
            public URL[] getURLs() {
                List<URL> result = new ArrayList<URL>();
                if (parent instanceof URLClassLoader) {
                    URL[] parentURLs = URLClassLoader.class.cast(parent).getURLs();
                    result.addAll(Arrays.asList(parentURLs));
                }
                for (HK2Module m : delegateModules) {
                    ModuleDefinition md = m.getModuleDefinition();
                    URI[] uris = md.getLocations();
                    URL[] urls = new URL[uris.length];
                    for (int i = 0; i < uris.length; ++i) {
                        try {
                            urls[i] = uris[i].toURL();
                        } catch (MalformedURLException e) {
                            logger.warning("Exception " + e + " while converting " + uris[i] + " to URL");
                        }
                    }
                    result.addAll(Arrays.asList(urls));
                }
                return result.toArray(new URL[0]);
            }

            @Override
            protected Class<?> findClass(String name) throws ClassNotFoundException {
                for (ClassLoader delegate : delegateCLs) {
                    try {
                        return delegate.loadClass(name);
                    } catch(ClassNotFoundException e) {
                        // This is expected, so ignore
                    }
                }
                throw new ClassNotFoundException(name);
            }

            @Override
            public URL findResource(String name) {
                URL resource = null;
                for (ClassLoader delegate : delegateCLs) {
                    resource = delegate.getResource(name);
                    if (resource != null) {
                        return resource;
                    }
                }
                return resource;
            }

            @Override
            public Enumeration<URL> findResources(String name) throws IOException {
                List<Enumeration<URL>> enumerators = new ArrayList<Enumeration<URL>>();
                for (ClassLoader delegate : delegateCLs) {
                    Enumeration<URL> enumerator = delegate.getResources(name);
                    enumerators.add(enumerator);
                }
                return new CompositeEnumeration(enumerators);
            }

        };
    }

    /**
     * Returns a ClassLoader capable of loading classes from a set of modules identified
     * by their module definition
     *
     * @param parent the parent class loader for the returned class loader instance
     * @param defs module definitions for all modules this classloader should be
     *        capable of loading classes from
     * @return class loader instance
     * @throws ResolveError if one of the provided module
     *         definition cannot be resolved
     */
    public ClassLoader getModulesClassLoader(ClassLoader parent,
                                             Collection<ModuleDefinition> defs)
        throws ResolveError {
        return getModulesClassLoader(parent, defs, null);
    }

    public HK2Module find(Class clazz) {
        Bundle b = pa.getBundle(clazz);
        if (b!=null) {
            return getModule(b);
        }
        return null;
    }

    public PackageAdmin getPackageAdmin() {
        return pa;
    }

    public void addModuleChangeListener(final ModuleChangeListener listener, final OSGiModuleImpl module) {
        BundleListener bundleListener = new BundleListener() {
            public void bundleChanged(BundleEvent event) {
                if ((event.getBundle() == module.getBundle()) &&
                        ((event.getType() & BundleEvent.UPDATED) == BundleEvent.UPDATED)) {
                    listener.changed(module);
                }
            }
        };
        bctx.addBundleListener(bundleListener);
        moduleChangeListeners.put(listener, bundleListener);
    }

    public boolean removeModuleChangeListener(ModuleChangeListener listener) {
        BundleListener bundleListener = moduleChangeListeners.remove(listener);
        if (bundleListener!= null) {
            bctx.removeBundleListener(bundleListener);
            return true;
        }
        return false;
    }

    public void register(final ModuleLifecycleListener listener) {
        // This is purposefully made an asynchronous bundle listener
        BundleListener bundleListener = new BundleListener() {
            public void bundleChanged(BundleEvent event) {
                switch (event.getType()) {
                    case BundleEvent.INSTALLED:
                        listener.moduleInstalled(getModule(event.getBundle()));
                        break;
                    case BundleEvent.UPDATED:
                        listener.moduleUpdated(getModule(event.getBundle()));
                        break;
                    case BundleEvent.RESOLVED:
                        listener.moduleResolved(getModule(event.getBundle()));
                        break;
                    case BundleEvent.STARTED:
                        listener.moduleStarted(getModule(event.getBundle()));
                        break;
                    case BundleEvent.STOPPED:
                        listener.moduleStopped(getModule(event.getBundle()));
                        break;
                }
            }
        };
        bctx.addBundleListener(bundleListener);
        moduleLifecycleListeners.put(listener,  bundleListener);
    }

    public void unregister(ModuleLifecycleListener listener) {
        BundleListener bundleListener = moduleLifecycleListeners.remove(listener);
        if (bundleListener!=null) {
            bctx.removeBundleListener(bundleListener);
        }
    }

    /*package*/ HK2Module getModule(Bundle bundle) {
        return modules.get(new OSGiModuleId(bundle));
    }
    
    public void remove(HK2Module module) {
        super.remove(module);
        
        if (!(module instanceof OSGiModuleImpl)) {
            return;
        }
        
        OSGiModuleImpl oModule = (OSGiModuleImpl) module;
        Bundle bundle = oModule.getBundle();
        
        String bsn = bundle.getSymbolicName();
        String version = bundle.getVersion().toString();
        
        Set<ServiceLocator> locators = getAllServiceLocators();
        
        for (ServiceLocator locator : locators) {
            if (!ServiceLocatorState.RUNNING.equals(locator.getState())) continue;
            
            ServiceLocatorUtilities.removeFilter(locator, new RemoveFilter(bsn, version));
        }
    }
    
    private static class RemoveFilter implements Filter {
        private final String bsn;
        private final String version;
        
        private RemoveFilter(String bsn, String version) {
            this.bsn = bsn;
            this.version = version;
        }
        
        private static String getMetadataValue(Descriptor d, String key) {
            Map<String, List<String>> metadata = d.getMetadata();
            
            List<String> values = metadata.get(key);
            if (values == null || values.size() <= 0) {
                return null;
            }
            
            return values.get(0);
        }

        @Override
        public boolean matches(Descriptor d) {
            String dBSN = getMetadataValue(d, OsgiPopulatorPostProcessor.BUNDLE_SYMBOLIC_NAME);
            if (dBSN == null || !dBSN.equals(bsn)) return false;
            
            String dVersion = getMetadataValue(d, OsgiPopulatorPostProcessor.BUNDLE_VERSION);
            if (dVersion == null) return false;
            
            return dVersion.equals(version);
        }
        
    }
}
