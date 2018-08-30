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

import java.io.Serializable;

/**
 * This represents either a ParentedModel or a
 * ChildDataModel but not both
 * 
 * @author jwells
 *
 */
public class ChildDescriptor implements Serializable {
    private static final long serialVersionUID = 4427931173669631514L;
    
    private ParentedModel parented;
    private ChildDataModel childData;
    
    public ChildDescriptor() {
    }
    
    public ChildDescriptor(ParentedModel parented) {
        this(parented, null);
    }
    
    public ChildDescriptor(ChildDataModel childData) {
        this(null, childData);
    }
    
    private ChildDescriptor(ParentedModel parented, ChildDataModel childData) {
        this.parented = parented;
        this.childData = childData;
    }
    
    public ParentedModel getParentedModel() { return parented; }
    public ChildDataModel getChildDataModel() { return childData; }
    
    @Override
    public String toString() {
        return "ChildDescriptor(" + parented + "," + childData + "," + System.identityHashCode(this) + ")";
    }
}
