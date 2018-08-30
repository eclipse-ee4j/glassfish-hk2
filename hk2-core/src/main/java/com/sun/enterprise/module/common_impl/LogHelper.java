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

package com.sun.enterprise.module.common_impl;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * The logging relataed methods have been refactored out of Utils class
 * which exists in impl module.
 *
 * @author Sanjeeb.Sahoo@Sun.COM
 */
public class LogHelper {
    private static final Logger logger =
            Logger.getLogger("com.sun.enterprise.module");

    public static Logger getDefaultLogger() {
        return logger;
    }

    /**
     * @see Logger#isLoggable(java.util.logging.Level)
     * @return
     */
    public static boolean isLoggable(Level level) {
        return logger.isLoggable(level);
    }
}
