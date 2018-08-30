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

package org.jvnet.hk2.internal;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.MultiException;

/**
 * @author jwells
 *
 */
public class ErrorResults {
    private final ActiveDescriptor<?> descriptor;
    private final Injectee injectee;
    private final MultiException me;
    
    /* package */ ErrorResults(
            ActiveDescriptor<?> descriptor,
            Injectee injectee,
            MultiException me) {
        this.descriptor = descriptor;
        this.injectee = injectee;
        this.me = me;
    }

    /**
     * @return the descriptor
     */
    ActiveDescriptor<?> getDescriptor() {
        return descriptor;
    }

    /**
     * @return the injectee
     */
    Injectee getInjectee() {
        return injectee;
    }

    /**
     * @return the me
     */
    MultiException getMe() {
        return me;
    }
    
    public String toString() {
        return "ErrorResult(" + descriptor + "," + injectee + "," + me + "," + System.identityHashCode(this) + ")";
    }
    
}
