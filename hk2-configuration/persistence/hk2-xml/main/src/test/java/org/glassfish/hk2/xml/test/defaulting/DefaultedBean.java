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

package org.glassfish.hk2.xml.test.defaulting;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;

import org.jvnet.hk2.annotations.Contract;

/**
 * @author jwells
 *
 */
@XmlRootElement(name="defaulted-bean",
  namespace="http://hk2.java.net/hk2-xml/test/defaulting") @Contract
public interface DefaultedBean {
    /**
     * This one has no setter
     * @return
     */
    @XmlElement(name="int-prop", defaultValue="13")
    public int getIntProp();
    
    /**
     * This one has a setter
     * JAXB annotation on the getter
     * @return
     */
    @XmlElement(name="long-prop", defaultValue="13")
    public long getLongProp();
    public void setLongProp(long prop);
    
    /**
     * This one has a setter
     * JAXB annotation on the setter
     * @return
     */
    @XmlElement(name="short-prop", defaultValue="13")
    public void setShortProp(short prop);
    public short getShortProp();
    
    @XmlElement(name="byte-prop", defaultValue="13")
    public void setByteProp(byte prop);
    public byte getByteProp();
    
    @XmlElement(name="boolean-prop", defaultValue="true")
    public boolean isBooleanProp();
    public void setBooleanProp(boolean prop);
    
    @XmlElement(name="char-prop", defaultValue="f")
    public char getCharProp();
    public void setCharProp(char prop);
    
    @XmlElement(name="float-prop", defaultValue="13.00")
    public float getFloatProp();
    public void setFloatProp(float prop);
    
    @XmlElement(name="double-prop", defaultValue="13.00")
    public double getDoubleProp();
    public void setDoubleProp(double prop);
    
    @XmlElement(name="string-prop", defaultValue="13")
    public String getStringProp();
    public void setStringProp(String prop);
    
    @XmlElement(name="qname-prop", defaultValue="xyz:foo")
    public QName getQNameProp();
    public void setQNameProp(QName qname);
    
    @XmlElement(name="color", defaultValue="GREEN")
    public Colors getColor();
    public void setColor(Colors color);
    
    // Below are testing the default defaults which should all be zero
    
    public int getDefaultIntProp();
    public long getDefaultLongProp();
    public short getDefaultShortProp();
    public byte getDefaultByteProp();
    public boolean isDefaultBooleanProp();
    public char getDefaultCharProp();
    public float getDefaultFloatProp();
    public double getDefaultDoubleProp();
    public String getDefaultStringProp();
    public QName getDefaultQNameProp();
    public Colors getDefaultColor();
}
