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

package com.sun.enterprise.module.maven;

import org.glassfish.hk2.maven.Version;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * Converts the project version into the OSGi format and
 * set that to "project.osgi.version" property.
 * It can be configured to drop certain portions from the
 * version. See {@link #dropVersionComponent}.
 *
 * @author Kohsuke Kawaguchi
 * @author Sanjeeb.Sahoo@Sun.COM
 * @goal compute-osgi-version
 * @threadSafe
 * @phase validate
 * @requiresProject
 */
public class OsgiVersionMojo extends AbstractMojo {
    /**
     * The maven project.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;
    
    /**
     * Flag used to determine what components of the version will be used
     * in OSGi version.
     * An OSGi version has four parts as shown below:
     * major.minor.micro.qualifer.
     * It is not always desirable to use all four parts while
     * exporting packages. In fact, maven version and OSGi version
     * behave just opposite during version comparison as shown below:
     * a maven version 1.2.3-SNAPSHOT is mapped to OSGi version 1.2.3.SNAPSHOT.
     * In maven, 1.2.3 > 1.2.3-SNAPSHOT, but in OSGi, 1.2.3 < 1.2.3.SNAPSHOT.
     * So, it is highly desirable to drop qualifier while computing the version.
     * Instead of hardcoding the policy, we let user tell us what portions will
     * be used in the OSGi version. If they ask us to drop minor, then only
     * major will be used. Similarly, if they ask us to drop qualifier, then
     * major, minor and micro portions will be used.
     * @parameter
     */
    protected Version.COMPONENT dropVersionComponent;

    /**
     * @parameter default-value="project.osgi.version"
     */
    protected String versionPropertyName;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Version projectVersion = new Version(project.getVersion());
        String v = projectVersion.convertToOsgi(dropVersionComponent);
        getLog().debug("OSGi Version for "+project.getVersion()+" is "+v);
        getLog().debug("It is set in project property called "+ versionPropertyName);
        project.getProperties().put(versionPropertyName,v);
    }
}
