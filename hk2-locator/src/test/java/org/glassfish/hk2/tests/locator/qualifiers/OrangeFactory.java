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

package org.glassfish.hk2.tests.locator.qualifiers;

import javax.inject.Inject;

import org.glassfish.hk2.api.Factory;

/**
 * @author jwells
 *
 */
public class OrangeFactory implements Factory<Color> {
    @Inject @Red
    private Color red;
    
    @Inject @Yellow
    private Color yellow;

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Factory#provide()
     */
    @Override @Orange
    public Color provide() {
        if (!red.getColorName().equals(QualifierTest.RED)) throw new AssertionError("Red is not red: " + red);
        if (!yellow.getColorName().equals(QualifierTest.YELLOW)) throw new AssertionError("Yellow is not yellow: " + yellow);
        
        return new DerivedColor(QualifierTest.ORANGE);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Factory#dispose(java.lang.Object)
     */
    @Override
    public void dispose(Color instance) {
        // TODO Auto-generated method stub
        
    }

}
