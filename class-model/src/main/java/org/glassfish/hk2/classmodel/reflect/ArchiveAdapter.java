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

package org.glassfish.hk2.classmodel.reflect;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.jar.Manifest;
import java.util.logging.Logger;

/**
 * adapter for reading archive style structure
 *
 * @author Jerome Dochez
 */
public interface ArchiveAdapter extends Closeable {

    /**
     * Returns the URI of the archive
     *
     * @return URI of the archive
     */
    public URI getURI();

    /**
     * Returns the manifest instance for the archive.
     *
     * @return the archive's manifest
     * @throws IOException if the manifest cannot be loaded.
     */
    public Manifest getManifest() throws IOException ;

    /**
     * defines the notion of an archive entry task which is a task
     * aimed to be run on particular archive entry.
     */
    public interface EntryTask {
        
        /**
         * callback to do some processing on an archive entry.
         *
         * @param e the archive entry information such as its name, size...
         * @param is the archive entry content.
         * @throws IOException if the input stream reading generates a failure
         */
        public void on(final Entry e, InputStream is) throws IOException;
    }

    public interface Selector {

        /**
         * callback to select an archive for processing
         * @param entry the archive entry information
         * @return true if the archive entry has been selected for processing
         */
        public boolean isSelected(final Entry entry);
    }
    
    /**
     * perform a task on each archive entry
     *
     * @param task the task to perform
     * @param logger for any logging activity
     * @throws IOException can be generated while reading the archive entries
     */
    public void onAllEntries(EntryTask task, Logger logger) throws IOException;

    /**
     * perform a task on selected archive entries
     *
     * @param selector implementation to select the archive archive entries on
     * which the task should be performed.
     * @param task the task to perform
     * @param logger for any logging activity
     * @throws IOException can be generated while reading the archive entries
     */
    public void onSelectedEntries(Selector selector, EntryTask task, Logger logger) throws IOException;


    /**
     * Definition of an archive entry
     */
    public final class Entry {
        final public String name;
        final public long size;

        /**
         * creates a new archive entry
         * @param name the entry name
         * @param size the entry size
         * @param isDirectory true if this entry is a directory
         * @deprecated Use the other constructor, isDirectory is not used
         */
        public Entry(String name, long size, boolean isDirectory) {
            this.name = name;
            this.size = size;
        }
        
        /**
         * creates a new archive entry
         * @param name the entry name
         * @param size the entry size
         */
        public Entry(String name, long size) {
            this(name, size, false);
        }
    }
}
