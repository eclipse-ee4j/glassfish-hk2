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

package org.glassfish.hk2.xml.jaxb.internal;

import java.util.Map;

import javax.xml.namespace.QName;

import org.glassfish.hk2.xml.internal.ModelImpl;

/**
 * @author jwells
 *
 */
public interface NamespaceBeanLikeMap {
    /**
     * Gets the value for key based on the namespace.
     * Locking must be done by the caller
     * 
     * @param namespace if null or the empty string the
     * default namespace will be used
     * @param key the non-null key
     * @return The value if found (may be null) or
     * null if not set (use {@link #isSet(String, String)}
     * to determine if a value has been set
     */
    public Object getValue(String namespace, String key);
    
    /**
     * Sets the value for key based on the namespace.
     * Locking must be done by the caller
     * 
     * @param namespace if null or the empty string the
     * default namespace will be used
     * @param key the non-null key
     * @param value The value to set this key to (may be null)
     */
    public void setValue(String namespace, String key, Object value);
    
    /**
     * Determines if the value for key based on namespace
     * has been explicitly set.  Locking must be done
     * by the caller
     * 
     * @param nanmespace if null or the empty string the
     * default namespace will be used
     * @param key the non-null key
     * @return true if the value is explicitly set, false
     * otherwise
     */
    public boolean isSet(String nanmespace, String key);
    
    /**
     * Create a backup of all namespaces at this point.
     * Locking must be done by the caller
     */
    public void backup();
    
    /**
     * Either drop the backup data or restore the
     * current data to the backup. Locking must
     * be done by the caller
     * 
     * @param drop if true the backup should be
     * dropped, if false the current data should
     * be made the same as the backup
     */
    public void restoreBackup(boolean drop);
    
    /**
     * Gets the bean-like map given the set
     * of prefixes to use for the various
     * namespaces.  Note that if a namespace is
     * not found in the map the values from that
     * namespace will not be included in the 
     * returned map.  Locking must be handled
     * by the caller
     * 
     * @param namespaceToPrefixMap A map from namespace
     * to the prefix that should be put on the keys for
     * the namespace
     * @return A map with the fully qualified bean-like names
     */
    public Map<String, Object> getBeanLikeMap(Map<String, String> namespaceToPrefixMap);
    
    /**
     * Gets a map from QName to value.  Default namespace
     * is not taken into account
     * 
     * @return A non-null map from QName to value
     */
    public Map<QName, Object> getQNameMap();
    
    /**
     * Does a shallow copy from another namespace bean-like map to this one
     * 
     * @param copyFrom The other namesapce bean-like map to copy
     * @param model The model to use to determine what fields to copy
     * @param copyReferences true if references should also be copied
     */
    public void shallowCopy(NamespaceBeanLikeMap copyFrom, ModelImpl model, boolean copyReferences);
    
    /**
     * Gets a raw copy of the bean-like map.  The outer map has
     * namespace keys (including one for default) and the inner
     * map is the bean-like map for that namespace.  The map returned
     * is not a copy, so any changes to it will affect the underlying
     * object (so don't change it)
     * 
     * @return
     */
    public Map<String, Map<String, Object>> getNamespaceBeanLikeMap();
}
