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

package org.glassfish.hk2.tests.locator.validating;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.ValidationInformation;
import org.glassfish.hk2.api.Validator;

/**
 * @author jwells
 *
 */
public class SecretValidator implements Validator {
    /**
     * Brilliant security.  Any class that has the word
     * System in it is a system class.  No way to spoof that one, eh?
     * 
     * @param injectedInto
     * @return
     */
    private boolean isSystemClass(Class<?> injectedInto) {
        if (injectedInto.getName().contains("System")) return true;
        
        return false;
    }
    
    private static Class<?> getDeclaringClass(Injectee injectee) {
        AnnotatedElement element = injectee.getParent();
        if (element instanceof Constructor) {
            return ((Constructor<?>) element).getDeclaringClass();
        }
        if (element instanceof Method) {
            return ((Method) element).getDeclaringClass();
        }
        
        return ((Field) element).getDeclaringClass();
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.InjectionPointValidator#validateInjectionPoint(java.lang.Class, org.glassfish.hk2.api.Injectee, java.lang.Class)
     */
    @Override
    public boolean validate(ValidationInformation info) {
        switch (info.getOperation()) {
        case BIND:
        case UNBIND:
            return true;
        }
        
        if (info.getInjectee() == null) return false;  // No direct lookups!
        
        return isSystemClass(getDeclaringClass(info.getInjectee()));
    }

}
