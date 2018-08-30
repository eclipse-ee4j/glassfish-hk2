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

package org.glassfish.hk2.utilities.general;

import java.lang.annotation.ElementType;
import java.security.AccessController;
import java.security.PrivilegedAction;

import javax.validation.Path;
import javax.validation.TraversableResolver;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorContext;
import javax.validation.ValidatorFactory;

import org.glassfish.hk2.utilities.general.internal.MessageInterpolatorImpl;
import org.hibernate.validator.HibernateValidator;


/**
 * @author jwells
 *
 */
public class ValidatorUtilities {
    private static final TraversableResolver TRAVERSABLE_RESOLVER = new TraversableResolver() {
        public boolean isReachable(Object traversableObject,
                Path.Node traversableProperty, Class<?> rootBeanType,
                Path pathToTraversableObject, ElementType elementType) {
                    return true;
        }

        public boolean isCascadable(Object traversableObject,
                Path.Node traversableProperty, Class<?> rootBeanType,
                Path pathToTraversableObject, ElementType elementType) {
                    return true;
        }
        
    };
    
    private static Validator validator;
    
    private static Validator initializeValidator() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        
        try {      
            Thread.currentThread().setContextClassLoader(HibernateValidator.class.getClassLoader());
       
            ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
            ValidatorContext validatorContext = validatorFactory.usingContext();
            validatorContext.messageInterpolator(new MessageInterpolatorImpl());                
            return validatorContext.traversableResolver(
                       TRAVERSABLE_RESOLVER).getValidator();
        }
        finally {
            Thread.currentThread().setContextClassLoader(cl);
        }
    }
    
    /**
     * Gets a validator that can be used to validate that is initialized with HK2
     * specific utilities such as the message interpolator
     * 
     * @return A javax bean validator for validating constraints
     */
    public synchronized static Validator getValidator() {
        if (validator == null) {
            validator = AccessController.doPrivileged(new PrivilegedAction<Validator>() {

                @Override
                public Validator run() {
                    return initializeValidator();
                }
                
            });
        }
        
        if (validator == null) {
            throw new IllegalStateException("Could not find a javax.validator");
        }
        
        return validator;
    }

}
