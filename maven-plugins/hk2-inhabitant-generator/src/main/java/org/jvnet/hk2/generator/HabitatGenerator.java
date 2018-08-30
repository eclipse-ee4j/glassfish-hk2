/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
import java.security.AccessController;
import java.security.PrivilegedAction;

import org.jvnet.hk2.generator.internal.GeneratorRunner;

/**
 * This is a command line (or embedded) utility
 * that will generate habitat files based on
 * &#64;Service annotations.
 * 
 * @author jwells
 *
 */
public class HabitatGenerator {
    private final static String CLASS_PATH_PROP = "java.class.path";
    private final static String CLASSPATH = AccessController.doPrivileged(new PrivilegedAction<String>() {
        @Override
        public String run() {
            return System.getProperty(CLASS_PATH_PROP);
        }
            
    });
    
    /** The flag for the location of the file */
    public final static String FILE_ARG = "--file";
    /** The flag for the name of the locator */
    public final static String LOCATOR_ARG = "--locator";
    /** The flag for verbosity */
    public final static String VERBOSE_ARG = "--verbose";
    /** The name of the JAR file to write to (defaults to input file, ignored if input file is directory) */
    public final static String OUTJAR_ARG = "--outjar";
    /** The path-separator delimited list of files to search for contracts and qualifiers (defaults to classpath) */
    public final static String SEARCHPATH_ARG = "--searchPath";
    /** This option will write files in-place, which is quicker but will remove existing files prior to writing new ones */
    public final static String NOSWAP_ARG = "--noswap";
    /** This option gives the name of directory in the target location where the file should be placed */
    public final static String DIRECTORY_ARG = "--directory";
    /** This option gives the name of directory in the target location where the file should be placed */
    public final static String NO_DATE_ARG = "--noDate";
    
    private final String directoryOrFileToGenerateFor;
    private final String outjarName;
    private final String locatorName;
    private final boolean verbose;
    private final String searchPath;
    private final boolean noSwap;
    private final String outputDirectory;
    private final boolean includeDate;
    
    private HabitatGenerator(String directoryOrFileToGenerateFor,
            String outjarName,
            String locatorName,
            boolean verbose,
            String searchPath,
            boolean noSwap,
            String outputDirectory,
            boolean includeDate) {
        this.directoryOrFileToGenerateFor = directoryOrFileToGenerateFor;
        this.outjarName = outjarName;
        this.locatorName = locatorName;
        this.verbose = verbose;
        this.searchPath = searchPath;
        this.noSwap = noSwap;
        this.outputDirectory = outputDirectory;
        this.includeDate = includeDate;
    }
    
    private void printThrowable(Throwable th) {
        int lcv = 0;
        while (th != null) {
            System.out.println("Exception level " + lcv++ + " message is \"" +
                th.getMessage() + "\"");
            
            th.printStackTrace();
            
            th = th.getCause();
        }
    }
    
    private int go() {
        GeneratorRunner runner = new GeneratorRunner(directoryOrFileToGenerateFor,
                outjarName, locatorName, verbose, searchPath, noSwap, outputDirectory,
                includeDate);
        
        try {
            runner.go();
            if (verbose) {
                System.out.println("HabitatGenerator completed successfully");
            }
        }
        catch (AssertionError ae) {
            if (verbose) {
                printThrowable(ae);
            }
            else {
                System.out.println(ae.getMessage());
            }
            return 1;
        }
        catch (IOException io) {
            if (verbose) {
                printThrowable(io);
            }
            else {
                System.out.println(io.getMessage());
            }
            
            return 2;
        }
        
        return 0;
    }
    
    private static void usage() {
        System.out.println("java org.jvnet.hk2.generator.HabitatGenerator\n" +
          "\t[--file jarFileOrDirectory]\n" +
          "\t[--searchPath path-separator-delimited-classpath]\n" +
          "\t[--outjar jarFile]\n" +
          "\t[--locator locatorName]\n" +
          "\t[--verbose]");
    }
    
    
    private final static String LOCATOR_DEFAULT = "default";
    private final static String META_INF = "META-INF";
    public final static String HK2_LOCATOR = "hk2-locator";
    
    /**
     * A utility to generate inhabitants files.  By default the first element of the classpath will be analyzed and
     * an inhabitants file will be put into the JAR or directory.  The arguments are as follows:
     * <p>
     * HabitatGenerator [--file jarFileOrDirectory] [--searchPath path-separator-delimited-classpath] [--outjar jarfile] [--locator locatorName] [--directory targetDirectory] [--verbose]
     * </p>
     * If the input file is a directory then the output file will go into META-INF/locatorName in the
     * original directory
     * <p>
     * If the input file is a jar file then the output file will go into the JAR file under
     * META-INF/locatorName, overwriting any file that was previously in that location
     * <p>
     * --outjar only works if the file being added to is a JAR file, in which case this is the
     * name of the output jar file that should be written.  This defaults to the input jar file
     * itself if not specified.  If specified and the jarFileOrDirectory parameter is a directory
     * then this parameter is ignored
     * 
     * @param argv The set of command line arguments
     * @return 0 on success, non-zero on failure
     */
    public static int embeddedMain(String argv[]) {
        String defaultFileToHandle = null;
        String defaultLocatorName = LOCATOR_DEFAULT;
        boolean defaultVerbose = false;
        String outjarFile = null;
        String searchPath = CLASSPATH;
        boolean userNoSwap = false;
        String outputDirectory = null;
        boolean defaultIncludeDate = true;
        
        for (int lcv = 0; lcv < argv.length; lcv++) {
            if (VERBOSE_ARG.equals(argv[lcv])) {
                defaultVerbose = true;
            }
            else if (FILE_ARG.equals(argv[lcv])) {
                lcv++;
                if (lcv >= argv.length) {
                    usage();
                    return 3;
                }
                
                defaultFileToHandle = argv[lcv];
            }
            else if (LOCATOR_ARG.equals(argv[lcv])) {
                lcv++;
                if (lcv >= argv.length) {
                    usage();
                    return 4;
                }
                
                defaultLocatorName = argv[lcv];
            }
            else if (OUTJAR_ARG.equals(argv[lcv])) {
                lcv++;
                if (lcv >= argv.length) {
                    usage();
                    return 5;
                }
                
                outjarFile = argv[lcv];
            }
            else if (SEARCHPATH_ARG.equals(argv[lcv])) {
                lcv++;
                if (lcv >= argv.length) {
                    usage();
                    return 5;
                }
                
                searchPath = argv[lcv];
            }
            else if (NOSWAP_ARG.equals(argv[lcv])) {
                userNoSwap = true;
            }
            else if (NO_DATE_ARG.equals(argv[lcv])) {
                defaultIncludeDate = false;
            }
            else if (DIRECTORY_ARG.equals(argv[lcv])) {
                lcv++;
                if (lcv >= argv.length) {
                    usage();
                    return 5;
                }
                
                outputDirectory = argv[lcv];
            }
            else {
                System.err.println("Uknown argument: " + argv[lcv]);
            }
        }
        
        if (defaultFileToHandle == null) {
            String cp = CLASSPATH;
            
            int pathSep = cp.indexOf(File.pathSeparator);
            
            String firstInLine;
            if (pathSep < 0) {
                firstInLine = cp;
            }
            else {
                firstInLine = cp.substring(0, pathSep);
            }
            
            defaultFileToHandle = firstInLine;
        }
        
        if (outjarFile == null) outjarFile = defaultFileToHandle;
        
        if (outputDirectory == null) {
            File defaultFileAsFile = new File(defaultFileToHandle);
            if (!defaultFileAsFile.exists() || defaultFileAsFile.isDirectory()) {
                File defaultDirectoryAsFile = new File(defaultFileAsFile, META_INF);
                defaultDirectoryAsFile = new File(defaultDirectoryAsFile, HK2_LOCATOR);
                
                outputDirectory = defaultDirectoryAsFile.getAbsolutePath();
            }
        }    
        
        HabitatGenerator hg = new HabitatGenerator(defaultFileToHandle, outjarFile,
                defaultLocatorName, defaultVerbose, searchPath, userNoSwap,
                outputDirectory, defaultIncludeDate);
        
        return hg.go();
    }
    
    /**
     * This method will call System.exit() with a 0 on success and non-zero on failure
     * 
     * @param argv The arguments to the command (see embeddedMain)
     */
    public static void main(String argv[]) {
        try {
            System.exit(embeddedMain(argv));
        }
        catch (Throwable th) {
            th.printStackTrace();
            
            System.exit(-1);
        }
    }

}
