/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.utilities.test;

/**
 * @author jwells
 *
 */
public class GenericJavaBean {
    private String value;
    private int anotherValue;
    private long thirdValue;
    private Generic2JavaBean bean2;
    private Generic3JavaBean bean3;
    
    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }
    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }
    /**
     * @return the anotherValue
     */
    public int getAnotherValue() {
        return anotherValue;
    }
    /**
     * @param anotherValue the anotherValue to set
     */
    public void setAnotherValue(int anotherValue) {
        this.anotherValue = anotherValue;
    }
    /**
     * @return the thirdValue
     */
    public long getThirdValue() {
        return thirdValue;
    }
    /**
     * @param thirdValue the thirdValue to set
     */
    public void setThirdValue(long thirdValue) {
        this.thirdValue = thirdValue;
    }
    
    public Generic2JavaBean getBean2() { return bean2; }
    public void setBean2(Generic2JavaBean bean) {
        this.bean2 = bean;
    }
    
    public Generic3JavaBean getBean3() { return bean3; }
    public void setBean3(Generic3JavaBean bean) {
        this.bean3 = bean;
    }

}
