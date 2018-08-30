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

package org.glassfish.hk2.xml.test.defaulting;

/**
 * @author jwells
 *
 */
public enum Colors {
    GREEN
    , RED
    , BLUE
    , BLACK
    , WHITE;
    
    public static Colors fromValue(String value) {
        if ("GREEN".equals(value)) return Colors.GREEN;
        if ("RED".equals(value)) return Colors.RED;
        if ("BLUE".equals(value)) return Colors.BLUE;
        if ("BLACK".equals(value)) return Colors.BLACK;
        if ("WHITE".equals(value)) return Colors.WHITE;
        throw new IllegalArgumentException("No color with value " + value);
    }
}
