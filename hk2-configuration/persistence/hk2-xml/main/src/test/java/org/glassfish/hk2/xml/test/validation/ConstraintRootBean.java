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

package org.glassfish.hk2.xml.test.validation;

import java.util.List;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author jwells
 *
 */
@XmlRootElement(name="constraint-root-bean")
public interface ConstraintRootBean {
    @XmlElement(name="named-bean")
    @Valid
    List<NamedBean> getNamed();
    NamedBean addNamed(NamedBean named);
    NamedBean lookupNamed(String name);
    boolean removeNamed(NamedBean toRemove);
    
    @XmlElement(name="valid-1")
    @Valid
    List<BeanToValidate1Bean> getValid1();
    BeanToValidate1Bean addValid1(BeanToValidate1Bean valid1);
}
