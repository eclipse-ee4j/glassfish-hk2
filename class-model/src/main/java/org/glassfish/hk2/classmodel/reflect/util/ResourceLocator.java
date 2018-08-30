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

package org.glassfish.hk2.classmodel.reflect.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Interface to load .class files as resources for processing by the parser.
 * 
 * @author Jerome Dochez
 */
public interface ResourceLocator {

    /**
     * Opens and input stream for the resources identified by the parameter name.
     * @param name the resource identification
     * @return an input stream, or null if the name does not exist
     * @throws IOException on i/o error
     */
    InputStream openResourceStream(String name) throws IOException;

    /**
     * Retrieves the URL given a resource name
     * @param name
     * @return the resource URL, or null if not found
     */
    URL getResource(String name);

}
