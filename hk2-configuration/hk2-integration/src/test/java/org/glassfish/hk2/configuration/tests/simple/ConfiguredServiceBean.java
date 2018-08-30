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

package org.glassfish.hk2.configuration.tests.simple;

/**
 * JavaBean for the ConfiguredService service
 * 
 * @author jwells
 *
 */
public class ConfiguredServiceBean {
    private String fieldOutput1;
    private String fieldOutput2;
    private String constructorOutput;
    private String methodOutput1;
    private String methodOutput2;
    
    /**
     * @return the fieldOutput1
     */
    public String getFieldOutput1() {
        return fieldOutput1;
    }
    /**
     * @param fieldOutput1 the fieldOutput1 to set
     */
    public void setFieldOutput1(String fieldOutput1) {
        this.fieldOutput1 = fieldOutput1;
    }
    /**
     * @return the fieldOutput2
     */
    public String getFieldOutput2() {
        return fieldOutput2;
    }
    /**
     * @param fieldOutput2 the fieldOutput2 to set
     */
    public void setFieldOutput2(String fieldOutput2) {
        this.fieldOutput2 = fieldOutput2;
    }
    /**
     * @return the constructorOutput
     */
    public String getConstructorOutput() {
        return constructorOutput;
    }
    /**
     * @param constructorOutput the constructorOutput to set
     */
    public void setConstructorOutput(String constructorOutput) {
        this.constructorOutput = constructorOutput;
    }
    /**
     * @return the methodOutput1
     */
    public String getMethodOutput1() {
        return methodOutput1;
    }
    /**
     * @param methodOutput1 the methodOutput1 to set
     */
    public void setMethodOutput1(String methodOutput1) {
        this.methodOutput1 = methodOutput1;
    }
    /**
     * @return the methodOutput2
     */
    public String getMethodOutput2() {
        return methodOutput2;
    }
    /**
     * @param methodOutput2 the methodOutput2 to set
     */
    public void setMethodOutput2(String methodOutput2) {
        this.methodOutput2 = methodOutput2;
    }
    
    

}
