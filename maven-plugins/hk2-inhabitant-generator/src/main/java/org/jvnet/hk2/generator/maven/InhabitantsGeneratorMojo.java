/*
 * Copyright (c) 2023 Contributors to the Eclipse Foundation. All rights reserved.
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

package org.jvnet.hk2.generator.maven;

import java.io.File;

/**
 * Generates inhabitant
 *
 * @goal generate-inhabitants
 * @phase process-classes
 * @threadSafe true
 * @requiresDependencyResolution test
 */
public class InhabitantsGeneratorMojo extends AbstractInhabitantsGeneratorMojo {

    /**
     * @parameter property="project.build.outputDirectory"
     */
    private File outputDirectory;

    /**
     * @parameter default-value="true"
     */
    private boolean noswap;

    @Override
    protected boolean getNoSwap() {
        return noswap;
    }

    @Override
    protected File getOutputDirectory() {
        return outputDirectory;
    }
}
