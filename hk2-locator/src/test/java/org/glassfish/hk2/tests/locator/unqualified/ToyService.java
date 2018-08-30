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

package org.glassfish.hk2.tests.locator.unqualified;

import javax.inject.Inject;

import org.glassfish.hk2.api.Unqualified;

/**
 * @author jwells
 *
 */
public class ToyService {
    @Inject @Unqualified({Tractor.class, Elephant.class}) @Shoe
    private Toy shoeToy;
    
    private Toy unknownToy;
    
    private final Toy naturalToy;
    
    @Inject
    private ToyService(Toy naturalToy) {
        // The toy you get with no qualifiers or anything
        this.naturalToy = naturalToy;
        
    }
    
    @Inject 
    private void setUnknownToy(@Unqualified Toy unknownToy) {
        // The toy you get with no qualifiers or anything
        this.unknownToy = unknownToy;
        
    }
    
    public Toy getNaturalToy() { return naturalToy; }
    
    public Toy getShoeToy() { return shoeToy; }
    
    public Toy getUnknownToy() { return unknownToy; }

}
