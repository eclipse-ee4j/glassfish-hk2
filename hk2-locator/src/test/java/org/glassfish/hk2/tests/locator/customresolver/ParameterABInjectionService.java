/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
 * @author Miroslav Fuksa (miroslav.fuksa at oracle.com)
 *
 */
public class ParameterABInjectionService {
    private final String parameterA;
    private final String anotherParameterA;
    private final String parameterB;

    public ParameterABInjectionService(@ParameterAInjectionPoint String parameterA,
                                       @ParameterBInjectionPoint String parameterB,
                                       @ParameterAInjectionPoint String anotherParameterA) {
        this.parameterA = parameterA;
        this.parameterB = parameterB;
        this.anotherParameterA = anotherParameterA;
    }

    public String getParameterA() {
        return parameterA;
    }

    public String getParameterB() {
        return parameterB;
    }

    public String getAnotherParameterA() {
        return anotherParameterA;
    }
}
