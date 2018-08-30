/*
 * Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.classmodel.reflect.util;

import org.glassfish.hk2.classmodel.reflect.Parser;

import java.io.*;
import java.nio.ByteBuffer;
import java.net.URI;
import java.nio.channels.FileChannel;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Directory base archive abstraction
 */
public class DirectoryArchive extends AbstractAdapter {
    public final File directory;
    public final Parser parser;

    public DirectoryArchive(Parser parser, File directory) {
        this.directory = directory;
        this.parser = parser;
    }

    @Override
    public String toString() {
      return getURI().toString();
    }
    
    @Override
    public URI getURI() {
        return directory.toURI();
    }


    @Override
    public Manifest getManifest() throws IOException {
        File manifest = new File(directory, JarFile.MANIFEST_NAME);
        if (manifest.exists()) {
            InputStream is = new BufferedInputStream(new FileInputStream(manifest));
            try {
                return new Manifest(is);
            } finally {
                is.close();
            }
        }
        return null;
    }

    @Override
    public void onSelectedEntries(Selector selector, EntryTask task, Logger logger) throws IOException {
        parse(directory, selector, task, logger);
    }

    private void parse(File dir, Selector selector, EntryTask task, Logger logger) throws IOException {
        File [] listFiles = dir.listFiles();
        if (null == listFiles) {
            System.err.println("listFiles() is null for: " + dir);
            return;
        }
      
        ByteBuffer buffer = ByteBuffer.allocate(52000);

        for (File f : listFiles) {
            Entry ae = new Entry(mangle(f), f.length(), f.isDirectory());
            if (!f.isDirectory()) {
                if (ae.name.endsWith(".jar")) {
                    JarArchive ja = null;
                    try {
                        ja = new JarArchive(parser, f.toURI());
                        ja.onSelectedEntries(selector, task, logger);
                    } finally {
                        ja.close();
                    }
                    continue;
                }
                if (!selector.isSelected(ae))
                    continue;
                InputStream is = null;
                try {
                    try {
                        is = new FileInputStream(f);
                        task.on(ae, is);
                    } catch(Exception e) {
                        logger.log(Level.SEVERE, "Exception while processing " + f.getName() +
                                " of size " + f.length(), e);
                    }

                } finally {
                    if (is!=null) {
                        is.close();
                    }
                }
            } else {
                parse(f, selector, task, logger);
            }

        }
    }

    private String mangle(File f) {
        String relativePath = f.getAbsolutePath().substring(directory.getAbsolutePath().length()+1);
        return relativePath.replace(File.separatorChar, '/');
    }

    @Override
    public void close() throws IOException {
    }
}
