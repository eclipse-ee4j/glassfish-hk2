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

package com.sun.enterprise.module.common_impl;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.zip.ZipException;

import com.sun.enterprise.module.InhabitantsDescriptor;
import com.sun.enterprise.module.ModuleMetadata;

/**
 * Abstraction of {@link JarFile} so that we can handle
 * both a jar file and a directory image transparently.
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class Jar {
    protected Jar() {}

    /**
     * See {@link JarFile#getManifest()} for the contract.
     */
    public abstract Manifest getManifest() throws IOException;

    /**
     * Loads all <tt>META-INF/habitats</tt> entries and store them to the list.
     */
    public abstract void loadMetadata(ModuleMetadata result);

    /**
     * Gets the base name of the jar.
     *
     * <p>
     * For example, "bar" for "bar.jar".
     */
    public abstract String getBaseName();

    public static Jar create(File file) throws IOException {
        if(file.isDirectory())
            return new Directory(file);
        else
            return new Archive(file);
    }

    private static final class Directory extends Jar {
        private final File dir;

        public Directory(File dir) {
            this.dir = dir;
        }

        public Manifest getManifest() throws IOException {
            File mf = new File(dir,JarFile.MANIFEST_NAME);
            if(mf.exists()) {
                FileInputStream in = new FileInputStream(mf);
                try {
                    return new Manifest(in);
                } finally {
                    in.close();
                }
            } else {
                return null;
            }
        }

        private File[] fixNull(File[] f) {
            if(f==null) return new File[0];
            else        return f;
        }

        public void loadMetadata(ModuleMetadata result) {
        }

        public String getBaseName() {
            return dir.getName();
        }

        private byte[] readFully(File f) throws IOException {
            byte[] buf = new byte[(int)f.length()];
            DataInputStream in = new DataInputStream(new FileInputStream(f));
            try {
                in.readFully(buf);
                return buf;
            } finally {
                in.close();
            }
        }
    }

    private static final class Archive extends Jar {
        private final JarFile jar;
        private final File file;

        public Archive(File jar) throws IOException {
            try {
                this.jar = new JarFile(jar);
                this.file = jar;
            } catch (ZipException e) {
                // ZipException doesn't include this crucial information, so rewrap
                IOException x = new IOException("Failed to open " + jar);
                x.initCause(e);
                throw x;
            }
        }

        public Manifest getManifest() throws IOException {
            return jar.getManifest();
        }

        public void loadMetadata(ModuleMetadata result) {
        }

        public String getBaseName() {
            String name = file.getName();
            int idx = name.lastIndexOf('.');
            if(idx>=0)
                name = name.substring(0,idx);
            return name;
        }
    }
}
