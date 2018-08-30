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

package org.glassfish.hk2.tests.interception;

import javax.inject.Singleton;

import org.glassfish.hk2.extras.interception.Intercepted;

/**
 * Has a class interceptor, method interceptors (different ones for
 * different methods)
 * @author jwells
 *
 */
@Singleton
@Recorder
@Intercepted
public class ComplexInterceptedService {
    @Recorder2
    public void interceptedByTwo() {
        
    }
    
    @Recorder3
    public void interceptedByThree() {
        
    }
    
    public void interceptedByClass() {
        
    }
    
    @Recorder3 @Recorder2 @Recorder @Deprecated
    public void interceptedByAll() {
        
    }
    
    @RecorderStereotype
    public void interceptedViaStereotype() {
        
    }

}
