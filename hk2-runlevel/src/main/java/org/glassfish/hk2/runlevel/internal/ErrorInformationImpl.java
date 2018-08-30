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
import org.glassfish.hk2.runlevel.ErrorInformation;

/**
 * @author jwells
 *
 */
public class ErrorInformationImpl implements ErrorInformation {
    private final Throwable error;
    private ErrorAction action;
    private final Descriptor descriptor;
    
    /* package */ ErrorInformationImpl(Throwable error, ErrorAction action, Descriptor descriptor) {
        this.error = error;
        this.action = action;
        this.descriptor = descriptor;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.runlevel.ErrorInformation#getError()
     */
    @Override
    public Throwable getError() {
        return error;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.runlevel.ErrorInformation#getAction()
     */
    @Override
    public ErrorAction getAction() {
        return action;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.runlevel.ErrorInformation#setAction(org.glassfish.hk2.runlevel.ErrorInformation.ErrorAction)
     */
    @Override
    public void setAction(ErrorAction action) {
        if (action == null) throw new IllegalArgumentException("action may not be null in setAction");
        
        this.action = action;
    }

    @Override
    public Descriptor getFailedDescriptor() {
        return descriptor;
    }
    
    @Override
    public String toString() {
        String descriptorString = (descriptor == null) ? "null" : descriptor.getImplementation();
        return "ErrorInformationImpl(" + action + "," + descriptorString + "," + System.identityHashCode(this) + ")";
    }

}
