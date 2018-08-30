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

package org.glassfish.hk2.xml.test.validation;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;

import org.glassfish.hk2.xml.api.annotations.PluralOf;
import org.jvnet.hk2.annotations.Contract;

/**
 * @author jwells
 *
 */
@XmlRootElement(name="validation") @Contract
public interface ValidationRootBean {
    @XmlElement @NotNull @XmlID
    public String getName();
    public void setName(String name);
    
    @XmlElement(name="element-one")
    @NotNull
    public String getElementOne();
    public void setElementOne(String elementOne);
    
    @XmlElement(name="list-child") @PluralOf("ListChild")
    @Valid
    public List<ValidationChildBean> getListChildren();
    public void setListChildren(List<ValidationChildBean> list);
    public void addListChild(ValidationChildBean toAdd);
    public void removeListChild(ValidationChildBean toRemove);
    
    @XmlElement(name="array-child") @PluralOf("ArrayChild")
    @Valid
    public ValidationChildArrayBean[] getArrayChildren();
    public void setArrayChildren(ValidationChildArrayBean[] list);
    public void addArrayChild(ValidationChildArrayBean toAdd);
    public void removeArrayChild(ValidationChildArrayBean toRemove);
    
    @XmlElement(name="direct-child")
    @Valid
    public ValidationChildDirectBean getDirectChild();
    public void setDirectChild(ValidationChildDirectBean direct);

}
