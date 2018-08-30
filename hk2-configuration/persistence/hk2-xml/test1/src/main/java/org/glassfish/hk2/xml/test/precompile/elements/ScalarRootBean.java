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

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import org.glassfish.hk2.xml.api.annotations.Hk2XmlPreGenerate;
import org.jvnet.hk2.annotations.Contract;

/**
 * @author jwells
 *
 */
@Contract
@XmlRootElement(name = "scalars")
@Hk2XmlPreGenerate
public interface ScalarRootBean {
    @XmlElement(name="number")
    List<Integer> getNumbers();
    
    @XmlElement(name="array-number")
    int[] getArrayNumbers();
    
    @XmlElement(name="time")
    List<Long> getTimes();
    
    @XmlElement(name="array-time")
    Long[] getArrayTimes();
    
    @XmlElement(name="one-number")
    public int getOneNumber();
    
    public List<String> getStrings();
    @XmlElement(name="string")
    public void setStrings(List<String> strings);
    
    @XmlElements ({
        @XmlElement(name="e-integer", type=Integer.class)
        , @XmlElement(name="e-long", type=Long.class)
        , @XmlElement(name="e-boolean", type=Boolean.class)
        , @XmlElement(name="e-short", type=Short.class)
        , @XmlElement(name="e-character", type=Character.class)
        , @XmlElement(name="e-float", type=Float.class)
        , @XmlElement(name="e-double", type=Double.class)
        , @XmlElement(name="e-string", type=String.class)
        // , @XmlElement(name="e-earth", type=EarthBean.class)
    })
    List<Object> getTypesTypesAndMoreTypes();

}
