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

package org.glassfish.hk2.xml.internal;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.glassfish.hk2.xml.internal.alt.AdapterInformation;
import org.glassfish.hk2.xml.internal.alt.AltClass;

/**
 * @author jwells
 *
 */
public class AdapterInformationImpl implements AdapterInformation {
    private final boolean isChild;
    private final AltClass valueType;
    private final AltClass boundType;
    private final AltClass adapter;
    
    public AdapterInformationImpl(AltClass adapter,
            AltClass valueType,
            AltClass boundType) {
        this.adapter = adapter;
        this.valueType = valueType;
        this.boundType = boundType;
        
        isChild = valueType.isInterface();
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.internal.alt.AdapterInformation#isChild()
     */
    @Override
    public boolean isChild() {
        return isChild;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.internal.alt.AdapterInformation#getValueType()
     */
    @Override
    public AltClass getValueType() {
        return valueType;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.internal.alt.AdapterInformation#getBoundType()
     */
    @Override
    public AltClass getBoundType() {
        return boundType;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.internal.alt.AdapterInformation#getAdapter()
     */
    @Override
    public AltClass getAdapter() {
        return adapter;
    }
    
    @Override
    public String toString() {
        return "AdapterInformationImpl(" + adapter + "," + valueType + "," + boundType + "," + isChild + "," + System.identityHashCode(this) + ")";
    }

}
