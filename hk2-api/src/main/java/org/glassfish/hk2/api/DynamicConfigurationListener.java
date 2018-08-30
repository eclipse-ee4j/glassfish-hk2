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

import org.jvnet.hk2.annotations.Contract;

/**
 * A service that listens for dynamic configuration changes.
 * Implementations of this service must be in the Singleton
 * scope.
 * @author jwells
 *
 */
@Contract
public interface DynamicConfigurationListener {
    /**
     * This method is called when the set of descriptors
     * in this service locator has been changed.  Changes to
     * parent service locators descriptors will not be reported.
     * These services are called back on the thread doing the
     * {@link DynamicConfiguration#commit()} so care should be taken
     * to do any work quickly.  Any exception thrown from this method
     * will be ignored.  A commit that failed will not be reported to
     * this method
     */
    public void configurationChanged();

}
