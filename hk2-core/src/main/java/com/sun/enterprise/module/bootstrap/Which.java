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

package com.sun.enterprise.module.bootstrap;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * Finds out where a class file is loaded from.
 *
 * @author dochez
 */
public class Which {
    public static File jarFile(final Class clazz) throws IOException {
        final String resourceName = clazz.getName().replace(".","/")+".class";
        URL resource = AccessController.doPrivileged(new PrivilegedAction<URL>() {
            @Override
            public URL run() {
                return clazz.getClassLoader().getResource(resourceName);
            }
        });
        if (resource==null) {
            throw new IllegalArgumentException("Cannot get bootstrap path from "+ clazz + " class location, aborting");
        }

        if (resource.getProtocol().equals("jar")) {
            try {
                JarURLConnection c = (JarURLConnection) resource.openConnection();
                URL jarFile = c.getJarFileURL();
                try {
                    return new File(jarFile.toURI());
                } catch (URISyntaxException e) {
                    return new File(jarFile.getPath());
                }
            } catch (IOException e) {
                throw new IllegalArgumentException("Cannot open jar file "+resource, e);
            }
        } else
            throw new IllegalArgumentException("Don't support packaging "+resource+" , please contribute !");
    }
}
