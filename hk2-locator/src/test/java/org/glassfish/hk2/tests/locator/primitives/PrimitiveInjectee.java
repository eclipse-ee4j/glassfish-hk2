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

package org.glassfish.hk2.tests.locator.primitives;

import javax.inject.Inject;

import org.glassfish.hk2.api.PerLookup;

/**
 * @author jwells
 *
 */
@PerLookup
public class PrimitiveInjectee {
    @Inject @Thirteen
    private char thirteenChar;
    
    @Inject @Thirteen
    private byte thirteenByte;
    
    @Inject @Thirteen
    private short thirteenShort;
    
    @Inject @Thirteen
    private int thirteenInt;
    
    @Inject @Thirteen
    private long thirteenLong;
    
    @Inject @Thirteen
    private float thirteenFloat;
    
    @Inject @Thirteen
    private double thirteenDouble;
    
    /* package */ char getThirteenChar() {
        return thirteenChar;
    }

    /**
     * @return the thirteenByte
     */
    byte getThirteenByte() {
        return thirteenByte;
    }

    /**
     * @return the thirteenShort
     */
    short getThirteenShort() {
        return thirteenShort;
    }

    /**
     * @return the thirteenInt
     */
    int getThirteenInt() {
        return thirteenInt;
    }

    /**
     * @return the thirteenLong
     */
    long getThirteenLong() {
        return thirteenLong;
    }

    /**
     * @return the thirteenFloat
     */
    float getThirteenFloat() {
        return thirteenFloat;
    }

    /**
     * @return the thirteenDouble
     */
    double getThirteenDouble() {
        return thirteenDouble;
    }
}
