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

package org.jvnet.hk2.metadata.tests.stub.impl;

/**
 * @author jwells
 *
 */
public abstract class AbstractBaseService {
    public void aMethod() {}
    
    public abstract int bMethod(float[] param);
    
    public abstract InnerEnum cMethod(InnerEnum param0);
    
    public abstract InnerInterface dMethod(InnerInterface param0);
    
    public abstract InnerClass eMethod(InnerClass param0);
    
    public abstract double[] fMethod(InnerInterface param0[], InnerClass param1[], InnerEnum[]... param2);
    
    public enum InnerEnum {
        A, B
    }
    
    public interface InnerInterface {
        public int innerMethod();
    }
    
    public class InnerClass {
        public int innerMethod() {
            return 12;
        }
    }

}
