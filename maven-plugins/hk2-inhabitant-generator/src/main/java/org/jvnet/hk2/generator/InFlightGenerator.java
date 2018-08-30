/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.jvnet.hk2.generator;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Allows an installer or other jar combiner to be able to scan
 * multiple directories for HK2 services
 * 
 * @author jwells
 *
 */
public interface InFlightGenerator {
    /**
     * Scans multiple directories for HK2 services to be written
     * to the given output stream
     * 
     * @param directories A set of directories that should contain class
     * files to be scanned for HK2 services to be added to the output.
     * All the files in this list must be directories
     * @param searchPath A set of directories or jar files that may contain
     * other required classes but which classes would not be added to the
     * output stream
     * @param verbose if true print information about progress
     * @param inhabitantFile The stream to which to write any HK2
     * service descriptors found in under directories
     * @throws IOException if there is an error writing the inhabitantFile
     * or reading the class files
     */
    public void generateFromMultipleDirectories(List<File> directories,
            List<File> searchPath,
            boolean verbose,
            OutputStream inhabitantFile) throws IOException;

}
