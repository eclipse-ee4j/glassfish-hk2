/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import org.glassfish.hk2.api.DescriptorFileFinder;
import org.glassfish.hk2.api.DescriptorFileFinderInformation;
import org.glassfish.hk2.utilities.reflection.Logger;

/**
 * This is an implementation of {@link DescriptorFileFinder} that
 * uses a particular classloader in order to find descriptor files.
 * @author jwells
 *
 */
public class ClasspathDescriptorFileFinder implements DescriptorFileFinder, DescriptorFileFinderInformation {
    private final static String DEBUG_DESCRIPTOR_FINDER_PROPERTY = "org.jvnet.hk2.properties.debug.descriptor.file.finder";
    private final static boolean DEBUG_DESCRIPTOR_FINDER = AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
        @Override
        public Boolean run() {
            return Boolean.parseBoolean(System.getProperty(DEBUG_DESCRIPTOR_FINDER_PROPERTY, "false"));
        }
            
    });
    
    private final static String DEFAULT_NAME = "default";

    private final ClassLoader classLoader;
    private final String names[];
    private final ArrayList<String> identifiers = new ArrayList<String>();
    
    /**
     * If this constructor is used then HK2 descriptor files will be found
     * by looking in the classpath of the process.  The classloader used
     * will be the classloader for this class itself.  The names of
     * the files found will be META-INF/hk2-locator/default.
     * <p>
     * This is most commonly used when using HK2 from a stand-alone client in
     * which all the JAR files are on a single classpath
     */
    public ClasspathDescriptorFileFinder() {
        this(ClasspathDescriptorFileFinder.class.getClassLoader(), DEFAULT_NAME);
    }
    
    /**
     * This constructor can be used to select the particular classloader
     * to search for HK2 descriptor files.  The names of the the files
     * found in this classloader will be META-INF/hk2-locator/default.
     * <p>
     * This is commonly used in more complex classloading scenarios where
     * the HK2 descriptor files are not necessarily on the system classpath.
     * 
     * @param cl May not be null and must be the classloader to use when
     * searching for HK2 descriptor files
     */
    public ClasspathDescriptorFileFinder (ClassLoader cl) {
        this(cl, DEFAULT_NAME);
    }
    
    /**
     * This constructor can be used to select the particular classloader
     * to search for HK2 descriptor files.  The names of the the files
     * found in this classloader will be META-INF/hk2-locator/name.
     *  
     * @param cl May not be null and must be the classloader to use when
     * searching for HK2 descriptor files
     * @param names May not be null and must be the name of the files to
     * search for in the META-INF/hk2-locator directory
     */
    public ClasspathDescriptorFileFinder (ClassLoader cl, String... names) {
        this.classLoader = cl;
        this.names = names;
    }

    /**
     * Simple implementation of the findDescriptorFiles which does a
     * simple getResources on the classloader in order to find the
     * hk2 descriptor files
     */
    @Override
    public List<InputStream> findDescriptorFiles() throws IOException {
        identifiers.clear();
        
        ArrayList<InputStream> returnList = new ArrayList<InputStream>();
        
        for (String name : names) {
            Enumeration<URL> e = classLoader.getResources(RESOURCE_BASE+name);

            for (; e.hasMoreElements();) {
                URL url = e.nextElement();
                
                if (DEBUG_DESCRIPTOR_FINDER) {
                    Logger.getLogger().debug("Adding in URL to set being parsed: " + url + " from " + RESOURCE_BASE+name);
                }
                try {
                    identifiers.add(url.toURI().toString());
                }
                catch (URISyntaxException e1) {
                    throw new IOException(e1);
                }
                
                InputStream inputStream;
                try {
                    inputStream = url.openStream();
                }
                catch (IOException ioe) {
                    if (DEBUG_DESCRIPTOR_FINDER) {
                        Logger.getLogger().debug("IOException for url " + url, ioe);
                    }
                    throw ioe;
                }
                catch (Throwable th) {
                    if (DEBUG_DESCRIPTOR_FINDER) {
                        Logger.getLogger().debug("Unexpected exception for url " + url, th);
                    }
                    throw new IOException(th);
                }
                
                if (DEBUG_DESCRIPTOR_FINDER) {
                    Logger.getLogger().debug("Input stream for: " + url + " from " + RESOURCE_BASE+name + " has succesfully been opened");
                }
                returnList.add(inputStream);
            }
        }
        
        return returnList;
    }
    
    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.DescriptorFileFinderInformation#getDescriptorFileInformation()
     */
    @Override
    public List<String> getDescriptorFileInformation() {
        return identifiers;
    }
    
    public String toString() {
        return "ClasspathDescriptorFileFinder(" + classLoader + "," + Arrays.toString(names) + "," + System.identityHashCode(this) + ")";
    }

    
}
