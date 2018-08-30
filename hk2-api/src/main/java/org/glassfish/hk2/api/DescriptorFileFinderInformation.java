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

package org.glassfish.hk2.api;

import java.util.List;

/**
 * Returns information about the {@link java.io.InputStream}
 * returned by {@link DescriptorFileFinder#findDescriptorFiles()}
 * that can be used to give better information when one of the
 * streams fails.  Classes that implement {@link DescriptorFileFinder}
 * should also implement this interface in order to provide better
 * failure information
 * 
 * @author jwells
 *
 */
public interface DescriptorFileFinderInformation {
    /**
     * This list must have the same cardinality as
     * {@link DescriptorFileFinder#findDescriptorFiles()}.
     * The Strings returned from this list must give identifying
     * information about the InputStream with the same index.
     * For example, if the InputStream is from a {@link java.net.URL}
     * then the toString of the URL would be appropriate.  If
     * the InputStream is from a {@link java.io.File} then the
     * Absolute path to that file might be appropriate
     * 
     * @return Identifying information about the InputStream
     * returned by {@link DescriptorFileFinder#findDescriptorFiles()}
     * correlated by index
     */
    List<String> getDescriptorFileInformation();

}
