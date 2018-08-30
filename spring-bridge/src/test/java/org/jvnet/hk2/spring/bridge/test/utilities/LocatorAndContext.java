/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.jvnet.hk2.spring.bridge.test.utilities;

import org.glassfish.hk2.api.ServiceLocator;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 
 * @author jwells
 *
 */
public class LocatorAndContext {
    private final ServiceLocator locator;
    private final ConfigurableApplicationContext context;
    
    /* package */ LocatorAndContext(ServiceLocator locator, ConfigurableApplicationContext context) {
        this.locator = locator;
        this.context = context;
    }
    
    /**
     * Gets the ServiceLocator
     * 
     * @return The ServiceLocator
     */
    public ServiceLocator getServiceLocator() { return locator; }
    
    /**
     * Gets the ApplicationContext
     * 
     * @return The ApplicationContext
     */
    public ConfigurableApplicationContext getApplicationContext() { return context; }
}
