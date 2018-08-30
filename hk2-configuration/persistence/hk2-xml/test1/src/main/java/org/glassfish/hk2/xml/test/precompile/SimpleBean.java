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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.glassfish.hk2.api.Customize;
import org.glassfish.hk2.api.Customizer;
import org.glassfish.hk2.xml.api.annotations.Hk2XmlPreGenerate;
import org.glassfish.hk2.xml.test.precompile.anno.EverythingBagel;
import org.glassfish.hk2.xml.test.precompile.anno.GreekEnum;

/**
 * @author jwells
 *
 */
@Hk2XmlPreGenerate
@XmlRootElement(name="simple-bean")
@Customizer(SimpleBeanCustomizer.class)
public interface SimpleBean {
    @XmlElement
    public String getName();
    public void setName(String name);
    
    @XmlElement(name="bagel-type")
    @EverythingBagel(byteValue = 13,
        booleanValue=true,
        charValue = 'e',
        shortValue = 13,
        intValue = 13,
        longValue = 13L,
        floatValue = (float) 13.00,
        doubleValue = 13.00,
        enumValue = GreekEnum.BETA,
        stringValue = "13",
        classValue = PreCompiledRoot.class,
    
        byteArrayValue = { 13, 14 },
        booleanArrayValue = { true, false },
        charArrayValue = { 'e', 'E' },
        shortArrayValue = { 13, 14 },
        intArrayValue = { 13, 14 },
        longArrayValue = { 13L, 14L },
        floatArrayValue = { (float) 13.00, (float) 14,00 },
        doubleArrayValue = { 13.00, 14.00 },
        enumArrayValue = { GreekEnum.GAMMA, GreekEnum.ALPHA },
        stringArrayValue = { "13", "14" },
        classArrayValue = { String.class, double.class })
    public int getBagelPreference();
    public void setBagelPreference(int bagelType);
    
    public int customizer12(boolean z, int i, long j, float f, double d, byte b, short s, char c, int... var);
    
    @Customize
    public void addListener(boolean[] z, byte[] b, char[] c, short[] s, int[] i, long[]j, String[] l);
    
    /**
     * This customized method references an interface that is not a child
     * 
     * @param iFace An interface that is not a child
     */
    public void customizer13(BeanListenerInterface iFace);
    
    /**
     * This customized method references a hard class that is not a child
     * 
     * @param clazz An class that is not a child
     */
    public int customizer14(WorkerClass clazz);
}
