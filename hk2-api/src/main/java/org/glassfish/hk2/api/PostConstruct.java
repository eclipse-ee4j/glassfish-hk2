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

package org.glassfish.hk2.api;

/**
 * Classes implementing this interface register an interest in
 * being notified when the instance has been created and the
 * component is about to be place into commission.
 *
 * @author Jerome Dochez
 */
public interface PostConstruct {

    /**
     * The component has been injected with any dependency and
     * will be placed into commission by the subsystem.
     *
     * Hk2 will catch all unchecked exceptions, and cause the
     * backing inhabitant to be released.
     */
    public void postConstruct();
}
