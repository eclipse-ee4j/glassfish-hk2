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

package org.glassfish.hk2.xml.test.basic.beans;

import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.jvnet.hk2.annotations.Contract;

/**
 * @author jwells
 *
 */
@XmlRootElement(name="root-bean-with-properties")
@Contract
public interface RootBeanWithProperties {
    @XmlElement(name="properties")
    @XmlJavaTypeAdapter(PropertyAdapter.class)
    public Map<String, String> getProperties();
    
    @XmlElement(name="collapsed-property")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public String getCollapsedProperty();
    
    @XmlElement(name="naked-property")
    public List<PropertyBean> getNakedProperties();
    public void setNakedProperties(List<PropertyBean> beans);
}
