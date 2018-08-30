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

package org.glassfish.hk2.xml.test.elements.beans;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import org.jvnet.hk2.annotations.Contract;

/**
 * @author jwells
 *
 */
@Contract
@XmlRootElement(name = "basic-elements")
public interface BasicElementalBean {
    @XmlElements({
        @XmlElement(name="earth", type=EarthBean.class)
        , @XmlElement(name="fire", type=FireBean.class)
        , @XmlElement(name="water", type=WaterBean.class)
        , @XmlElement(name="wind", type=WindBean.class)
        , @XmlElement(name="none", type=ElementalBean.class)
        , @XmlElement(name="special", type=ElementalBean.class)
        , @XmlElement(name="sand", type=EarthBean.class)
    })
    public List<ElementalBean> getEarthWindAndFire();
}
