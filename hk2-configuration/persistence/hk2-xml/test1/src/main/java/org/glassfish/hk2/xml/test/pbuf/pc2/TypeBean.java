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

package org.glassfish.hk2.xml.test.pbuf.pc2;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.glassfish.hk2.pbuf.api.annotations.Comment;
import org.glassfish.hk2.pbuf.api.annotations.GenerateProto;
import org.glassfish.hk2.xml.api.annotations.Hk2XmlPreGenerate;
import org.jvnet.hk2.annotations.Contract;

/**
 * @author jwells
 *
 */
@Contract
@Hk2XmlPreGenerate
@XmlType(propOrder={ "IType"
        , "JType"
        , "ZType"
        , "BType"
        , "CType"
        , "SType"
        , "FType"
        , "DType"
        , "string"})
@GenerateProto
@Comment("Copyright (c) 2017 Oracle and/or its affiliates. All rights reserved.\nThis program and the accompanying materials are made available under the\nterms of the Eclipse Public License v. 2.0, which is available at\nhttp://www.eclipse.org/legal/epl-2.0.\nThis Source Code may also be made available under the following Secondary\nLicenses when the conditions for such availability set forth in the\nEclipse Public License v. 2.0 are satisfied: GNU General Public License,\nversion 2 with the GNU Classpath Exception, which is available at\nhttps://www.gnu.org/software/classpath/license.html.\nSPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0")
public interface TypeBean {
    @XmlElement(name="int")
    public int getIType();
    public void setIType(int i);

    @XmlElement(name="long")
    public long getJType();
    public void setJType(long i);

    @XmlElement(name="boolean")
    public boolean getZType();
    public void setZType(boolean i);

    @XmlElement(name="byte")
    public byte getBType();
    public void setBType(byte i);

    @XmlElement(name="char")
    public char getCType();
    public void setCType(char i);

    @XmlElement(name="short")
    public short getSType();
    public void setSType(short i);

    @XmlElement(name="float")
    public float getFType();
    public void setFType(float i);

    @XmlElement(name="double")
    public double getDType();
    public void setDType(double i);

    @XmlElement(name="string", required=true)
    public String getString();
    public void setString(String string);

}
