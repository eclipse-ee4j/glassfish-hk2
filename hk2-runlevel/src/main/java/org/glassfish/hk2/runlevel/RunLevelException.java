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

package org.glassfish.hk2.runlevel;

import org.glassfish.hk2.api.HK2RuntimeException;


/**
 * Exception related to the operations of the {@link RunLevelController}.
 *
 * @author jtrent, tbeerbower
 */
public class RunLevelException extends HK2RuntimeException {

    /**
     * For serialization
     */
    private static final long serialVersionUID = 1514027985824049713L;

    /**
     * Basic no-args constructor
     */
    public RunLevelException() {
        super();
    }
    
    /**
     * Exception with message
     * @param message The message to be associated with this exception
     */
    public RunLevelException(String message) {
        super(message);
    }

    /**
     * Exception with origin
     * @param origin The exception that caused the exception
     */
    public RunLevelException(Throwable origin) {
        super(origin);
    }

    /**
     * Exception with message and origin
     * 
     * @param message The message to be associated with this exception
     * @param origin The exception that caused the exception
     */
    public RunLevelException(String message, Throwable origin) {
        super(message, origin);
    }
}
