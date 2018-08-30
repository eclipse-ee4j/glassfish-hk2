/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import org.glassfish.hk2.utilities.reflection.ClassReflectionHelper;
import org.glassfish.hk2.xml.jaxb.internal.BaseHK2JAXBBean;

/**
 * Used by implementations of XmlService, though it may not actually
 * be JAXB that is calling the methods
 * 
 * @author jwells
 *
 */
public class Hk2JAXBUnmarshallerListener extends Unmarshaller.Listener {
    private final JAUtilities jaUtilities;
    private final ClassReflectionHelper classReflectionHelper;

    /**
     * @param xmlServiceImpl
     */
    Hk2JAXBUnmarshallerListener(JAUtilities jaUtilities, ClassReflectionHelper classReflectionHelper) {
        this.jaUtilities = jaUtilities;
        this.classReflectionHelper = classReflectionHelper;
    }

    private final LinkedList<BaseHK2JAXBBean> allBeans = new LinkedList<BaseHK2JAXBBean>();
    
    private void setUserKey(BaseHK2JAXBBean bean, boolean listOrArray) {
        ModelImpl model = bean._getModel();
        
        QName keyProperty = model.getKeyProperty();
        if (keyProperty == null && listOrArray) {
            bean._setKeyValue(jaUtilities.getUniqueId());
            
            return;
        }
        
        if (keyProperty == null) return;
        
        String key = (String) bean._getProperty(QNameUtilities.getNamespace(keyProperty), keyProperty.getLocalPart());
        if (key == null) return;
        
        bean._setKeyValue(key);
    }
    
    
    
    @SuppressWarnings("unchecked")
    private void setSelfXmlTagInAllChildren(BaseHK2JAXBBean targetBean, BaseHK2JAXBBean parent) {
        ModelImpl model = targetBean._getModel();
        
        for (Map.Entry<QName, ChildDescriptor> childDescriptorEntry : model.getAllChildrenDescriptors().entrySet()) {
            ParentedModel parentedNode = childDescriptorEntry.getValue().getParentedModel();
            
            if (parentedNode != null) {
                String childXmlTagNamespace = parentedNode.getChildXmlNamespace();
                String childXmlTag = parentedNode.getChildXmlTag();
                String xmlWrapperTag = parentedNode.getXmlWrapperTag();
                if (parentedNode.getAdapter() != null) {
                    continue;
                }
                
                Object children;
                switch (parentedNode.getAliasType()) {
                case NORMAL:
                    children = targetBean._getProperty(childXmlTagNamespace, childXmlTag);
                    break;
                case IS_ALIAS:
                    children = targetBean._getProperty(childXmlTagNamespace, childXmlTag);
                    targetBean.__fixAlias(childXmlTagNamespace, childXmlTag, parentedNode.getChildXmlAlias());
                    
                    break;
                case HAS_ALIASES:
                default:
                    children = null;
                }
                
                if (children == null) continue;
                
                String proxyName = Utilities.getProxyNameFromInterfaceName(parentedNode.getChildInterface());
                
                if (children instanceof List) {
                    for (Object child : (List<Object>) children) {
                        if (!child.getClass().getName().equals(proxyName)) {
                            continue;
                        }
                        
                        BaseHK2JAXBBean childBean = (BaseHK2JAXBBean) child;
                        
                        childBean._setSelfXmlTag(parentedNode.getChildXmlNamespace(), Utilities.constructXmlTag(xmlWrapperTag, parentedNode.getChildXmlTag()));
                        
                        setUserKey(childBean, true);
                    }
                    
                }
                else if (children.getClass().isArray()) {
                    for (Object child : (Object[]) children) {
                        BaseHK2JAXBBean childBean = (BaseHK2JAXBBean) child;
                        
                        childBean._setSelfXmlTag(parentedNode.getChildXmlNamespace(), Utilities.constructXmlTag(xmlWrapperTag, parentedNode.getChildXmlTag()));
                        
                        setUserKey(childBean, true);
                    }
                }
                else {
                    BaseHK2JAXBBean childBean = (BaseHK2JAXBBean) children;
                    
                    childBean._setSelfXmlTag(parentedNode.getChildXmlNamespace(), Utilities.constructXmlTag(xmlWrapperTag, parentedNode.getChildXmlTag()));
                    
                    setUserKey(childBean, false);
                }
                
            }
            else {
                QName nonChildProp = childDescriptorEntry.getKey();
                ChildDataModel cdm = childDescriptorEntry.getValue().getChildDataModel();
                
                if (AliasType.IS_ALIAS.equals(cdm.getAliasType())) {
                    targetBean.__fixAlias(QNameUtilities.getNamespace(nonChildProp), nonChildProp.getLocalPart(), cdm.getXmlAlias());
                }
            }
        }
    }
    
    @Override
    public void afterUnmarshal(Object target, Object parent) {
        if (!(target instanceof BaseHK2JAXBBean)) return;
        
        BaseHK2JAXBBean targetBean = (BaseHK2JAXBBean) target;
        BaseHK2JAXBBean parentBean = (BaseHK2JAXBBean) parent;
        ModelImpl targetNode = targetBean._getModel();
        
        allBeans.add(targetBean);
        
        if (parentBean == null) {
            QName rootName = targetNode.getRootName();
            
            targetBean._setSelfXmlTag(QNameUtilities.getNamespace(rootName), rootName.getLocalPart());
        }
        
        setSelfXmlTagInAllChildren(targetBean, parentBean);
    }
    
    @Override
    public void beforeUnmarshal(Object target, Object parent) {
        if (!(target instanceof BaseHK2JAXBBean)) return;
        if ((parent != null) && !(parent instanceof BaseHK2JAXBBean)) return;
        
        BaseHK2JAXBBean targetBean = (BaseHK2JAXBBean) target;
        BaseHK2JAXBBean parentBean = (BaseHK2JAXBBean) parent;
        
        targetBean._setClassReflectionHelper(classReflectionHelper);
        targetBean._setParent(parentBean);
    }
    
    LinkedList<BaseHK2JAXBBean> getAllBeans() {
        return allBeans;
    }
    
}
