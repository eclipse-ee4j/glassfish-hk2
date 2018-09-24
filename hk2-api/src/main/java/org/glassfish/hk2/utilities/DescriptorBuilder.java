/*
 * Copyright (c) 2007, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.utilities;

import java.lang.annotation.Annotation;
import java.util.List;

import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.FactoryDescriptors;
import org.glassfish.hk2.api.HK2Loader;

/**
 * This is an object that uses the Builder pattern
 * in order to generate a Descriptor (for binding) or
 * a Filter (for searching).  It is intended
 * to facilitate query strings of method calls
 * that is simple to understand and read.
 *
 */
public interface DescriptorBuilder {
	
	/**
	 * The name for this descriptor object.  Note that at the current time a descriptor
	 * can only have one name, hence this method will throw an IllegalArgumentException
	 * if named is called more than once.
	 * 
	 * @param name The name to be associated with this Descriptor
	 * @return A DescriptorBuilder with the given name
	 * @throws IllegalArgumentException if there is more than one name on the predicate
	 */
	public DescriptorBuilder named(String name) throws IllegalArgumentException;
	
	/**
	 * A contract to be associated with this descriptor object.
	 * 
	 * @param contract A class that is annotated with Contract to
	 *   be associated with this Descriptor
	 * @return A DescriptorBuilder with the given name
	 * @throws IllegalArgumentException on failures
	 */
	public DescriptorBuilder to(Class<?> contract) throws IllegalArgumentException;
	
	/**
	 * A contract to be associated with this descriptor object.
	 * 
	 * @param contract The fully qualified name of a class that is annotated with Contract.
	 * @return A DescriptorBuilder with the given name
	 * @throws IllegalArgumentException on failures
	 */
	public DescriptorBuilder to(String contract) throws IllegalArgumentException;
	
	/**
	 * A scope to be associated with this descriptor object. Note that at the current time a
	 * descriptor can only have one scope, hence this method will throw an IllegalArgumentException
	 * if in is called more than once.
	 * 
	 * @param scope The class of the scope this descriptor is to have.
	 * @return A DescriptorBuilder with the given scope
	 * @throws IllegalArgumentException If in is called more than once
	 */
	public DescriptorBuilder in(Class<? extends Annotation> scope) throws IllegalArgumentException;
	
   /**
    * A scope to be associated with this descriptor object. Note that at the current time a
    * descriptor can only have one scope, hence this method will throw an IllegalArgumentException
    * if in is called more than once.
    * 
    * @param scope The fully qualified class name of the scope this predicate is to have.
    * @return A DescriptorBuilder with the given scope
    * @throws IllegalArgumentException If in is called more than once
    */
  public DescriptorBuilder in(String scope) throws IllegalArgumentException;
	
	/**
	 * A qualifier to be associated with this descriptor object.  It is
	 * dangerous to use this method with a ghost annotation (one that is
	 * not on the object) if that annotation has fields, as these
	 * fields will NOT survive being put into the {@link org.glassfish.hk2.api.Descriptor}.
	 * Use this method with care and ONLY with annotations that do
	 * not have fields.  A safe version of this method for use
	 * with ghost annotations is 
	 * {@link ActiveDescriptorBuilder#qualifiedBy(Annotation)}
	 * 
	 * @param annotation The annotation to be associated with this descriptor
	 * @return A DescriptorBuilder with the given annotation
	 * @throws IllegalArgumentException on failures
	 */
	public DescriptorBuilder qualifiedBy(Annotation annotation) throws IllegalArgumentException;
	
	/**
	 * A qualifier to be associated with this descriptor object
	 * 
	 * @param annotation The fully qualified class name of an annotation to be
	 * associated with this descriptor
	 * @return A DescriptorBuilder with the given annotation
	 * @throws IllegalArgumentException on failures
	 */
	public DescriptorBuilder qualifiedBy(String annotation) throws IllegalArgumentException;
	
	/**
	 * An instance of data to be associated with this descriptor
	 * 
	 * @param key The key for the data to be associated with this descriptor
	 * @param value The value this key should take (single value metadata)
	 * @return A DescriptorBuilder with the given metadata
	 * @throws IllegalArgumentException
	 */
	public DescriptorBuilder has(String key, String value) throws IllegalArgumentException;
	
	/**
	 * An instance of data to be associated with this descriptor
	 * 
	 * @param key The key for the data to be associated with this descriptor
	 * @param values The values this key should take (single value metadata)
	 * @return A DescriptorBuilder with the given metadata
	 * @throws IllegalArgumentException
	 */
	public DescriptorBuilder has(String key, List<String> values) throws IllegalArgumentException;
	
	/**
	 * The rank to be associated with this descriptor.  The last rank
	 * bound wins
	 * 
	 * @param rank The rank to be associated with this descriptor.
	 * @return A DescriptorBuilder with the given rank
	 */
	public DescriptorBuilder ofRank(int rank);
	
	/**
	 * This will cause the isProxiable field of the returned
	 * descriptor to return true (it will force this
	 * descriptor to use proxies).
	 * 
	 * @return A DescriptorBuilder with the proxiable field set to true
	 */
	public DescriptorBuilder proxy();
	
	/**
     * This will cause the isProxiable field of the returned
     * descriptor to return the given value.
     * 
     * @param forceProxy if true then this descriptor will be proxied,
     * if false then this descriptor will NOT be proxied
     * @return A DescriptorBuilder with the proxiable field set to
     * the given value
     */
	public DescriptorBuilder proxy(boolean forceProxy);
	
	/**
     * This will cause the isProxyForSameScope field of the returned
     * descriptor to return true (it will force this
     * descriptor to proxy even when injecting into the same scope).
     * 
     * @return A DescriptorBuilder with the proxyForSameScope field set to true
     */
    public DescriptorBuilder proxyForSameScope();
    
    /**
     * This will cause the isProxyForSameScope field of the returned
     * descriptor to return the given value.
     * 
     * @param proxyForSameScope if true then this descriptor will be proxied
     * even when being injected into the same scope,
     * if false then this descriptor will NOT be proxied when injected
     * into a service of the same scope
     * @return A DescriptorBuilder with the proxyForSameScope field set to
     * the given value
     */
    public DescriptorBuilder proxyForSameScope(boolean proxyForSameScope);
	
	/**
     * This will cause the descriptorVisibility field of the returned
     * descriptor to return LOCAL
     * 
     * @return A DescriptorBuilder with the descriptorVisibility
     * field to be set to LOCAL
     */
    public DescriptorBuilder localOnly();
    
    /**
     * This will set the descriptorVisibility field of the returned
     * descriptor
     * 
     * @param visibility The non-null visibility that this descriptor should take
     * @return A DescriptorBuilder with the descriptorVisibility field
     * set to the input value
     */
    public DescriptorBuilder visibility(DescriptorVisibility visibility);
    
    /**
     * Call this if this descriptor should be loaded with the given HK2Loader
     * 
     * @param loader The loader to use with this descriptor
     * @return A DescriptorBuilder with the given HK2Loader
     * @throws IllegalArgumentException if the HK2Loader is set non-null more than once
     */
    public DescriptorBuilder andLoadWith(HK2Loader loader) throws IllegalArgumentException;
    
    /**
     * Call this if the descriptor should be analyzed with the
     * {@link org.glassfish.hk2.api.ClassAnalyzer} service of the given name
     * 
     * @param serviceName the name of the {@link org.glassfish.hk2.api.ClassAnalyzer} service
     * that should be used to analyze this service
     * @return A DescriptorBuilder with the given analysis service
     */
    public DescriptorBuilder analyzeWith(String serviceName);
	
	/**
	 * Generates a descriptor that can be used in binding operations
	 * 
	 * @return The descriptor that has been built up with this DescriptorBuilder
	 * @throws IllegalArgumentException if the built descriptor is invalid
	 */
	public DescriptorImpl build() throws IllegalArgumentException;
	
	/**
     * Generates a factory descriptor that can be used in binding operations.
     * The generated factory service will have no name, no qualifiers and the
     * same metadata as given to this builder.  The factory will be put into
     * PerLookup scope
     * 
     * @return The descriptor that has been built up with this DescriptorBuilder
     * @throws IllegalArgumentException if the built descriptor is invalid
     */
    public FactoryDescriptors buildFactory() throws IllegalArgumentException;
	
	/**
     * Generates a factory descriptor that can be used in binding operations.
     * The generated factory service will have no name, no qualifiers and the
     * same metadata as given to this builder.  The generated service will
     * have had the implementation class removed from its set of advertised
     * contracts
     * 
	 * @param factoryScope The scope the factory service itself is in.
     * @return The descriptor that has been built up with this DescriptorBuilder
     * @throws IllegalArgumentException if the built descriptor is invalid
     */
    public FactoryDescriptors buildFactory(String factoryScope) throws IllegalArgumentException;
    
    /**
     * Generates a factory descriptor that can be used in binding operations.
     * The generated factory service will have no name, no qualifiers and the
     * same metadata as given to this builder.  The generated service will
     * have had the implementation class removed from its set of advertised
     * contracts
     * 
     * @param factoryScope The scope the factory service itself is in.  If this is null the
     * PerLookup scope will be used
     * @return The descriptor that has been built up with this DescriptorBuilder
     * @throws IllegalArgumentException if the built descriptor is invalid
     */
    public FactoryDescriptors buildFactory(Class<? extends Annotation> factoryScope) throws IllegalArgumentException;

}
