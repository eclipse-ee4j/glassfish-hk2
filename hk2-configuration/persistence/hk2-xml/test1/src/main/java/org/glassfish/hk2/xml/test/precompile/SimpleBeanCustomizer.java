/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.xml.test.precompile;

import org.jvnet.hk2.annotations.Service;

/**
 * @author jwells
 *
 */
@Service
public class SimpleBeanCustomizer {
    private boolean customizer12Called = false;
    private boolean listenerCustomizerCalled = false;
    
    public int customizer12(boolean z, int i, long j, float f, double d, byte b, short s, char c, int... var) {
        customizer12Called = true;
        return 13;
    }
    
    public void addListener(boolean[] z, byte[] b, char[] c, short[] s, int[] i, long[]j, String[] l) {
        if (z != null) {
            listenerCustomizerCalled = true;
        }
    }
    
    public boolean getCustomizer12Called() {
        return customizer12Called;
    }
    
    public boolean getListenerCustomizerCalled() {
        return listenerCustomizerCalled;
    }
    
    public void customizer13(BeanListenerInterface iFace) {
        iFace.doSomething();
    }
    
    public int customizer14(WorkerClass clazz) {
        return clazz.returnFourteen();
    }

}
