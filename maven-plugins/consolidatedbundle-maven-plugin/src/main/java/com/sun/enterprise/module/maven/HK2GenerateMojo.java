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

package com.sun.enterprise.module.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.artifact.Artifact;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Generates a consolidated OSGI bundle with a consolidated HK2 header
 *
 * @goal hk2-generate
 * @phase prepare-package
 *
 * @requiresProject true
 * @requiresDependencyResolution compile
 * @author Sivakumar Thyagarajan
 */
/* We use prepare-package as the phase as we need to perform this consolidation before the maven-bundle-plugin's bundle goal gets executed in the package phase.*/
public class HK2GenerateMojo extends AbstractMojo {

    private final static String META_INF = "META-INF";
    private final static String HK2_LOCATOR = "hk2-locator";
    private final static String DEFAULT = "default";
    private final static String JAR_ENTRY = "META-INF/hk2-locator/default";
    private final static int BUFFER_SIZE = 4096;
    /**
     * Directory where the manifest will be written
     *
     * @parameter expression="${manifestLocation}"
     * default-value="${project.build.outputDirectory}"
     */
    protected File manifestLocation;
    /**
     * The maven project.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    @SuppressWarnings("unchecked")
    public void execute() throws MojoExecutionException {
        Set<Artifact> dependencyArtifacts = project.getDependencyArtifacts();
        if (dependencyArtifacts == null) {
            return;
        }

        try {
            OutputStream catStream = getCatOutputStream();

            // Create the consolidated inhabitant file contents by
            // catting all the dependency artifacts together
            for (Artifact a : (Set<Artifact>) project.getDependencyArtifacts()) {
                if (a.getScope() != null && a.getScope().equals("test")) {
                    continue;
                }
                getLog().info("Dependency Artifact: " + a.getFile().toString());

                JarFile jf = new JarFile(a.getFile());
                JarEntry je = jf.getJarEntry(JAR_ENTRY);
                if (je == null) {
                    continue;
                }

                getLog().debug("Dependency Artifact " + a + " has Inhabitants File: " + je);

                catJarEntry(jf, je, catStream);
            }
        } catch (IOException ioe) {
            throw new MojoExecutionException(ioe.getMessage(), ioe);
        }
    }

    private void catJarEntry(JarFile jf, JarEntry e, OutputStream catStream)
            throws IOException {
        byte buf[] = new byte[BUFFER_SIZE];

        InputStream is = jf.getInputStream(e);
        int readLength;
        while ((readLength = is.read(buf)) > 0) {
            catStream.write(buf, 0, readLength);
        }
    }

    private OutputStream getCatOutputStream() throws MojoExecutionException, IOException {
        String inhabitantsDir = "" + manifestLocation + File.separatorChar
                + META_INF + File.separatorChar + HK2_LOCATOR;

        File inhabitantsDirFile = new File(inhabitantsDir);

        if (inhabitantsDirFile.exists()) {
            if (!inhabitantsDirFile.isDirectory()) {
                throw new MojoExecutionException("File "
                        + inhabitantsDirFile.getAbsolutePath() + " is not a directory");
            }
        } else {
            boolean success = inhabitantsDirFile.mkdirs();
            if (!success) {
                throw new MojoExecutionException("Unable to created directory "
                        + inhabitantsDirFile.getAbsolutePath());
            }
        }

        File defaultFile = new File(inhabitantsDirFile, DEFAULT);
        FileOutputStream fos = new FileOutputStream(defaultFile,true);
        return fos;
    }
}
