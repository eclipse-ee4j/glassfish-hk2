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

package org.glassfish.hk2.configuration.tests.threaded;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.glassfish.hk2.api.IterableProvider;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.configuration.api.Configured;
import org.glassfish.hk2.configuration.api.ConfiguredBy;
import org.jvnet.hk2.annotations.Service;

/**
 * @author jwells
 *
 */
@Service @ConfiguredBy(ConfiguredThreadedTest.THREADED_TYPE)
public class ConfiguredService {
    @Inject
    private AnotherConfiguredService acs;
    
    private String name;
    
    @Inject
    private ServiceLocator locator;
    
    @SuppressWarnings("unused")
    private synchronized void setName(@Configured(ConfiguredThreadedTest.NAME_KEY) String name) {
        this.name = name;
    }
    
    public synchronized String getName() {
        return name;
    }
    
    public synchronized AnotherConfiguredService getACS() {
        return acs;
    }
    
    public int runOthersDown() {
        int retVal = 0;
        List<ConfiguredService> allOfMe = locator.getAllServices(ConfiguredService.class);
        for (ConfiguredService cs : allOfMe) {
            retVal++;
        }
        
        return retVal;
    }

}
