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

package org.glassfish.hk2.xml.api;

import java.util.Map;

import javax.xml.namespace.QName;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.xml.internal.ModelImpl;
import org.jvnet.hk2.annotations.Contract;

/**
 * @author jwells
 *
 */
@Contract
public interface XmlHk2ConfigurationBean {
    /**
     * Returns a read-only copy of the
     * bean-like map corresponding to the current
     * state of this bean in the default namespace
     * of the document
     * 
     * @return A copy of the bean-like map associated
     * with this bean
     */
    public Map<String, Object> _getBeanLikeMap();
    
    /**
     * Returns the parent of this bean, or null if this
     * object is the root of the true
     * 
     * @return The parent of this object, or null if this
     * is the root of the tree
     */
    public XmlHk2ConfigurationBean _getParent();
    
    /**
     * Returns the XmlPath for this object
     * 
     * @return The XmlPath for this object
     */
    public String _getXmlPath();
    
    /**
     * Returns the instance path/name for this object
     * 
     * @return The instance path/name for this object
     */
    public String _getInstanceName();
    
    /**
     * Returns the name of the property that
     * returns the key for this bean, or
     * null if this bean does not have a key
     * property
     * 
     * @return The name of the key property for
     * this bean or null if this bean does
     * not have a key property
     */
    public QName _getKeyPropertyName();
    
    /**
     * Returns the key value for this object
     * 
     * @return The instance path/name for this object
     */
    public String _getKeyValue();
    
    /**
     * Gets the model for the given bean
     * 
     * @return The model for the bean
     */
    public ModelImpl _getModel();
    
    /**
     * Gets the descriptor with which this service was created.  May be
     * null if this service is not advertised in a ServiceLocator
     * 
     * @return The descriptor with which this service was created or null
     * if this service is not advertised in a ServiceLocator
     */
    public ActiveDescriptor<?> _getSelfDescriptor();
    
    /**
     * Looks up the child with the given propertyName that has the
     * given key value.  Will only search the default namespace
     * 
     * @param propName The non-null property name to look for
     * @param keyValue The non-null keyValue to look for
     * @return The child or null if not found
     */
    public Object _lookupChild(String propName, String keyValue);
    
    /**
     * Looks up the child with the given propertyName that has the
     * given key value
     * 
     * @param propNamespace The namespace to find the property in.
     * If null then the default namespace
     * @param propName The non-null property name to look for
     * @param keyValue The non-null keyValue to look for
     * @return The child or null if not found
     */
    public Object _lookupChild(String propNamespace, String propName, String keyValue);
    
    /**
     * Returns true if the given property is explicitly set, false
     * if the property has not been explicitly set.  Will only
     * check the default namespace
     *
     * @param propName The name of the property to check for being set,
     * may not be null
     * @return true if the property is explicitly set, false if
     * the property is not explicitly set
     */
    public boolean _isSet(String propName);
    
    /**
     * Returns true if the given property is explicitly set, false
     * if the property has not been explicitly set
     * 
     * @param propNamespace The namespace to find the property in.
     * If null then the default namespace
     * @param propName The name of the property to check for being set,
     * may not be null
     * @return true if the property is explicitly set, false if
     * the property is not explicitly set
     */
    public boolean _isSet(String propNamespace, String propName);
    
    /**
     * Gets the property with the given name from the
     * default name space.  Defaulting will happen
     * 
     * @param propName The property with the given name
     * @return The value of this property or the default
     * value if not set
     */
    public Object _getProperty(String propName);
    
    /**
     * Gets the property with the given name.
     * Defaulting will happen
     * 
     * @param propNamespace the non-null name space name
     * @param propName The property with the given name
     * @return The value of this property or the default
     * value if not set
     */
    public Object _getProperty(String propNamespace, String propName);
    
    
    /**
     * Will set the corresponding property of this bean to the
     * given value in the default name space.  Care should be
     * taken that the propName is one of the properties of
     * this bean
     * 
     * @param propName The non-null name of a property on this bean to set
     * @param propValue The possibly null value the property should take.  May
     * not be null if this property represents some scalar value
     */
    public void _setProperty(String propName, Object propValue);
    
    /**
     * Will set the corresponding property of this bean to the
     * given value.  Care should be
     * taken that the propName is one of the properties of
     * this bean
     * 
     * @param propNamespace The non-null name space of this property
     * @param propName The non-null name of a property on this bean to set
     * @param propValue The possibly null value the property should take.  May
     * not be null if this property represents some scalar value
     */
    public void _setProperty(String propNamespace, String propName, Object propValue);
    
    
    /**
     * Gets the root associated with this bean.  If this bean
     * has no associated root this will return null
     * 
     * @return The root of this bean, or null if this bean
     * is not associated with a root
     */
    public XmlRootHandle<?> _getRoot();
}
