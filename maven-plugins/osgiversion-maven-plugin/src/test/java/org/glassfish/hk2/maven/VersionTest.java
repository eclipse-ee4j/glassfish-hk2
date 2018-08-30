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

package org.glassfish.hk2.maven;

import junit.framework.Assert;
import org.junit.Test;

/**
 *
 * @author Romain Grecourt
 */
public class VersionTest {
    
    public static void doTest(
            String orig,
            Version.COMPONENT compToDrop,
            String expected) {

        String actual = new Version(orig).convertToOsgi(compToDrop);
        Assert.assertEquals(
                String.format("orig=%s ; compToDrop=%s ; expected=%s ; actual=%s", orig, String.valueOf(compToDrop), expected, actual),
                expected,
                actual);
    }
    
    @Test
    public void simpleTests(){
        doTest("4.0.1-SNAPSHOT",Version.COMPONENT.qualifier,"4.0.1");
        doTest("12.1.3.339",Version.COMPONENT.qualifier,"12.1.3");
        doTest("12.1.3.339",null,"12.1.3");
        doTest("12.1.3.0.0-130717.3355",null,"12.1.3.130717_3355");
        doTest("12.1.3.0.0-130717.3355",Version.COMPONENT.qualifier,"12.1.3");
    }
}
