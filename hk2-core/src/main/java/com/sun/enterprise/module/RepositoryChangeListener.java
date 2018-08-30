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

package com.sun.enterprise.module;

import java.net.URI;

/**
 * Listener interface to listen to repository changes. Implementations of this listener
 * interface will be notified when repositories they registered to are changing.
 *
 * @author Jerome Dochez
 */
public interface RepositoryChangeListener {

    /**
     * A new libary jar file was added to the repository.
     *
     * @param location the new jar file location
     */
    public void added(URI location);

    /**
     * A library jar file was removed from the repository
     *
     * @param location of the removed file
     */
    public void removed(URI location);

    /**
     * A new module jar file was added to the repository.
     *
     * @param definition the new module definition
     */
    public void moduleAdded(ModuleDefinition definition);

    /**
     * A module file was removed from the repository
     *
     * @param definition the module definition of the removed module
     */
    public void moduleRemoved(ModuleDefinition definition);
}
