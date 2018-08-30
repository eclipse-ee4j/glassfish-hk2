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

import org.jvnet.hk2.annotations.Service;

/**
 * @author jwells
 *
 */
@Service
@ScopeWithMetadata(value1=InhabitantsGeneratorTest.VALUE1,
                   value3=3,
                   value5={InhabitantsGeneratorTest.VALUE5_1,
                           InhabitantsGeneratorTest.VALUE5_2,
                           InhabitantsGeneratorTest.VALUE5_3})
@QualifierWithMetadata(value2=InhabitantsGeneratorTest.VALUE2,
                       value4=InhabitantsGeneratorTest.class,
                       value6={InhabitantsGeneratorTest.VALUE6_1,
                               InhabitantsGeneratorTest.VALUE6_2,
                               InhabitantsGeneratorTest.VALUE6_3})
public class ServiceWithMetadata {

}
