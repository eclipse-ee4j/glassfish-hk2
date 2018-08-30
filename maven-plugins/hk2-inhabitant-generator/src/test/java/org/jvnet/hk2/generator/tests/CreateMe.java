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

package org.jvnet.hk2.generator.tests;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.jvnet.hk2.config.GenerateServiceFromMethod;
import org.jvnet.hk2.config.GeneratedServiceName;

/**
 * @author jwells
 *
 */
@Retention(RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE } )
@GenerateServiceFromMethod(implementation=InhabitantsGeneratorTest.GENERATE_METHOD_CREATE_IMPL,
    advertisedContracts={InhabitantsGeneratorTest.GENERATE_METHOD_CREATE_IMPL,
        InhabitantsGeneratorTest.GENERATE_METHOD_CREATE_CONTRACT})
public @interface CreateMe {
    @GeneratedServiceName
    public String value();
}
