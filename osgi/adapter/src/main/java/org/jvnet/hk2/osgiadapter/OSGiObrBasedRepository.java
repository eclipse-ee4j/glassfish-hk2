/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
import com.sun.enterprise.module.Repository;
import com.sun.enterprise.module.RepositoryChangeListener;
import org.apache.felix.bundlerepository.Resource;
import org.osgi.framework.Version;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sanjeeb.Sahoo@Sun.COM
 */
public class OSGiObrBasedRepository implements Repository {

    private org.apache.felix.bundlerepository.Repository obr;

    public OSGiObrBasedRepository(org.apache.felix.bundlerepository.Repository obr) {
        this.obr = obr;
    }

    @Override
    public String getName() {
        return obr.getName();
    }

    @Override
    public URI getLocation() {
        return URI.create(obr.getURI());
    }

    @Override
    public ModuleDefinition find(String name, String version) {
        List<ModuleDefinition> mds = findAll(name, version);
        return mds.isEmpty() ? null : mds.get(0);
    }

    @Override
    public List<ModuleDefinition> findAll() {
        return findAll(null, null);
    }

    private ModuleDefinition makeModuleDef(File jar) throws IOException {
        return new OSGiModuleDefinition(jar);
    }

    @Override
    public List<ModuleDefinition> findAll(String name) {
        return findAll(name, null);
    }

    private List<ModuleDefinition> findAll(String name, String version) {
        List<ModuleDefinition> mds = new ArrayList<ModuleDefinition>();
        for (Resource resource : obr.getResources()) {
            if (name != null) {
                final String rsn = resource.getSymbolicName();
                final Version rv = resource.getVersion();
                boolean versionMatching = (version == null) || version.equals(rv.toString());
                boolean nameMatching = name.equals(rsn);
                if (nameMatching && versionMatching) {
                    try {
                        final URI uri = URI.create(resource.getURI());
                        mds.add(makeModuleDef(new File(uri)));
                    } catch (IOException e) {
                        throw new RuntimeException(e); // TODO(Sahoo): Proper Exception Handling
                    }
                }
            }
        }
        return mds;
    }

    @Override
    public void initialize() throws IOException {
        // obr.xml is already available
    }

    @Override
    public void shutdown() throws IOException {
        // no-op, since we don't do anything in initialize()
    }

    @Override
    public List<URI> getJarLocations() {
        List<URI> uris = new ArrayList<URI>();
        for (Resource resource : obr.getResources()) {
            final URI e = URI.create(resource.getURI());
            uris.add(e);
        }
        return uris;
    }

    @Override
    public boolean addListener(RepositoryChangeListener listener) {
        return false;  // not supported
    }

    @Override
    public boolean removeListener(RepositoryChangeListener listener) {
        return false;  // not supported
    }

}
