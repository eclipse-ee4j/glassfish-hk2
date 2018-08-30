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

package org.glassfish.hk2.configuration.tests.simpleMap;

import javax.annotation.PostConstruct;

import org.glassfish.hk2.configuration.api.Configured;
import org.glassfish.hk2.configuration.api.ConfiguredBy;
import org.junit.Assert;
import org.jvnet.hk2.annotations.Service;

/**
 * @author jwells
 */
@Service @ConfiguredBy(MapConfigurationTest.TEST_TYPE_ONE)
public class ConfiguredService {
    @Configured
    private String fieldOutput1;
    
    @Configured("fieldOutput2")
    private String anotherField;
    
    private final String constructorOutput;
    private String methodOutput1;
    private String methodOutput2;
    
    private ConfiguredService(@Configured("constructorOutput") String constructorOutput,
            SimpleService simpleService) {
        simpleService.hashCode();  //throws NPE if simpleService is null
        this.constructorOutput = constructorOutput;
    }
    
    @SuppressWarnings("unused")
    private void setMethodOutput1(@Configured("methodOutput1") String methodOutput1) {
        this.methodOutput1 = methodOutput1;
        
    }
    
    @SuppressWarnings("unused")
    private void anotherMethodInitializer(@Configured("methodOutput2") String methodOutput2,
            SimpleService simpleService) {
        simpleService.hashCode();  //throws NPE if simpleService is null
        
        this.methodOutput2 = methodOutput2;
    }
    
    @PostConstruct
    private void postConstruct() {
        Assert.assertNotNull(fieldOutput1);
        Assert.assertNotNull(anotherField);
        Assert.assertNotNull(constructorOutput);
        Assert.assertNotNull(methodOutput1);
        Assert.assertNotNull(methodOutput2);
    }
    
    public String getFieldOutput1() {
        return fieldOutput1;
    }
    
    public String getFieldOutput2() {
        return anotherField;
    }
    
    public String getConstructorOutput() {
        return constructorOutput;
    }
    
    public String getMethodOutput1() {
        return methodOutput1;
    }

    public String getMethodOutput2() {
        return methodOutput2;
    }
}
