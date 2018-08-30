/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
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

import org.glassfish.hk2.utilities.general.GeneralUtilities;

class ReferenceKey {
    private final String type;
    private final String xmlID;
    
    ReferenceKey(String type, String xmlID) {
        this.type = type;
        this.xmlID = xmlID;
    }
    
    @Override
    public int hashCode() {
        return type.hashCode() ^ xmlID.hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof ReferenceKey)) return false;
        ReferenceKey other = (ReferenceKey) o;
        
        return GeneralUtilities.safeEquals(type, other.type) && GeneralUtilities.safeEquals(xmlID, other.xmlID);
    }
    
    @Override
    public String toString() {
        return "ReferenceKey(" + type + "," + xmlID + "," + System.identityHashCode(this) + ")";
    }
}
