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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Archive adapter based on a single InputStream instance.
 * 
 * @author Jerome Dochez
 */
public class InputStreamArchiveAdapter extends AbstractAdapter {
    
    final private InputStream is;
    final private URI uri;
//    final private JarArchive parentArchive;

    public InputStreamArchiveAdapter(JarArchive parent, URI uri, InputStream is) {
        this.uri = uri;
        this.is = is;
//        this.parentArchive = parent;
    }

    @Override
    public URI getURI() {
        return uri;
    }

    @Override
    public Manifest getManifest() throws IOException {
        throw new IOException("Not Implemented");
    }

    @Override
    public void onSelectedEntries(Selector selector, EntryTask task, Logger logger) throws IOException {
        byte[] bytes = new byte[52000];
        JarEntry ja;
        JarInputStream jis = new JarInputStream(new BufferedInputStream(is));
        
        try {
            while ((ja=jis.getNextJarEntry())!=null) {
                try {
                    Entry je = new Entry(ja.getName(), ja.getSize(), ja.isDirectory());
                    if (!selector.isSelected(je))
                        continue;
    
                    try {
                        if (ja.getSize()>bytes.length) {
                            bytes = new byte[(int) ja.getSize()];
                        }
                        if (ja.getSize()!=0) {
                            // beware, ja.getSize() can be equal to -1 if the size cannot be determined.
    
                            int read = 0;
                            int allRead=0;
                            do {
                                read = jis.read(bytes, allRead, bytes.length-allRead);
                                allRead+=read;
                                if (allRead==bytes.length) {
                                    bytes = Arrays.copyOf(bytes, bytes.length*2);
                                }
    
                            } while (read!=-1);
    
                            if (ja.getSize()!=-1 && ja.getSize()!=(allRead+1)) {
                                logger.severe("Incorrect file length while processing " + ja.getName() + " of size " + ja.getSize() + " got " + allRead);
                            }
    
                            // if the size was not known, let's reset it now.
                            if (je.size==-1) {
                                je = new Entry(ja.getName(), allRead+1);
                            }
                        }
                        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                        try {
                            task.on(je, bais);
                        } finally {
                            bais.close();
                        }
    
                    } catch (Exception e) {
                        logger.log(Level.SEVERE, "Exception while processing " + ja.getName()
                                + " of size " + ja.getSize(), e);
                    }
                } finally {
                    // this is here to catch the spurious "java.io.EOFException:Unexpected end of ZLIB input stream"
                    try {
                      jis.closeEntry();
                    } catch (Exception e) {
                      logger.log(Level.FINE, "swallowing error", e);
                    }
                }
            }
        } finally {
          try {
            jis.close();
          } catch (Exception e) {
            logger.log(Level.FINE, "swallowing error", e);
          }
        }
    }

    @Override
    public void close() throws IOException {
        is.close();
    }
}
