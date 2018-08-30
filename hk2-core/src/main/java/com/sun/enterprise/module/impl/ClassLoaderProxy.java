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

package com.sun.enterprise.module.impl;

import com.sun.enterprise.module.common_impl.FlattenEnumeration;

import java.net.URLClassLoader;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.io.IOException;

/**
 * ClassLoaderProxy capable of loading classes from itself but also from other class loaders
 *
 * @author Jerome Dochez
 */
public class ClassLoaderProxy extends URLClassLoader {

    private final List<ClassLoader> surrogates = new CopyOnWriteArrayList<ClassLoader>();
    private final List<ClassLoaderFacade> facadeSurrogates = new CopyOnWriteArrayList<ClassLoaderFacade>();

    /** Creates a new instance of ClassLoader */
    public ClassLoaderProxy(URL[] shared, ClassLoader parent) {
        super(shared, parent);
    }

    protected void finalize() throws Throwable {
        super.finalize();
        stop();
    }

    protected Class<?> loadClass(String name, boolean resolve, boolean followImports)
            throws ClassNotFoundException {
        // First, check if the class has already been loaded
        Class c = findLoadedClass(name);
        if (c == null) {
            try {
                if (getParent() != null) {
                    c = getParent().loadClass(name);
                }
            } catch (ClassNotFoundException e) {

            }
            if (c == null) {
                c = findClass(name, followImports);
            }
            if (resolve) {
                resolveClass(c);
            }
        } else {
            if (c.getClassLoader() == this) {
                return c;
            } else throw new ClassNotFoundException(name);
        }

        return c;
    }

    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return findClass(name, true);
    }

    protected Class<?> findClass(String name, boolean followImports) throws ClassNotFoundException {
        try {
            // try to find it within this module first.
            // this potentially causes a problem when two modules have the same jar in the classpath,
            // but because the classes are most often found locally, this has a tremendous performance boost.
            // so we knowingly make this decision to do child-first loading.

            // the pain of duplicate jars are somewhat mitigated by the fact that dependencies tend to be
            // defined between HK2 modules, and those will not show up in the classpath of this module.
            return findClassDirect(name);
        } catch(ClassNotFoundException cfne) {
            if (followImports) {
                Class c=null;
                for (ClassLoaderFacade classLoader : facadeSurrogates) {
                    try {
                        c = classLoader.getClass(name);
                    } catch(ClassNotFoundException e) {
                        // ignored.
                    }
                    if (c!=null) {
                        return c;
                    }
                }
                for (ClassLoader classLoader : surrogates) {
                    try {
                        c = classLoader.loadClass(name);
                    } catch(ClassNotFoundException e) {
                        // ignored.
                    }
                    if (c!=null) {
                        return c;
                    }
                }
            }
            throw cfne;
        }
    }

    /**
     * {@link #findClass(String)} except the classloader punch-in hack.
     */
    /*package*/ synchronized Class findClassDirect(String name) throws ClassNotFoundException {
        Class c = findLoadedClass(name);
        if(c!=null) return c;
        try {
            return super.findClass(name);
        } catch (NoClassDefFoundError e) {
            throw new ClassNotFoundException(e.getMessage());
        }
    }

    public URL findResource(String name) {
        URL url = super.findResource(name);
        if (url!=null)  return url;

        for (ClassLoaderFacade classLoader : facadeSurrogates) {
            url = classLoader.findResourceDirect(name);
            if (url!=null) {
                return url;
            }
        }
        for (ClassLoader classLoader : surrogates) {
            url = classLoader.getResource(name);
            if (url!=null) {
                return url;
            }
        }
        return null;
    }

    /**
     * Works like {@link #findResource(String)} but only looks at
     * this module, without delegating to ancestors.
     */
    public URL findResourceDirect(String name) {
        return super.findResource(name);
    }

    public Enumeration<URL> findResources(String name) throws IOException {
        // Let's build our list of enumerations first.
        Vector<Enumeration<URL>> sources = new Vector<Enumeration<URL>>();
        Enumeration< URL> enumerat = super.findResources(name);
        if (enumerat!=null && enumerat.hasMoreElements()) {
             sources.add(enumerat);
        }
        for (ClassLoaderFacade classLoader : facadeSurrogates) {
            enumerat = classLoader.getResources(name);
            if (enumerat!=null && enumerat.hasMoreElements()) {
                sources.add(enumerat);
            }
        }
        for (ClassLoader classLoader : surrogates) {
            enumerat = classLoader.getResources(name);
            if (enumerat!=null && enumerat.hasMoreElements()) {
                sources.add(enumerat);
            }
        }
        // return a flattened enumeration now.
        return new FlattenEnumeration<URL>(sources.elements());
    }

    public void addDelegate(ClassLoader cl) {
        if (cl instanceof ClassLoaderFacade) {
            facadeSurrogates.add((ClassLoaderFacade) cl);
        } else {
            surrogates.add(cl);
        }
    }

    public void removeDelegate(ClassLoader cl) {
        if (cl instanceof ClassLoaderFacade) {
            facadeSurrogates.remove(cl);
        } else {
            surrogates.remove(cl);
        }
    }

    public Collection<ClassLoader> getDelegates() {
        return new ArrayList<ClassLoader>(surrogates);
    }


    /**
     * called by the facade class loader when it is garbage collected.
     * this is a good time to see if this module should be unloaded.
     */
    public void stop() {
       surrogates.clear();
       facadeSurrogates.clear();
    }

    public String toString() {
        StringBuffer s= new StringBuffer();
        s.append(",URls[]=");
        for (URL url : getURLs()) {
            s.append(url).append(",");
        }
        s.append(")");

        for (ClassLoader surrogate : surrogates) {
            s.append("\n ref : ").append(surrogate.toString());
        }
        return s.toString();
    }

      /**
       * Appends the specified URL to the list of URLs to search
       * for classes and resources.
       *
       * @param url the url to append
       */
    public void addURL(URL url) {
        super.addURL(url);
    }
}
