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

package org.jvnet.hk2.generator.internal;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.TreeSet;

import org.glassfish.hk2.utilities.DescriptorImpl;
import org.jvnet.hk2.generator.InFlightGenerator;

/**
 * @author jwells
 *
 */
public class InFlightGeneratorImpl implements InFlightGenerator {

    /* (non-Javadoc)
     * @see org.jvnet.hk2.generator.InFlightGenerator#generateFromMultipleDirectories(java.util.List, java.util.List, boolean, java.io.OutputStream)
     */
    @Override
    public void generateFromMultipleDirectories(List<File> directories,
            List<File> searchPath, boolean verbose, OutputStream inhabitantFile)
            throws IOException {
        Utilities utilities = new Utilities(verbose, searchPath);
        
        TreeSet<DescriptorImpl> retVal = new TreeSet<DescriptorImpl>(new DescriptorComparitor());
        for (File directory : directories) {
            if (!directory.exists()) continue;
            
            if (!directory.isDirectory()) {
                throw new IllegalArgumentException(directory.getAbsolutePath() + " is not a directory");
            }
            
            List<DescriptorImpl> newOnes = utilities.findAllServicesFromDirectory(directory, directories);
            if (newOnes != null) {
                retVal.addAll(newOnes);
            }
        }
        
        utilities.close();
        
        if (retVal.isEmpty()) return;
        
        PrintWriter writer = new PrintWriter(inhabitantFile);
        for (DescriptorImpl di : retVal) {
            di.writeObject(writer);
        }
        
        writer.flush();
    }

}
