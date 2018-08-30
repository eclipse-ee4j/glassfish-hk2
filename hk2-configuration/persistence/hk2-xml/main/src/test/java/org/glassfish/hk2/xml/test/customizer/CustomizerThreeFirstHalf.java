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

package org.glassfish.hk2.xml.test.customizer;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * @author jwells
 *
 */
@Singleton @Named(CustomizerTest.CAROL_NAME)
public class CustomizerThreeFirstHalf implements CustomOne {
    @Inject
    private MuseumBean customized;
    
    private boolean customizer2Called = false;
    
    public String customizer1(String prefix, String postfix) {
        return prefix + customized.getName() + postfix;
    }
    
    public void customizer2() {
        customizer2Called = true;
    }
    
    public boolean getCustomizer2Called() {
        return customizer2Called;
    }
    
    public long[] customizer3(String[][] anArray) {
        return new long[0];
    }
    
    public boolean customizer4() {
        return CustomizerTest.C4;
    }


}
