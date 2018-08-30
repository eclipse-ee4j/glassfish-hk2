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

package org.jvnet.hk2.metadata.tests;

import org.jvnet.hk2.annotations.Service;

/**
 * @author jwells
 *
 */
@Service
@ScopeWithMetadata(value1=Constants.VALUE1,
                   value3=3,
                   value5={Constants.VALUE5_1,
                           Constants.VALUE5_2,
                           Constants.VALUE5_3})
@QualifierWithMetadata(value2=Constants.VALUE2,
                       value4=Constants.class,
                       value6={Constants.VALUE6_1,
                               Constants.VALUE6_2,
                               Constants.VALUE6_3})
public class ServiceWithMetadata {

}
