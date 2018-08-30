/*
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.xml.test.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.glassfish.hk2.xml.api.XmlRootHandle;

/**
 * @author jwells
 *
 */
public class HasChildWithNameValidator implements ConstraintValidator<HasChildWithName, String> {
    private static CaptureRootChangeListener ROOT_LISTENER;
    
    private HasChildWithName lastInitialization;

    /* (non-Javadoc)
     * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
     */
    @Override
    public void initialize(HasChildWithName arg0) {
        lastInitialization = arg0;
    }

    /* (non-Javadoc)
     * @see javax.validation.ConstraintValidator#isValid(java.lang.Object, javax.validation.ConstraintValidatorContext)
     */
    @Override
    public boolean isValid(String nameBeingSet, ConstraintValidatorContext arg1) {
        if (nameBeingSet == null) return true;
        if (ROOT_LISTENER == null) return true;
        
        XmlRootHandle<ConstraintRootBean> handle = ROOT_LISTENER.getRoot();
        ConstraintRootBean root = handle.getRoot();
        if (root == null) return false;
        
        Class<?> type = lastInitialization.type();
        if (type.equals(NamedBean.class)) {
            NamedBean foundBean = root.lookupNamed(nameBeingSet);
            
            return (foundBean != null);
        }
        
        return false;
    }
    
    public static void setRootListener(CaptureRootChangeListener listener) {
        ROOT_LISTENER = listener;
    }
}
