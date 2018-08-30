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

package org.glassfish.hk2.tests.locator.validating;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Singleton;

import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.ValidationInformation;
import org.glassfish.hk2.api.ValidationService;
import org.glassfish.hk2.api.Validator;
import org.glassfish.hk2.utilities.BuilderHelper;

/**
 * @author jwells
 *
 */
@Singleton
public class CheckCallerValidationService implements ValidationService {
    private final MyValidator myValidator = new MyValidator();

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ValidationService#getLookupFilter()
     */
    @Override
    public Filter getLookupFilter() {
        return BuilderHelper.allFilter();
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ValidationService#getValidator()
     */
    @Override
    public Validator getValidator() {
        return myValidator;
    }
    
    public List<StackTraceElement> getLastCaller() {
        return myValidator.getLastCaller();
    }
    
    public void clear() {
        myValidator.clear();
    }
    
    private static class MyValidator implements Validator {
        private final LinkedList<StackTraceElement> lastCaller = new LinkedList<StackTraceElement>();

        @Override
        public boolean validate(ValidationInformation info) {
            lastCaller.addFirst(info.getCaller());
            
            return true;  // Everybody is kool
        }
        
        public List<StackTraceElement> getLastCaller() {
            return lastCaller;
        }
        
        public void clear() {
            lastCaller.clear();
        }
    }

}
