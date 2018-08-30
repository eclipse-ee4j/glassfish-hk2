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

package org.glassfish.hk2.xml.internal;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.glassfish.hk2.xml.api.XmlService;
import org.glassfish.hk2.xml.jaxb.internal.NamespaceBeanLikeMap;

/**
 * @author jwells
 *
 */
public class NamespaceBeanLikeMapImpl implements NamespaceBeanLikeMap, Serializable {
    private static final long serialVersionUID = 7351909351649012181L;

    private Map<String, Map<String, Object>> namespaceMap =
            new LinkedHashMap<String, Map<String, Object>>();
    
    private Map<String, Map<String, Object>> backupMap;
    
    public NamespaceBeanLikeMapImpl() {
        namespaceMap.put(XmlService.DEFAULT_NAMESPACE, new LinkedHashMap<String, Object>());
    }
    
    private static Map<String, Map<String, Object>> deepCopyNamespaceBeanLikeMaps(Map<String, Map<String, Object>> copyMe) {
        Map<String, Map<String, Object>> retVal = new LinkedHashMap<String, Map<String, Object>>();
        if (copyMe == null) return retVal;
        
        for (Map.Entry<String, Map<String,Object>> entry : copyMe.entrySet()) {
            String namespace = entry.getKey();
            Map<String, Object> blm = entry.getValue();
            
            retVal.put(namespace, new LinkedHashMap<String, Object>(blm));
        }
        
        return retVal;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.jaxb.internal.NamespaceBeanLikeMap#getValue(java.lang.String, java.lang.String)
     */
    @Override
    public Object getValue(String namespace, String key) {
        namespace = QNameUtilities.fixNamespace(namespace);
        
        Map<String, Object> nMap = namespaceMap.get(namespace);
        if (nMap == null) return null;
        
        return nMap.get(key);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.jaxb.internal.NamespaceBeanLikeMap#setValue(java.lang.String, java.lang.String, java.lang.Object)
     */
    @Override
    public void setValue(String namespace, String key, Object value) {
        namespace = QNameUtilities.fixNamespace(namespace);
        
        Map<String, Object> narrowedMap = namespaceMap.get(namespace);
        if (narrowedMap == null) {
            narrowedMap = new LinkedHashMap<String, Object>();
            namespaceMap.put(namespace, narrowedMap);
        }
        
        narrowedMap.put(key, value);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.jaxb.internal.NamespaceBeanLikeMap#isSet(java.lang.String, java.lang.String)
     */
    @Override
    public boolean isSet(String namespace, String key) {
        namespace = QNameUtilities.fixNamespace(namespace);
        
        Map<String, Object> narrowedMap = namespaceMap.get(namespace);
        if (narrowedMap == null) return false;
        
        return narrowedMap.containsKey(key);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.jaxb.internal.NamespaceBeanLikeMap#backup()
     */
    @Override
    public void backup() {
        if (backupMap != null) return;
        
        backupMap = deepCopyNamespaceBeanLikeMaps(namespaceMap);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.jaxb.internal.NamespaceBeanLikeMap#restoreBackup(boolean)
     */
    @Override
    public void restoreBackup(boolean drop) {
        try {
            if (backupMap == null) {
                return;
            }
            
            if (drop) {
                return;
            }
            
            namespaceMap = backupMap;
        }
        finally {
            backupMap = null;
        }
    }
    
    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.jaxb.internal.NamespaceBeanLikeMap#getBeanLikeMap(java.util.Map)
     */
    @Override
    public Map<String, Object> getBeanLikeMap(
            Map<String, String> namespaceToPrefixMap) {
        LinkedHashMap<String, Object> retVal = new LinkedHashMap<String, Object>();
        
        for (Map.Entry<String, Map<String, Object>> outerEntry : namespaceMap.entrySet()) {
            String namespace = outerEntry.getKey();
            Map<String, Object> blm = outerEntry.getValue();
            
            boolean addNamespace = !XmlService.DEFAULT_NAMESPACE.equals(namespace);
            String prefix = addNamespace ? namespaceToPrefixMap.get(namespace) : null ;
            
            if (addNamespace && prefix == null) {
                // could not find the namespace prefix to use
                continue;
            }
            
            for (Map.Entry<String, Object> innerEntry : blm.entrySet()) {
                String key;
                Object value = innerEntry.getValue();
            
                if (addNamespace) {
                    key = prefix + ":" + innerEntry.getKey();
                }
                else {
                    key = innerEntry.getKey();
                }
                
                retVal.put(key, value);
            }
        }
        
        return retVal;
    }
    
    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.jaxb.internal.NamespaceBeanLikeMap#shallowCopy(org.glassfish.hk2.xml.jaxb.internal.NamespaceBeanLikeMap, org.glassfish.hk2.xml.internal.ModelImpl)
     */
    @Override
    public void shallowCopy(NamespaceBeanLikeMap copyFrom, ModelImpl copyModel, boolean copyReferences) {
        for (Map.Entry<String, Map<String, Object>> outerEntry : copyFrom.getNamespaceBeanLikeMap().entrySet()) {
            String copyNamespace = outerEntry.getKey();
            Map<String, Object> copyBeanLikeMap = outerEntry.getValue();
            
            for (Map.Entry<String, Object> entrySet : copyBeanLikeMap.entrySet()) {
                String xmlTag = entrySet.getKey();
                
                QName childQName = QNameUtilities.createQName(copyNamespace, xmlTag);
                
                if (copyModel.getKeyedChildren().contains(childQName) || copyModel.getUnKeyedChildren().contains(childQName)) {
                    continue;
                }
                
                ChildDataModel cdm = copyModel.getNonChildProperties().get(childQName);
                if (!copyReferences && cdm != null && cdm.isReference()) {
                    continue;
                }
                
                setValue(copyNamespace, xmlTag, entrySet.getValue());
            }
        }
    }
    
    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.jaxb.internal.NamespaceBeanLikeMap#getNamespaceBeanLikeMap()
     */
    @Override
    public Map<String, Map<String, Object>> getNamespaceBeanLikeMap() {
        return namespaceMap;
    }
    
    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.jaxb.internal.NamespaceBeanLikeMap#getQNameMap()
     */
    @Override
    public Map<QName, Object> getQNameMap() {
        Map<QName, Object> retVal = new LinkedHashMap<QName, Object>();
        
        for (Map.Entry<String, Map<String, Object>> outerEntry : namespaceMap.entrySet()) {
            String namespace = outerEntry.getKey();
            Map<String, Object> innerMap = outerEntry.getValue();
            
            for (Map.Entry<String, Object> innerEntry : innerMap.entrySet()) {
                QName key = QNameUtilities.createQName(namespace, innerEntry.getKey());
                Object value = innerEntry.getValue();
                
                retVal.put(key, value);
            }
            
        }
        
        return retVal;
    }
    
    @Override
    public String toString() {
        return "NamespaceBeanLikeMapImpl(" + System.identityHashCode(this) + ")";
    }



    

    
}
