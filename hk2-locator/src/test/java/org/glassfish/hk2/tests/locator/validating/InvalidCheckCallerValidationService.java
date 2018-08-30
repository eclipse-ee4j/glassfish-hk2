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
public class InvalidCheckCallerValidationService implements ValidationService {
    private final MyValidator validator = new MyValidator();

    @Override
    public Filter getLookupFilter() {
        return BuilderHelper.allFilter();
    }

    @Override
    public Validator getValidator() {
        return validator;
    }
    
    public boolean check() {
        return validator.check();
    }
    
    public static class MyValidator implements Validator {
        private boolean done = false;
        private boolean check = false;

        @Override
        public boolean validate(ValidationInformation info) {
            MyRunner runner = new MyRunner(this, info);
            
            Thread t = new Thread(runner);
            t.start();
            
            synchronized (this) {
                while (!done) {
                    try {
                        this.wait();
                    }
                    catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            
            check = (runner.getElement() == null);
            
            return true;
        }
        
        public void done() {
            done = true;
        }
        
        public boolean check() {
            return check;
        }
        
    }
    
    public static class MyRunner implements Runnable {
        private final MyValidator parent;
        private final ValidationInformation info;
        private StackTraceElement element;
        
        private MyRunner(MyValidator parent, ValidationInformation info) {
            this.parent = parent;
            this.info = info;
        }

        @Override
        public void run() {
            synchronized (parent) {
                element = info.getCaller();
                parent.done();
                
                parent.notify();
            }
        }
        
        public StackTraceElement getElement() {
            synchronized (parent) {
                return element;
            }
        }
    }

}
