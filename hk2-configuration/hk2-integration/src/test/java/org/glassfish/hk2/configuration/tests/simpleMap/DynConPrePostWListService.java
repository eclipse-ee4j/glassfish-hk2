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

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.glassfish.hk2.configuration.api.Configured;
import org.glassfish.hk2.configuration.api.ConfiguredBy;
import org.glassfish.hk2.configuration.api.Dynamicity;
import org.glassfish.hk2.configuration.api.PostDynamicChange;
import org.glassfish.hk2.configuration.api.PreDynamicChange;
import org.jvnet.hk2.annotations.Service;

/**
 * @author jwells
 *
 */
@Service @ConfiguredBy(MapConfigurationTest.TEST_TYPE_THREE)
public class DynConPrePostWListService {
    @Configured(dynamicity=Dynamicity.FULLY_DYNAMIC)
    private String fieldOutput1;
    
    private String preChangeCalled = null;
    private String postChangeCalled = null;
    
    private List<PropertyChangeEvent> preChangeList = null;
    private List<PropertyChangeEvent> postChangeList = null;
    
    @PreDynamicChange
    private void preChange(List<PropertyChangeEvent> changes) {
        preChangeCalled = fieldOutput1;
        preChangeList = changes;
    }
    
    @PostDynamicChange
    private void postChange(List<PropertyChangeEvent> changes) {
        postChangeCalled = fieldOutput1;
        postChangeList = changes;
    }

    public String isPostChangeCalled() {
        return postChangeCalled;
    }
    
    public String isPreChangeCalled() {
        return preChangeCalled;
    }
    
    public List<PropertyChangeEvent> getPostChangeList() {
        return postChangeList;
    }
    
    public List<PropertyChangeEvent> getPreChangeList() {
        return preChangeList;
    }
    
    public String getFieldOutput1() {
        return fieldOutput1;
    }

}
