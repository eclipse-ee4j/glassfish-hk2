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

package org.glassfish.hk2.runlevel.internal;

import org.glassfish.hk2.api.Descriptor;

/**
 * @author jwells
 *
 */
public class WouldBlockException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 3273389390415705962L;
    
    /**
     * This indicates a blocking condition on a given descriptor
     * 
     * @param d The non-null descriptor that would cause a blocking condition
     */
    public WouldBlockException(Descriptor d) {
        super("This descriptor would block: " + d);
    }
}
