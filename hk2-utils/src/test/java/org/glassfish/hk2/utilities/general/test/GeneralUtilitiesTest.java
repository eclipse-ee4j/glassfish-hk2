/*
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.utilities.general.test;

import java.util.Random;

import org.glassfish.hk2.utilities.general.GeneralUtilities;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class GeneralUtilitiesTest {
    private final static String RANDOM_RESULT =
            "Total buffer length: 128\n" +
            "00000000 88 18 B9 9F E8 54 2F 53  36 5A 99 D8 9E 24 14 EA \n" +
            "00000010 22 E8 90 24 67 DB 7E 4B  5E 71 05 60 65 40 F5 A3 \n" +
            "00000020 E5 32 CE FF 7D 58 77 2E  A5 21 80 72 17 7F 7B B4 \n" +
            "00000030 8D B7 1B B6 9C 32 28 BB  5C 7C 04 3D D2 D1 41 A1 \n" +
            "00000040 3F 44 6C 27 84 EF AE 06  82 01 2E F6 1C 24 FF F3 \n" +
            "00000050 DE BB 0A 54 0C F1 42 8A  32 17 80 61 4A 70 36 0C \n" +
            "00000060 9E 9C 14 5B 22 BA B9 FA  5C 04 69 80 26 36 A9 60 \n" +
            "00000070 D6 DB FD C5 10 00 9C 66  B7 62 6D 31 CC 37 28 F2 ";
    
    private final Random random = new Random(1967L);
    
    @Test
    public void testPrintOutBytes() {
        byte buffer[] = new byte[128];
        
        random.nextBytes(buffer);
        
        String asString = GeneralUtilities.prettyPrintBytes(buffer);
        
        Assert.assertEquals(RANDOM_RESULT, asString);
    }

}
