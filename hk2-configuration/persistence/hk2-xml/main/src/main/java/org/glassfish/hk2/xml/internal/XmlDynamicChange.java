/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.xml.internal;

import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.configuration.hub.api.WriteableBeanDatabase;

/**
 * @author jwells
 *
 */
public class XmlDynamicChange {
    public static final XmlDynamicChange EMPTY = new XmlDynamicChange(null, null, null);
    
    private final WriteableBeanDatabase userDatabase;
    private final DynamicConfiguration userDynamicConfiguration;
    private final DynamicConfiguration systemDynamicConfiguration;
    
    public XmlDynamicChange(WriteableBeanDatabase userDatabase, DynamicConfiguration userDynamicConfiguration, DynamicConfiguration systemDynamicConfiguration) {
        this.userDatabase = userDatabase;
        this.userDynamicConfiguration = userDynamicConfiguration;
        this.systemDynamicConfiguration = systemDynamicConfiguration;
    }
    
    public WriteableBeanDatabase getBeanDatabase() {
        return userDatabase;
    }
    
    public DynamicConfiguration getDynamicConfiguration() {
        return userDynamicConfiguration;
    }
    
    public DynamicConfiguration getSystemDynamicConfiguration() {
        return systemDynamicConfiguration;
    }
    
    @Override
    public String toString() {
        return "XmlDynamicChange(" + userDatabase + "," + userDynamicConfiguration + "," + systemDynamicConfiguration + "," + System.identityHashCode(this) + ")";
    }
}
