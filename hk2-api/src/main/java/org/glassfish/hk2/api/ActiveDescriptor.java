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

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

/**
 * An ActiveDescriptor contains information about a Descriptor after it has been reified.
 * Most of the methods in an ActiveDescriptor cannot be called until the isReified method
 * return true.  Prior to that the information is not yet known.  ActiveDescriptors
 * can be reified with the {@link ServiceLocator}.reifyDescriptor method.
 * <p>
 * ActiveDescriptors may also be provided to the system pre-reified, which is useful
 * when the Service described may be produced by a third-party system.  In this case
 * the ActiveDescriptors create method must use the third-party system in order to
 * create instances of the described service
 * 
 * @author jwells
 * 
 * @param <T> This is the type produced by the cache and consumed by the cache
 */
public interface ActiveDescriptor<T> extends Descriptor, SingleCache<T> {
    /**
     * This method returns true if this descriptor has been reified
     * (class loaded).  If this method returns false then the other methods
     * in this interface will throw an IllegalStateException.  Once this
     * method returns true it may be
     * 
     * @return true if this descriptor has been reified, false otherwise
     */
    public boolean isReified();
    
    /**
     * The implementation class that should be used
     * to generate new instances of this descriptor.
     * <p>
     * If the class returned is a Factory, then the
     * factory is used to create instances.  In this case
     * the system will get an instance of the factory and
     * use it to create the instances
     *  
     * @return The class that directly implements the contract
     * types, or the class that is the factory for an object
     * that implements the contract types
     */
    public Class<?> getImplementationClass();
    
    /**
     * If known the Type of the implementation.  If unknown will
     * return the same as {@link #getImplementationClass()}
     * 
     * @return The type of the implementation or the
     * implementation class
     */
    public Type getImplementationType();
    
    /**
     * The set of types that this ActiveDescriptor must produce.
     * These types may be Classes or ParameterizedTypes, and
     * may be no other subclass of Type
     * 
     * @return the set of types this ActiveDescriptor must implement
     * or extend
     */
    public Set<Type> getContractTypes();
    
    /**
     * Returns the scope as an {@link Annotation}
     * implementation that this ActiveDescriptor belongs to
     * 
     * @return The scope of this ActiveDescriptor as an {@link Annotation}
     */
    public Annotation getScopeAsAnnotation();
    
    /**
     * Returns the scope that this ActiveDescriptor belongs to
     * 
     * @return The scope of this ActiveDescriptor
     */
    public Class<? extends Annotation> getScopeAnnotation();
    
    /**
     * The full set of qualifiers that this ActiveDescriptor
     * provides
     * 
     * @return The set of annotations that this ActiveDescriptor
     * provides
     */
    public Set<Annotation> getQualifierAnnotations();
    
    /**
     * Returns the full list of Injectees this class has.  These
     * references will be resolved prior to the class being constructed,
     * even if these injectees are field or method injectees.
     * <p>
     * If this descriptor is describing a factory created type then
     * this list must have zero length
     * 
     * @return Will not return null, but may return an empty list.  The set
     * of Injectees that must be resolved before this ActiveDescriptor can
     * be constructed
     */
    public List<Injectee> getInjectees();
    
    /**
     * If this ActiveDescriptor has DescriptorType of PROVIDE_METHOD then
     * this field will return the ServiceId of its associated Factory
     * service.  Otherwise this method should return null
     * 
     * @return The service ID of the associated factory service
     */
    public Long getFactoryServiceId();
    
    /**
     * If this ActiveDescriptor has DescriptorType of PROVIDE_METHOD then
     * this field will return the ServiceId of its associated Factory
     * service.  Otherwise this method should return null
     * 
     * @return The locator ID of the associated factory service
     */
    public Long getFactoryLocatorId();
    
    /**
     * Creates an instance of the ActiveDescriptor.  All of the
     * Injectee's must be created prior to instantiation, and
     * associated with the ExtendedProvider so that they can be
     * destroyed properly
     * 
     * @param root The root service handle, which can be used
     * to associated all the PerLookup objects with this creation
     * 
     * @return An instance of this ActiveDescriptor
     */
    public T create(ServiceHandle<?> root);
    
    /**
     * Disposes this instance.  All the PerLookup objects that
     * were created for this instance will be destroyed after this
     * object has been destroyed
     * 
     * @param instance The instance to destroy
     */
    public void dispose(T instance);
    
    
}
