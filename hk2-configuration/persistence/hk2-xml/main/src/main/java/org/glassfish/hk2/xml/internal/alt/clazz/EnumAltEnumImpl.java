/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.xml.internal.alt.clazz;

import org.glassfish.hk2.xml.internal.alt.AltEnum;

/**
 * @author jwells
 *
 */
public class EnumAltEnumImpl implements AltEnum {
    private final Enum<?> eValue;
    
    public EnumAltEnumImpl(Enum<?> eValue) {
        this.eValue = eValue;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.internal.alt.AltEnum#getDeclaringClass()
     */
    @Override
    public String getDeclaringClass() {
        return eValue.getDeclaringClass().getName();
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.internal.alt.AltEnum#getName()
     */
    @Override
    public String getName() {
        return eValue.name();
    }
    
    @Override
    public String toString() {
        return "EnumAltEnumImpl(" + eValue + "," + System.identityHashCode(this) + ")";
    }

}
