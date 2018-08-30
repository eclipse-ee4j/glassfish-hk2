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

package org.glassfish.hk2.xml.test.dynamic.overlay;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

/**
 * Unkeyed but still has an value for use in testing
 * 
 * @author jwells
 *
 */
public interface UnkeyedLeafBean {
    /**
     * Very explicitly NOT a key
     * @return
     */
    @XmlElement(name=OverlayUtilities.NAME_TAG)
    public String getName();
    public void setName(String name);
    
    @XmlElement(name=OverlayUtilities.LEAF_LIST)
    public List<UnkeyedLeafBean> getListLeaf();
    public void setListLeaf(List<UnkeyedLeafBean> listLeaf);
    public UnkeyedLeafBean addListLeaf();
    public UnkeyedLeafBean removeListLeaf(int index);
    
    @XmlElement(name=OverlayUtilities.LEAF_ARRAY)
    public UnkeyedLeafBean[] getArrayLeaf();
    public void setArrayLeaf(UnkeyedLeafBean listLeaf[]);
    public UnkeyedLeafBean addArrayLeaf();
    public UnkeyedLeafBean removeArrayLeaf(int index);

}
