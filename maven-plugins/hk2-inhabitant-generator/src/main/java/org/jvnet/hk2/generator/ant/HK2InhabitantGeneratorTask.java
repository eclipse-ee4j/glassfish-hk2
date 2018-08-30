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

package org.jvnet.hk2.generator.ant;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.jvnet.hk2.generator.HabitatGenerator;

/**
 * @author jwells
 *
 */
public class HK2InhabitantGeneratorTask extends Task {
    private File targetDirectory = new File("target/classes");
    private boolean verbose = false;
    private String locator = null;
    private File outputDirectory = null;
    private boolean noswap = false;
    private Path classpath = null;
    private boolean includeDate = true;
    
    public void setTargetDirectory(File targetDirectory) {
        this.targetDirectory = targetDirectory;
    }
    
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
    
    public void setLocator(String locator) {
        this.locator = locator;
    }
    
    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }
    
    public void setNoSwap(boolean noswap) {
        this.noswap = noswap;
    }
    
    public void setIncludeDate(boolean includeDate) {
        this.includeDate = includeDate;
    }
    
    public void addClasspath(Path classpath) {
        this.classpath = classpath;
    }
    
    public void execute() throws BuildException {
        List<String> args = new LinkedList<String>();
        
        if (targetDirectory != null) {
            if (!targetDirectory.isDirectory()) {
                throw new BuildException("targetDirectory " + targetDirectory.getAbsolutePath() +
                        " must point to the directory where the built classes reside");
            }
            
            args.add(HabitatGenerator.FILE_ARG);
            args.add(targetDirectory.getAbsolutePath());
        }
        
        if (verbose) {
            args.add(HabitatGenerator.VERBOSE_ARG);
        }
        
        if (!includeDate) {
            args.add(HabitatGenerator.NO_DATE_ARG);
        }
        
        if (locator != null) {
            args.add(HabitatGenerator.LOCATOR_ARG);
            args.add(locator);
        }
        
        if (outputDirectory != null) {
            if (!outputDirectory.isDirectory()) {
                if (outputDirectory.exists()) {
                    throw new BuildException("outputDirectory " + outputDirectory.getAbsolutePath() +
                            " exists and is not a directory");
                }
                
                if (!outputDirectory.mkdirs()) {
                    throw new BuildException("Could not create directory " + outputDirectory.getAbsolutePath());
                }
            }
            
            args.add(HabitatGenerator.DIRECTORY_ARG);
            args.add(outputDirectory.getAbsolutePath());
        }
        
        if (noswap) {
            args.add(HabitatGenerator.NOSWAP_ARG);
        }
        
        if (classpath != null) {
            args.add(HabitatGenerator.SEARCHPATH_ARG);
            args.add(classpath.toString());
        }
        
        String argv[] = args.toArray(new String[args.size()]);
        
        int result = HabitatGenerator.embeddedMain(argv);
        if (result != 0) {
            throw new BuildException("Could not generate inhabitants file for " + targetDirectory.getAbsolutePath());
        }
    }

}
