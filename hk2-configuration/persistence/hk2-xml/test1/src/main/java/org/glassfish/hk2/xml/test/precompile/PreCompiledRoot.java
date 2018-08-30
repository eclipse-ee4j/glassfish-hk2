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

package org.glassfish.hk2.xml.test.precompile;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.glassfish.hk2.api.Customize;
import org.glassfish.hk2.api.Customizer;
import org.glassfish.hk2.xml.api.annotations.Hk2XmlPreGenerate;

/**
 * @author jwells
 *
 */
@Hk2XmlPreGenerate
@XmlRootElement(name="root")
@Customizer(MyCustomizer.class)
public interface PreCompiledRoot {
    @XmlElement(name="pre-compiled-multi-child")
    public List<PreCompiledMultiChild> getPreCompiledMultiChild();
    
    @XmlElement(name="multi-child")
    public List<MultiChild> getMultiChild();
    
    @XmlElement(name="pre-compiled-direct-child")
    public PreCompiledDirectChild getPreCompiledDirectChild();
    
    @XmlElement(name="direct-child")
    public DirectChild getDirectChild();
    
    @XmlElement(name="pre-compiled-array-child")
    public void setPreCompiledArrayChild(PreCompiledArrayChild children[]);
    public PreCompiledArrayChild[] getPreCompiledArrayChild();
    
    @XmlElement(name="array-child")
    public ArrayChild[] getArrayChild();
    public void setArrayChild(ArrayChild children[]);
    
    @Customize
    public CustomizedReturn[] getCustomizedReturner();
    
    public CustomizedReturn[] aCustomizedThingWithParameter(CustomizedParameter hello);
    
    public void aCustomizedThingWithParameters(double aScalar, CustomizedParameter[] anArray, CustomizedReturn anInterface);
}
