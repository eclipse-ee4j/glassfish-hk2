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

package org.glassfish.hk2.xml.test.customizer;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.glassfish.hk2.api.Customize;
import org.glassfish.hk2.api.Customizer;
import org.jvnet.hk2.annotations.Contract;

/**
 * @author jwells
 *
 */
@XmlRootElement(name="museum") @Contract
@Customizer(CustomizerOne.class)
public interface MuseumBean {
    @XmlElement
    public void setName(String name);
    public String getName();
    
    @XmlElement
    public void setAge(int age);
    public int getAge();
    
    @XmlAttribute
    public void setId(int id);
    public int getId();
    
    public String customizer1(String prefix, String postfix);
    public void customizer2();
    
    public long[] customizer3(String[][] anArray);
    
    public boolean customizer4();
    public int customizer5();
    public long customizer6();
    public float customizer7();
    public double customizer8();
    
    public byte customizer9();
    public short customizer10();
    public char customizer11();
    
    public int customizer12(boolean z, int i, long j, float f, double d, byte b, short s, char c, int[]... var);
    
    public String[] toUpper(String[] strings);
    
    @Customize
    public void addListener(BeanListener listener);
    
    @Customize
    public void theVeryBadNotGoodMethod();

}
