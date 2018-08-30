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

package org.glassfish.hk2.xml.test.pbuf.pc1;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.glassfish.hk2.pbuf.api.annotations.Comment;
import org.glassfish.hk2.pbuf.api.annotations.GenerateProto;
import org.glassfish.hk2.pbuf.api.annotations.OneOf;
import org.glassfish.hk2.xml.api.annotations.Hk2XmlPreGenerate;
import org.glassfish.hk2.xml.test.pbuf.pc2.PBufPrecompileChild2;
import org.jvnet.hk2.annotations.Contract;

/**
 * @author jwells
 *
 */
@Contract
@Hk2XmlPreGenerate
@XmlRootElement(name="root")
@XmlType(propOrder={ "name"
        , "IType"
        , "remoteTypes"
        , "localTypes"
        , "firstThing"
        , "secondThing"
        , "thirdThing"
        , "fourthThing"
        , "fifthThing"
        })
@GenerateProto
@Comment("Copyright (c) 2017 Oracle and/or its affiliates. All rights reserved.\nThis program and the accompanying materials are made available under the\nterms of the Eclipse Public License v. 2.0, which is available at\nhttp://www.eclipse.org/legal/epl-2.0.\nThis Source Code may also be made available under the following Secondary\nLicenses when the conditions for such availability set forth in the\nEclipse Public License v. 2.0 are satisfied: GNU General Public License,\nversion 2 with the GNU Classpath Exception, which is available at\nhttps://www.gnu.org/software/classpath/license.html.\nSPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0\n" +
         "This is a comment on the root bean")
public interface PBufPrecompileRootBean {
    @Comment("This is a multiline comment\n"
            + "wherein there are multiple lines\n"
            + "for this comment.  I will supply now a limerick\n\n"
            + "There was a young man so benighted\n"
            + "He never knew when he was slighted;\n"
            + "He would go to a party\n"
            + "And eat just as hearty,\n"
            + "As if he'd been really invited.")
    @XmlElement
    public String getName();
    public void setName(String name);

    @XmlAttribute(name="itype")
    public int getIType();
    public void setIType(int iType);

    @Comment("This is a single line comment")
    @XmlElement(name="localTypes")
    public List<PBufPrecompileChild> getLocalTypes();
    public void addLocalType(PBufPrecompileChild addMe);

    @XmlElement(name="remoteTypes")
    public PBufPrecompileChild2[] getRemoteTypes();
    public void addRemoteType(PBufPrecompileChild2 addMe);

    @XmlElement @OneOf("FirstOneOf")
    public ThingBean getFirstThing();
    public void setFirstThing(ThingBean first);

    @XmlElement @OneOf("FirstOneOf")
    public ThingBean getSecondThing();
    public void setSecondThing(ThingBean second);

    @XmlElement @OneOf("SecondOneOf")
    public ThingOneBean getThirdThing();
    public void setThirdThing(ThingOneBean third);

    @XmlElement @OneOf("SecondOneOf")
    public ThingTwoBean getFourthThing();
    public void setFourthThing(ThingTwoBean fouth);

    @XmlElement @OneOf("SecondOneOf")
    public String getFifthThing();
    public void setFifthThing(String fifth);
}
