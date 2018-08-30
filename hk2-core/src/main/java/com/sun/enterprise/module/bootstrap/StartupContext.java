/*
 * Copyright (c) 2007, 2018 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.enterprise.module.bootstrap;

import java.util.Properties;

/**
 * This class contains important information about the startup process.
 * This is one of the initial objects to be populated in the {@link org.jvnet.hk2.component.Habitat},
 * so {@link Populator}s can depend on this object.
 *
 * Do not add domain specific knowledge here. Since this takes a properties object in the constructor,
 * such knowledge can be maintained outside this object.
 *
 * @author Jerome Dochez, Sanjeeb Sahoo
 */

public class StartupContext {
    final Properties args;
    final long timeZero;
    public final static String TIME_ZERO_NAME = "__time_zero";  //NO I18N
    public final static String STARTUP_MODULE_NAME = "hk2.startup.context.mainModule";
    public final static String STARTUP_MODULESTARTUP_NAME = "hk2.startup.context.moduleStartup";

    public StartupContext() {
        this(new Properties());
    }

    public StartupContext(Properties args) {
        this.args = (Properties)args.clone();
        if (this.args.containsKey(TIME_ZERO_NAME)) {
            this.timeZero = Long.decode(this.args.getProperty(TIME_ZERO_NAME));
        } else {
            this.timeZero = System.currentTimeMillis();
        }
    }

    /**
     * Return the properties that constitues this context. Except the well known properties like
     * {@link #TIME_ZERO_NAME}, {@link #STARTUP_MODULE_NAME}, {@link #STARTUP_MODULESTARTUP_NAME},
     * this class does not know about any other properties. It is up to the user set them and get them.
     *
     */
    public Properties getArguments() {
        return args;
    }

    public String getStartupModuleName() {
        return String.class.cast(args.get(STARTUP_MODULE_NAME));
    }

    public String getPlatformMainServiceName() {
        String v = String.class.cast(args.get(STARTUP_MODULESTARTUP_NAME));
//        // todo : dochez, horrible hack to work around ArgumentManager clumsyness
//        if (v==null) {
//            return String.class.cast(args.get("-"+ STARTUP_MODULESTARTUP_NAME));
//        }
        return v;
    }
    
    /**
     * Returns the time at which this StartupContext instance was created.
     * This is roughly the time at which the hk2 program started.
     *
     * @return the instanciation time
     */
    public long getCreationTime() {
        return timeZero;
    }
    
}
