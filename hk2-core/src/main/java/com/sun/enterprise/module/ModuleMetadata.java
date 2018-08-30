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

import org.glassfish.hk2.api.Descriptor;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.*;

/**
 * Holds information about /META-INF/services and /META-INF/inhabitants for a {@link Module}.
 *
 * <p>
 * A Service implementation is identified by the service
 * interface it implements, the implementation class of that service interface
 * and the module in which that implementation resides.
 *
 * <p>
 * Note that since a single {@link ModuleDefinition} is allowed to be used
 * in multiple {@link Module}s, this class may not reference anything {@link Module}
 * specific.
 *
 * @author Kohsuke Kawaguchi
 */
public final class ModuleMetadata implements Serializable {

    /**
     * META-INF/hk2-locator/* cache
     */
    private Map<String, List<Descriptor>> descriptors = new HashMap<String, List<Descriptor>>();

    public Map<String, List<Descriptor>> getDescriptors() {
        return descriptors;
    }

    public synchronized void addDescriptors(String serviceLocatorName, Collection<Descriptor> descriptorsToAdd) {
        List<Descriptor> descriptorList = descriptors.get(serviceLocatorName);

        if (descriptorList == null) {
            descriptorList = new ArrayList<Descriptor>();

            descriptors.put(serviceLocatorName, descriptorList);
        }

        descriptorList.addAll(descriptorsToAdd);
    }

    public static final class Entry implements Serializable {
        public final List<String> providerNames = new ArrayList<String>();
        public final List<URL> resources = new ArrayList<URL>();

        /**
         * Loads a single service file.
         */
        private void load(URL source, InputStream is) throws IOException {
            this.resources.add(source);
            try {
                /*
                 * The format of service file is specified at
                 * http://java.sun.com/j2se/1.3/docs/guide/jar/jar.html#Service%20Provider
                 * According to the above spec,
                 * The file contains a list of fully-qualified binary names of
                 * concrete provider classes, one per line.
                 * Space and tab characters surrounding each name,
                 * as well as blank lines, are ignored.
                 * The comment character is '#' ('\u0023', NUMBER SIGN);
                 * on each line all characters following the first comment
                 * character are ignored. The file must be encoded in UTF-8.
                 */
                Scanner scanner = new Scanner(is);
                final String commentPattern = "#"; // NOI18N
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (!line.startsWith(commentPattern)) {
                        StringTokenizer st = new StringTokenizer(line);
                        while (st.hasMoreTokens()) {
                            providerNames.add(st.nextToken());
                            break; // Only one entry per line
                        }
                    }
                }
            } finally {
                is.close();
            }
        }

        public boolean hasProvider() {
            return !providerNames.isEmpty();
        }
    }

    /**
     * {@link Entry}s keyed by the service name.
     */
    private final Map<String,Entry> entries = new HashMap<String, Entry>();

    /*package*/
    public Entry getEntry(String serviceName) {
        Entry e = entries.get(serviceName);
        if(e==null) e = NULL_ENTRY;
        return e;
    }

    /*package*/
    public Iterable<Entry> getEntries() {
        return entries.values();
    }

    public List<URL> getDescriptors(String serviceName) {
        return getEntry(serviceName).resources;
    }

    public void load(URL source, String serviceName) throws IOException {
        load(source,serviceName,source.openStream());
    }

    public void load(URL source, String serviceName, InputStream is) throws IOException {
        Entry e = entries.get(serviceName);
        if(e==null) {
            e = new Entry();
            entries.put(serviceName,e);
            e.load(source,is);
        } else {
          is.close();
        }
    }

    /**
     * Empty Entry used to indicate that there's no service.
     * This is mutable, so its working correctly depends on the good will of the callers.
     */
    private static final Entry NULL_ENTRY = new Entry();
}
