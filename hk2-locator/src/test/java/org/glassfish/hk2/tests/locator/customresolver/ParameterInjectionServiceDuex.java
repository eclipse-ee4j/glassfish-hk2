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

package org.glassfish.hk2.tests.locator.customresolver;

/**
 * @author jwells
 *
 */
public class ParameterInjectionServiceDuex {
    private final SimpleService injectedViaConstructor;
    private final SimpleServiceDuex injectedViaConstructorDuex;
    
    private SimpleService injectedViaMethod;
    private SimpleServiceDuex injectedViaMethodDuex;
    
    public ParameterInjectionServiceDuex() {
        injectedViaConstructor = null;
        injectedViaConstructorDuex = null;
    }
    
    
    public ParameterInjectionServiceDuex(@ParameterInjectionPoint SimpleService ss, SimpleServiceDuex duex) {
        injectedViaConstructor = ss;
        injectedViaConstructorDuex = duex;
    }
    
    public void setViaMethod(SimpleServiceDuex duex, @ParameterInjectionPoint SimpleService ss) {
        injectedViaMethod = ss;
        injectedViaMethodDuex = duex;
    }
    
    public SimpleService getViaConstructor() {
        return injectedViaConstructor;
    }
    
    public SimpleServiceDuex getViaConstructorDuex() {
        return injectedViaConstructorDuex;
    }
    
    public SimpleService getViaMethod() {
        return injectedViaMethod;
    }
    
    public SimpleServiceDuex getViaMethodDuex() {
        return injectedViaMethodDuex;
    }

}
