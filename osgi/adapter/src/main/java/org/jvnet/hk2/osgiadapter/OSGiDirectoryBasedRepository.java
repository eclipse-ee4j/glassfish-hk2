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

package org.jvnet.hk2.osgiadapter;

import com.sun.enterprise.module.ModuleDefinition;
import com.sun.enterprise.module.common_impl.DirectoryBasedRepository;
import com.sun.enterprise.module.common_impl.ModuleId;
import org.osgi.framework.Constants;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Level;

import static org.jvnet.hk2.osgiadapter.Logger.logger;

/**
 * Only OSGi bundles are recognized as modules.
 * 
 * @author Sanjeeb.Sahoo@Sun.COM
 */
public class OSGiDirectoryBasedRepository extends DirectoryBasedRepository {

    ModuleDefinitionCacheSingleton cache = ModuleDefinitionCacheSingleton.getInstance();

    public OSGiDirectoryBasedRepository(String name, File repository) {
        this(name, repository, true);
    }

    public OSGiDirectoryBasedRepository(String name, File repository, boolean isTimerThreadDaemon) {
        super(name, repository, isTimerThreadDaemon);
    }

    @Override
    public void initialize() throws IOException {
        super.initialize();
    }

    /**
     * This class overrides this mthod, because we don't support the following cases:
     * 1. external manifest.mf file for a jar file
     * 2. jar file exploded as a directory.
     * Both the cases are supported in HK2, but not in OSGi.
     *
     * @param jar bundle jar
     * @return a ModuleDefinition for this bundle
     * @throws IOException
     */
    @Override
    protected ModuleDefinition loadJar(File jar) throws IOException {
        assert (jar.isFile()); // no support for exploded jar
        ModuleDefinition md = cache.get(jar.toURI());
        if (md != null) {
            if(logger.isLoggable(Level.FINE)) {
                logger.logp(Level.FINER, "OSGiDirectoryBasedRepository", "loadJar", "Found in mdCache for {0}", new Object[]{jar});
            }
            return md;
        }

        Manifest m = new JarFile(jar).getManifest();
        if (m != null) {
            cache.invalidate();

            // Needs to be added to the cache, cache needs to be saved (on shutdown?), but we want a BundleJar, not a Jar.Archive
            if (m.getMainAttributes().getValue(Constants.BUNDLE_SYMBOLICNAME) != null) {
                Logger.logger.logp(Level.FINE, "OSGiDirectoryBasedRepository", "loadJar",
                        "{0} is an OSGi bundle", new Object[]{jar});
                return newModuleDefinition(jar, null);
            }
        }
        return null;
    }

    @Override
    protected ModuleDefinition newModuleDefinition(File jar, Attributes attr) throws IOException {
        return new OSGiModuleDefinition(jar);
    }

    @Override
    protected void loadModuleDefs(Map<ModuleId, ModuleDefinition> moduleDefs, List<URI> libraries) throws IOException {
        if (cache.isCacheInvalidated()) {
          super.loadModuleDefs(moduleDefs, libraries);
        }
    }
}
