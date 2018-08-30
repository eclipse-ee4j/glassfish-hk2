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

package org.glassfish.hk2.tests.locator.named;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author jwells
 *
 */
public class Verona {
    private final CitizenOfVerona juliet;
    
    @Inject @Named(NamedTest.ROMEO)
    private CitizenOfVerona romeo;
    
    @Inject @Named
    private CitizenOfVerona Benvolio;
    
    private CitizenOfVerona mercutio;
    
    @Inject
    protected Verona(@Named(NamedTest.JULIET) CitizenOfVerona juliet) {
        this.juliet = juliet;
    }
    
    @Inject
    /* package */ void setMercutio(@Named(NamedTest.MERCUTIO) CitizenOfVerona mercutio) {
        this.mercutio = mercutio;
    }

    /**
     * @return the juliet
     */
    public CitizenOfVerona getJuliet() {
        return juliet;
    }

    /**
     * @return the romeo
     */
    public CitizenOfVerona getRomeo() {
        return romeo;
    }

    /**
     * @return the mercutio
     */
    public CitizenOfVerona getMercutio() {
        return mercutio;
    }
    
    public CitizenOfVerona getBenvolio() {
        return Benvolio;
    }

}
