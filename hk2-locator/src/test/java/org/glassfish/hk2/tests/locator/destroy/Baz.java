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

/**
 * This one uses java method name convention to
 * find the post construct and pre destroy
 * 
 * @author jwells
 *
 */
public class Baz {
    @Inject
    private Registrar registrar;
    
    @Inject
    private Qux qux;
    
    public void postConstruct() {
        if (qux == null) throw new AssertionError("qux is null in " + this);
        registrar.addBirth(this);
    }
    
    public void preDestroy() {
        if (qux == null) throw new AssertionError("qux is null in " + this);
        registrar.addDeath(this);
    }
    
    public String toString() {
        return "Baz(" + System.identityHashCode(this) + ")";
    }

}
