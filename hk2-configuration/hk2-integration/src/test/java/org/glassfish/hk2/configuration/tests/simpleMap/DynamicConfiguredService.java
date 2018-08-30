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

import org.glassfish.hk2.configuration.api.Configured;
import org.glassfish.hk2.configuration.api.ConfiguredBy;
import org.glassfish.hk2.configuration.api.Dynamicity;
import org.jvnet.hk2.annotations.Service;

/**
 * Very basic service that has dynamically configured field and method
 * parameters
 * 
 * @author jwells
 *
 */
@Service @ConfiguredBy(MapConfigurationTest.TEST_TYPE_TWO)
public class DynamicConfiguredService {
    @Configured(dynamicity=Dynamicity.FULLY_DYNAMIC)
    private String fieldOutput1;
    
    private String methodOutput1;
    
    @SuppressWarnings("unused")
    private void setMethodOutput1(@Configured(value="methodOutput1",
                                              dynamicity=Dynamicity.FULLY_DYNAMIC)
                                              String methodOutput1) {
        this.methodOutput1 = methodOutput1;
    }
    
    public String getFieldOutput1() {
        return fieldOutput1;
    }
    
    public String getMethodOutput1() {
        return methodOutput1;
    }

}
