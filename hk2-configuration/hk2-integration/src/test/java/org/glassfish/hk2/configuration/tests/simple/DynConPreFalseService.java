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

import org.glassfish.hk2.configuration.api.Configured;
import org.glassfish.hk2.configuration.api.ConfiguredBy;
import org.glassfish.hk2.configuration.api.Dynamicity;
import org.glassfish.hk2.configuration.api.PreDynamicChange;
import org.jvnet.hk2.annotations.Service;

/**
 * @author jwells
 *
 */
@Service @ConfiguredBy(BasicConfigurationTest.TEST_TYPE_THREE)
public class DynConPreFalseService {
    @Configured(dynamicity=Dynamicity.FULLY_DYNAMIC)
    private String fieldOutput1;
    
    private String preChangeCalled = null;
    
    @PreDynamicChange
    private boolean preChange() {
        preChangeCalled = fieldOutput1;
        return false;
    }

    public String isPreChangeCalled() {
        return preChangeCalled;
    }
    
    public String getFieldOutput1() {
        return fieldOutput1;
    }

}
