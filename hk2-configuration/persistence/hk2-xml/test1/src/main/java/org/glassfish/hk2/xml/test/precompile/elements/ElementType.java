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

package org.glassfish.hk2.xml.test.precompile.elements;

/**
 * @author jwells
 *
 */
public enum ElementType {
    EARTH("EARTH"),
    FIRE("FIRE"),
    WATER("WATER"),
    WIND("WIND"),
    NONE("NONE"),
    SPECIAL("SPECIAL");
    
    private final String value;
    
    ElementType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }
    
    public static ElementType fromValue(String v) {
        for (ElementType c: ElementType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
