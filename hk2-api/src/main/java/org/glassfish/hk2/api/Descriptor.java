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

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A Descriptor is a bean-like structure that describes a service
 * declaration in HK2.  A descriptor is comprised only of basic
 * objects such as String or boolean (e.g., not Class or Annotation}.
 * A described service does not need to be classloaded in order to
 * have a Descriptor.  However, this does imply that a Descriptor will
 * have some loss of information when compared to a classloaded service,
 * which is described with an {@link ActiveDescriptor}.  For example, a
 * Descriptor can know that a service has a qualifier named Foo, but
 * will not know (without the use of {@link Metadata}) what values
 * Foo contains.
 * <p>
 * Services have a number of optional attributes such as name and scope.
 * A service is required to minimally have a type name representing the
 * concrete (i.e., byType) definition.
 * <p>
 * @see ActiveDescriptor Metadata
 * 
 * @author Jerome Dochez, Jeff Trent, Mason Taube
 */
public interface Descriptor {
    /**
     * Returns the fully qualified class
     * name of the implementation
     * class.  If this is a factory descriptor
     * then this will return the fully
     * qualified name of the class implementing
     * the factory interface.
     *
     * @return Might return null in some cases,
     * but will usually return the fully qualified
     * class name of the implementation class or
     * of the factory class for this descriptor
     */
    public String getImplementation();

	/**
	 * Returns the base class name of the contracts that
	 * this service describes.  If the contract is a
	 * paramterized type this will return the raw class.
	 * If this service can be looked up by its implementation
	 * class then the name of the implementation class
	 * must also be found in this list
	 *   
	 * @return Will never return null, but
	 * may return an empty set.  The returned
	 * strings are the fully qualified class
	 * names of contracts the predicate
	 * describes 
	 */
	public Set<String> getAdvertisedContracts();
	
	/**
	 * Returns the fully qualified class name of
	 * the scope annotation that should be
	 * associated with this descriptor.
	 * 
	 * @return If this returns null then this
	 * descriptor is assumed to be in the
	 * default scope, which is PerLookup
	 */
	public String getScope();
	
	/**
	 * The name of this descriptor.  Note that
	 * if this returns not null then there must
	 * also be the Named qualifier in the set of
	 * qualifiers with the same value
	 * <p>
	 * 
	 * @return The name of this descriptor, or null
	 * if there is no name associated with this qualifier
	 */
	public String getName();
	
	/**
	 * Returns all of the annotation classes
	 * that this object should be registered
	 * with or looked up by
	 * 
	 * @return Never returns null, but may return
	 * an empty set.  The set of fully
	 * qualified class names that are annotations
	 * that this object must have
	 */
	public Set<String> getQualifiers();
	
	/**
     * Returns CLASS if this is a class descriptor
     * and FACTORY if this is a descriptor describing
     * a factory for a type, in which case the
     * implClass should point to the implementation
     * class of the factory
     * 
     * @return Either CLASS or PROVIDE_METHOD
     */
    public DescriptorType getDescriptorType();
    
    /**
     * Returns the visibility of this descriptor.
     * <p>
     * If the visibility is NORMAL then this descriptor
     * may be seen by all children locators of the
     * locator in which this descriptor is bound
     * <p>
     * If the visibility is LOCAL then this descriptor
     * may only be seen by the servcie locator in which
     * it is bound, and in none of the children
     * of that locator
     * 
     * @return Either NORMAL or LOCAL
     */
    public DescriptorVisibility getDescriptorVisibility();
	
	/**
	 * Returns all of the metadata associated
	 * that this object should be registered
	 * with or looked up by
	 * 
	 * @return Never returns null, but may return
	 * an empty set.  The set of metadata
	 * associated with the object.  The values
	 * in the returned map will never be null, and
	 * will always have at least one member
	 */
	public Map<String, List<String>> getMetadata();
	
	/**
	 * This returns the loader that should be used when
	 * classloading this descriptor.  If this method
	 * returns null then the class will be loaded with the
	 * classloader of the Injectee if there is one, or
	 * with the classloader that loaded HK2 itself;  failing
	 * that the context class loader on the thread
	 * will be tried.
	 * 
	 * @return An HK2Loader that can be used to load
	 * this descriptor, or null if the default classloading
	 * algorithm should be used
	 */
	public HK2Loader getLoader();
	
	/**
	 * Returns the ranking of this descriptor.  Rankings
	 * with higher value will be considered "better" than
	 * rankings of lower value.  Descriptors with the same
	 * ranking will be returned in the reverse ServiceID order
	 * (in other words, the least service ID is considered
	 * "better" than any higher service ID).
	 * 
	 * @return the ranking that should be associated with this
	 * descriptor
	 */
	public int getRanking();
	
	/**
     * Returns the ranking of this descriptor.  Rankings
     * with higher value will be considered "better" than
     * rankings of lower value.  Descriptors with the same
     * ranking will be returned in the reverse ServiceID order
     * (in other words, the least service ID is considered
     * "better" than any higher service ID).
     * <p>
     * The ranking of a service may change at any time during
     * the life of the descriptor
     * 
     * @param ranking The new ranking this descriptor should have
     * @return the previous ranking that this descriptor had
     */
    public int setRanking(int ranking);
    
    /**
     * If this returns true then the system will create a proxy for
     * instances of this descriptor.  As with all proxies, the proxy
     * created will also implement the {@link ProxyCtl} interface
     * <p>
     * It is an error for this method to return true if the scope that
     * this descriptor is in is {@link Unproxiable} (such as PerLookup).
     * 
     * @return true if this descriptor must be proxied, false if this
     * descriptor must NOT be proxied (even if it is in an Unproxiable scope)
     * and null if this descriptor should take its proxiable status from
     * the scope it is in (i.e., it will only be proxied if the scope is
     * marked {@link Proxiable})
     */
    public Boolean isProxiable();
    
    /**
     * This value determines whether or not this service should be
     * proxied when being injected into other services of the same
     * scope.  If a scope is proxiable then it will have a default
     * setting for whether or not its services should be proxied
     * when being injected into the same scope.  If this method
     * returns null then the default setting for the scope will
     * be used.  Otherwise the value returned will determine
     * whether or not this service will be proxied when being
     * injected into a service of the same scope.
     * 
     * @return null if this descriptor should use the default
     * ProxyForSameScope value for the scope.  If it returns
     * true then this service will be proxied even when
     * being injected into the same scope.  If it returns
     * false then this service will NOT be proxied when
     * being injected into the same scope (i.e., it cannot
     * be used for lazy initialization of the service when
     * injected into the same scope)
     */
    public Boolean isProxyForSameScope();
    
    /**
     * Returns the name of the {@link ClassAnalyzer} service that
     * should be used to discover the constructors, initialization methods,
     * field and postConstruct and preDestory methods.  If null the default
     * implementation will be used.  Will be ignored for descriptors that
     * are not automatically analyzed by hk2
     * 
     * @return the possibly null name of the ClassAnalysis service that
     * should be used to analyze the class represented by this descriptor.
     * If null then the HK2 default analysis will be performed
     */
    public String getClassAnalysisName();
	
	/**
	 * This returns the unique identifier for this descriptor.
	 * This field will be ignored upon binding, and will then
	 * be assigned by the system.  However, this field can be
	 * set on search operations, in which case this search will
	 * match exactly one Provider in the system.
	 * 
	 * @return The service id for this object.  Note that this
	 * field may return null if this is an initial bind, as it
	 * is not the responsibility of the binder to set this
	 * value.  If this returns non-null on a bind operation the
	 * return value will be ignored
	 */
	public Long getServiceId();
	
	/**
     * This returns the id of the ServiceLocator which this descriptor
     * is registered with.  Returns null if this descriptor
     * is not yet registered with a ServiceLocator
     * 
     * @return The id of the ServiceLocator this Descriptor is registered
     * with, or null if this Descriptor is not registered with a ServiceLocator
     */
    public Long getLocatorId();
}
