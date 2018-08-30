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

package org.glassfish.hk2.classmodel.reflect.util;

import org.glassfish.hk2.classmodel.reflect.Parser;

import java.net.URI;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Jar based archive abstraction
 */
public class JarArchive extends AbstractAdapter {

    private final Parser parser;
    private final JarFile jar;
    private final URI uri;

    /**
     * We need to maintain how many internal jars got opened so that
     * we don't close our jar archive until all the sub scanning
     * has been done successfully.
     */
    private final AtomicInteger releaseCount = new AtomicInteger(1);

    public JarArchive(Parser parser, URI uri) throws IOException
    {
        File f = new File(uri);
        this.uri = uri;
        this.jar = new JarFile(f);
        this.parser = parser;
    }

    @Override
    public URI getURI() {
        return uri;
    }

    @Override
     public void onSelectedEntries(Selector selector, EntryTask task, final Logger logger) throws IOException {
         Enumeration<JarEntry> enumEntries = jar.entries();
         while(enumEntries.hasMoreElements()) {
             JarEntry ja = enumEntries.nextElement();
             if (ja.getName().endsWith(".jar")) {
                 URI subURI = null;
                 try {
                     subURI = new URI("jar:"+uri+"!/"+ja.getName());
                 } catch (URISyntaxException e) {
                     try {
                         subURI = new URI(ja.getName());
                     } catch (URISyntaxException e1) {
                         logger.log(Level.FINE, "ignoring exception", e1);
                     }
                 }

                 final InputStreamArchiveAdapter subArchive = new InputStreamArchiveAdapter(this, subURI,
                         jar.getInputStream(jar.getEntry(ja.getName())));
                 releaseCount.incrementAndGet();
                 parser.parse(subArchive, new Runnable() {
                     @Override
                     public void run() {
                         try {
                             subArchive.close();
                         } catch (IOException e) {
                             logger.log(Level.SEVERE, "Cannot close sub archive {0}", subArchive.getURI());
                         }
                     }
                 });
                     subArchive.onSelectedEntries(selector, task, logger);
             }
             InputStream is = null;
             try {
                 Entry entry = new Entry(ja.getName(), ja.getSize());
                 if (!selector.isSelected(entry))
                    continue;
                 is = jar.getInputStream(ja);
                 try {
                     task.on(entry, is);
                 } catch (Exception e) {
                     logger.log(Level.SEVERE, "Exception while processing " + ja.getName()
                             + " inside " + jar.getName() + " of size " + ja.getSize(), e);
                 }
             } finally {
                 if (is!=null) {
                     is.close();
                 }
             }
         }

     }
    

    @Override
    public Manifest getManifest() throws IOException {
        return jar.getManifest();
    }

    @Override
    public void close() throws IOException {
        releaseCount();
    }

    void releaseCount() throws IOException {
        int release = releaseCount.decrementAndGet();
        if (release==0) {
            jar.close();
        }
    }
}
