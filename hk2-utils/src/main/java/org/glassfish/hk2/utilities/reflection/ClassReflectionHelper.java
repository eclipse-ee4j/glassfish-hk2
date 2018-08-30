/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.utilities.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * An interface representing useful reflection utilities
 * 
 * @author jwells
 */
public interface ClassReflectionHelper {
    
    /**
     * Gets all methods for a class (taking class heirarchy into account)
     * 
     * @param clazz The class to analyze for all methods
     * @return The set of all methods on this class (and all subclasses)
     */
    public Set<MethodWrapper> getAllMethods(Class<?> clazz);
    
    /**
     * Creates a method wrapper from the given method
     * 
     * @param m A non-null method to create a wrapper from
     * @return A method wrapper
     */
    public MethodWrapper createMethodWrapper(Method m);
    
    /**
     * Gets all fields for a class (taking class heirarchy into account)
     * 
     * @param clazz The class to analyze for all fields
     * @return The set of all fields on this class (and all subclasses)
     */
    public Set<Field> getAllFields(Class<?> clazz);
    
    /**
     * Finds the postConstruct method on this class
     * 
     * @param clazz The class to check for the postConstruct method
     * @param matchingClass The PostConstruct interface, a small performance improvement
     * @return A matching method, or null if none can be found
     * @throws IllegalArgumentException If a method marked as postConstruct is invalid
     */
    public Method findPostConstruct(Class<?> clazz, Class<?> matchingClass) throws IllegalArgumentException;
    
    /**
     * Finds the preDestroy method on this class
     * 
     * @param clazz The class to check for the postConstruct method
     * @param matchingClass The PostConstruct interface, a small performance improvement
     * @return A matching method, or null if none can be found
     * @throws IllegalArgumentException If a method marked as postConstruct is invalid
     */
    public Method findPreDestroy(Class<?> clazz, Class<?> matchingClass) throws IllegalArgumentException;
    
    /**
     * Removes this class (and all appropriate sub-classes) from the cache
     * 
     * @param clazz The class to remove.  If null this method does nothing
     */
    public void clean(Class<?> clazz);
    
    /**
     * Releases the entire cache, though the ClassReflectionHelper is
     * still usable after calling dispose
     */
    public void dispose();
    
    /**
     * Returns an approximation of the current size of the cache
     * @return An approximation of the current size of the cache
     */
    public int size();

}
