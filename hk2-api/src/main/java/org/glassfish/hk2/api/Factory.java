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
 * This interface should be implemented in order to provide
 * a factory for another type.  This is useful when the type
 * has some reason that it cannot be a created in the usual way.
 * <p>
 * A factory may not have a TypeVariable or a Wildcard as its
 * actual type.  A factory may have any scope, and the scope
 * of the factory is independent of the scope of the type it
 * is providing.
 * <p>
 * The scope and qualifiers of the objects this factory is producing
 * must be placed on the provide method itself.  Objects created with
 * this method will be put into the scope on the provide method, and
 * will have the qualifiers of the provide method.
 * <p>
 * A factory is generally added with the {@link FactoryDescriptors} helper
 * class, though factories can also be registered independently.
 * <p>
 * A Factory implementation may inject a {@link InstantiationService}.  If
 * it does so then the {@link InstantiationService#getInstantiationData()} method
 * will return information about the caller of the provide method if that
 * information is available.  This can be used to customize the returned object
 * based on the Injection point of the parent.
 * 
 * @author jwells
 * @param <T> This must be the type of entity for which this is a factory.
 * For example, if this were a factory for Foo, then your factory
 * must implement Factory&lt;Foo&gt;.
 *
 */
@Contract
public interface Factory<T> {
    /**
     * This method will create instances of the type of this factory.  The provide
     * method must be annotated with the desired scope and qualifiers.
     * 
     * @return The produces object
     */
    public T provide();
    
    /**
     * This method will dispose of objects created with this scope.  This method should
     * not be annotated, as it is naturally paired with the provide method
     * 
     * @param instance The instance to dispose of
     */
    public void dispose(T instance);

}
