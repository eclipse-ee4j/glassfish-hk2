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

package org.glassfish.hk2.tests.locator.destroy;

import javax.inject.Inject;

import org.glassfish.hk2.api.PostConstruct;

/**
 * This uses PostConstruct and method convention
 * 
 * @author jwells
 */
public class Bar implements PostConstruct {
    @Inject
    private Registrar registrar;
    
    @Inject
    private Baz baz;
    
    public void postConstruct() {
        if (baz == null) throw new AssertionError("baz is null in " + this);
        registrar.addBirth(this);
    }
    
    public void preDestroy() {
        if (baz == null) throw new AssertionError("baz is null in " + this);
        registrar.addDeath(this);
    }
    
    public String toString() {
        return "Bar(" + System.identityHashCode(this) + ")";
    }

}
