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

import static org.jvnet.hk2.osgiadapter.Logger.logger;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.SynchronousBundleListener;

import com.sun.enterprise.module.HK2Module;
import com.sun.enterprise.module.ModuleDefinition;

/**
 * This is an implementation of {@link com.sun.enterprise.module.ModulesRegistry}.
 * It uses OSGi extender pattern to do necessary parsing of OSGi bundles.
 *
 * @author Sanjeeb.Sahoo@Sun.COM
 */
public class OSGiModulesRegistryImpl
        extends AbstractOSGiModulesRegistryImpl
        implements SynchronousBundleListener {

    private final ReentrantLock lock = new ReentrantLock();

    ModuleDefinitionCacheSingleton cache = ModuleDefinitionCacheSingleton.getInstance();

    /*package*/ OSGiModulesRegistryImpl(BundleContext bctx) {
        super(bctx);

        // Need to add a listener so that we get notification about
        // bundles that get installed/uninstalled from now on...
        // This must happen before we start iterating the existing bundles.
        bctx.addBundleListener(this);

        // Populate registry with pre-installed bundles
        for (final Bundle b : bctx.getBundles()) {
            if (b.getLocation().equals (org.osgi.framework.Constants.SYSTEM_BUNDLE_LOCATION)) {
                continue;
            }
            try {
                add(makeModule(b)); // call add as it processes provider names
            } catch (Exception e) {
                logger.logp(Level.WARNING, "OSGiModulesRegistryImpl",
                        "OSGiModulesRegistryImpl",
                        "Not able convert bundle [{0}] having location [{1}] " +
                                "to module because of exception: {2}",
                        new Object[]{b, b.getLocation(), e});
            }
        }

        if (cache.isCacheInvalidated()) {
            try {
                cache.saveCache();
            } catch (IOException e) {
                logger.logp(Level.WARNING, "OSGiModulesRegistryImpl", "OSGiModulesRegistryImpl", "Could not save module definition cache",e);
            }
        }
    }

    @Override
    public void bundleChanged(BundleEvent event) {
        // Extender implementation.
        try {
            final Bundle bundle = event.getBundle();
            switch (event.getType()) {
                case BundleEvent.INSTALLED : {
                    if (logger.isLoggable(Level.FINE)) {
                        logger.log(Level.FINE, "[{0}] {1} installed", new Object[]{bundle.getBundleId(), bundle.getSymbolicName()});
                    }
                    break;
                } 

                case BundleEvent.RESOLVED :
                {
                    // call add as it processes provider names
                    OSGiModuleImpl m = makeModule(bundle);
                    add(m);
                    break;
                }
                case BundleEvent.UNINSTALLED :
                {
                    final HK2Module m = getModule(bundle);
                    
                    if (m!=null) {
                        // getModule can return null if some bundle got uninstalled
                        // before we have finished initialization. This can
                        // happen if framework APIs are called in parallel
                        // by some third party bundles.
                        // We need to call remove as it processes provider names
                        // and updates the cache.
                        remove(m);
                    }
                    break;
                }
                case BundleEvent.UPDATED :
                    final HK2Module m = getModule(bundle);
                    if (m!=null) {
                        // getModule can return null if some bundle got uninstalled
                        // before we have finished initialization. This can
                        // happen if framework APIs are called in parallel
                        // by some third party bundles.
                        // We need to call remove as it processes provider names
                        // and updates the cache.
                        remove(m);
                    }

                    // make a new module from the updated bundle data and add it
                    add(makeModule(bundle));
                    break;
            }
        } catch (Exception e) {
            logger.logp(Level.WARNING, "OSGiModulesRegistryImpl", "bundleChanged", e.getMessage(), e);
        }
    }

    // Factory method
    private OSGiModuleImpl makeModule(Bundle bundle) throws IOException, URISyntaxException {
        final OSGiModuleDefinition md = makeModuleDef(bundle);

        OSGiModuleImpl m = new OSGiModuleImpl(this, bundle, md);

        return m;
    }

    // Factory method
    private OSGiModuleDefinition makeModuleDef(Bundle bundle)
            throws IOException, URISyntaxException {
        URI key = OSGiModuleDefinition.toURI(bundle);

        ModuleDefinition md = cache.get(key);

        if (md != null) {
        	return OSGiModuleDefinition.class.cast(md);
        } else {
            cache.invalidate();
            md = new OSGiModuleDefinition(bundle);

            cache.cacheModuleDefinition(key, md);

            return (OSGiModuleDefinition) md;
        }
    }

    @Override
    protected void add(HK2Module newModule) {
        lock.lock();
        try {
            // It is overridden to make it synchronized as it is called from
            // BundleListener.
            super.add(newModule);
            // don't set cacheInvalidated = true here, as this method is called while iterating initial
            // set of bundles when this module is started. Instead, we invalidate the cache makeModuleDef().
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void remove(HK2Module module) {
        lock.lock();
        try {
            // It is overridden to make it synchronized as it is called from
            // BundleListener.
            super.remove(module);

            // Update cache. 
            final URI location = module.getModuleDefinition().getLocations()[0];

            cache.remove(location);
        } finally {
            lock.unlock();
        }
    }

    // factory method
    @Override
    protected HK2Module newModule(ModuleDefinition moduleDef) {
        String location = moduleDef.getLocations()[0].toString();
        try {
            if (logger.isLoggable(Level.FINE)) {
                logger.logp(Level.FINE, "OSGiModulesRegistryImpl", "newModule",
                    "location = {0}", location);
            }
            File l = new File(moduleDef.getLocations()[0]);
            if (l.isDirectory()) {
                location = "reference:" + location;
            }
            Bundle bundle = bctx.installBundle(location);
            // wrap Bundle by a Module object
            return new OSGiModuleImpl(this, bundle, moduleDef);
        } catch (BundleException e) {
            logger.logp(Level.WARNING, "OSGiModulesRegistryImpl", "newModule",
                    "Exception {0} while adding location = {1}", new Object[]{e, location});
//            throw new RuntimeException(e); // continue
        }
        return null;
    }

    @Override
    public void shutdown() {
        lock.lock();
        try {
            for (HK2Module m : modules.values()) {
                // Only stop modules that were started after ModulesRegistry
                // came into existence.
                if (OSGiModuleImpl.class.cast(m).isTransientlyActive()) {
                     m.stop();
                }
            }
            
            // Save the cache before clearing modules
            try {
                cache.saveCache();
            } catch (IOException e) {
                Logger.logger.log(Level.WARNING, "Cannot save metadata to cache", e);
                }

            bctx.removeBundleListener(this);

            super.shutdown();
        } finally {
            lock.unlock();
        }
    }

    protected String getProperty(String property) {
        String value = bctx.getProperty(property);
        // Check System properties to work around Equinox Bug:
        // https://bugs.eclipse.org/bugs/show_bug.cgi?id=320459
        if (value == null) value = System.getProperty(property);
        return value;
    }

}
