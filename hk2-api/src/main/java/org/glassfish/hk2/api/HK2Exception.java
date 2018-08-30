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

package org.glassfish.hk2.api;

/**
 * Base class for HK2 defined checked exceptions
 * 
 * @author jwells
 *
 */
public class HK2Exception extends Exception {

    /**
     * For serialization
     */
    private static final long serialVersionUID = -6933923442167686426L;

    /**
     * 
     */
    public HK2Exception() {
    }

    /**
     * @param message
     */
    public HK2Exception(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public HK2Exception(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public HK2Exception(String message, Throwable cause) {
        super(message, cause);
    }

}
