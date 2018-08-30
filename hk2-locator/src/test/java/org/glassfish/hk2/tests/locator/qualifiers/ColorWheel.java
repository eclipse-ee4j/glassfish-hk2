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
import javax.inject.Singleton;

/**
 * @author jwells
 *
 */
@Singleton
public class ColorWheel {
    private final Color red;
    private final Color purple;
    private final Color blue;
    private final Color green;
    private final Color yellow;
    private final Color orange;
    
    @Inject
    private ColorWheel(
            @Red Color red,
            @Purple Color purple,
            @Blue Color blue,
            @Green Color green,
            @Yellow Color yellow,
            @Orange Color orange) {
        this.red = red;
        this.purple = purple;
        this.blue = blue;
        this.green = green;
        this.yellow = yellow;
        this.orange = orange;
    }

    /**
     * @return the red
     */
    Color getRed() {
        return red;
    }

    /**
     * @return the purple
     */
    Color getPurple() {
        return purple;
    }

    /**
     * @return the blue
     */
    Color getBlue() {
        return blue;
    }

    /**
     * @return the green
     */
    Color getGreen() {
        return green;
    }

    /**
     * @return the yellow
     */
    Color getYellow() {
        return yellow;
    }

    /**
     * @return the orange
     */
    Color getOrange() {
        return orange;
    }
    
    

}
