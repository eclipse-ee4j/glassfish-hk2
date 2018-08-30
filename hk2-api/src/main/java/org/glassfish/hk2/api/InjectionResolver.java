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

package org.glassfish.hk2.api;

import org.jvnet.hk2.annotations.Contract;

/**
 * This class allows users to provide a custom injection target for
 * any annotation (including &#64;Inject).  The user would usually
 * only provide a resolver for &#64;Inject if it were specializing
 * the system provided resolver for &#64;Inject.  Otherwise, this
 * resolver can be used to provide injection points for any annotation.
 * <p>
 * An implementation of InjectionResolver must be in the Singleton scope.
 * Implementations of InjectionResolver will be instantiated as soon as
 * they are added to HK2 in order to avoid deadlocks and circular references.
 * Therefore it is recommended that implementations of InjectionResolver
 * make liberal use of {@link javax.inject.Provider} or {@link IterableProvider}
 * when injecting dependent services so that these services are not instantiated
 * when the InjectionResolver is created
 * 
 * @author jwells
 * @param <T> This must be the class of the injection annotation that this resolver
 * will handle
 */
@Contract
public interface InjectionResolver<T> {
    /** This is the name of the system provided resolver for 330 injections */
    public final static String SYSTEM_RESOLVER_NAME = "SystemInjectResolver";
    
    /**
     * This method will return the object that should be injected into the given
     * injection point.  It is the responsiblity of the implementation to ensure that
     * the object returned can be safely injected into the injection point.
     * <p>
     * This method should not do the injection themselves
     * 
     * @param injectee The injection point this value is being injected into
     * @param root The service handle of the root class being created, which should
     * be used in order to ensure proper destruction of associated &64;PerLookup
     * scoped objects.  This can be null in the case that this is being used
     * for an object not managed by HK2.  This will only happen if this
     * object is being created with the create method of ServiceLocator.
     * @return A possibly null value to be injected into the given injection point
     */
    public Object resolve(Injectee injectee, ServiceHandle<?> root);
    
    /**
     * This method should return true if the annotation that indicates that this is
     * an injection point can appear in the parameter list of a constructor.
     * 
     * @return true if the injection annotation can appear in the parameter list of
     * a constructor
     */
    public boolean isConstructorParameterIndicator();
    
    /**
     * This method should return true if the annotation that indicates that this is
     * an injection point can appear in the parameter list of a method.
     * 
     * @return true if the injection annotation can appear in the parameter list of
     * a method
     */
    public boolean isMethodParameterIndicator();

}
