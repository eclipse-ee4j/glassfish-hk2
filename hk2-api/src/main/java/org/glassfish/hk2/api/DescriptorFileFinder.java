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

package org.glassfish.hk2.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.jvnet.hk2.annotations.Contract;

/**
 * Implementations of this interface allow the customization of
 * how hk2 inhabitant files are found.  Classes that implement
 * this interface should also implement {@link DescriptorFileFinderInformation}
 * for better information when a failure occurs
 * 
 * @author jwells
 *
 */
@Contract
public interface DescriptorFileFinder {
    /** The name of the default location for hk2 inhabitant files */
    public static final String RESOURCE_BASE="META-INF/hk2-locator/";
    
    /**
     * Returns a list of input streams for hk2 locator files
     * 
     * @return A non-null (but possibly empty) list of InputStreams
     * for hk2 inhabitant files
     * @throws IOException If there was an error finding the hk2 inhabitant files
     */
    List<InputStream> findDescriptorFiles() throws IOException;

}
