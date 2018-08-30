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

import java.io.Serializable;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.glassfish.hk2.utilities.general.GeneralUtilities;

/**
 * This contains the model for children who have a specific
 * parent, containing information such as the xml tag
 * name and type.  These are all strings or other simple
 * types so it can be hard-coded into the proxy at build
 * time
 * 
 * @author jwells
 *
 */
public class ParentedModel implements Serializable {
    private static final long serialVersionUID = -2480798409414987937L;
    
    private final Object lock = new Object();
    
    /** The interface of the child for which this is a parent */
    private String childInterface;
    private String childXmlNamespace;
    private String childXmlTag;
    private String childXmlWrapperTag;
    private String childXmlAlias;
    private ChildType childType;
    private String givenDefault;
    private AliasType aliased;
    private String adapterClassName;
    private boolean required;
    private String originalMethodName;
    
    /** Set at runtime */
    private ClassLoader myLoader;
    private transient JAUtilities jaUtilities;
    
    /** Calculated lazily */
    private ModelImpl childModel;
    
    public ParentedModel() {
    }
    
    public ParentedModel(String childInterface,
            String childXmlNamespace,
            String childXmlTag,
            String childXmlAlias,
            ChildType childType,
            String givenDefault,
            AliasType aliased,
            String childXmlWrapperTag,
            String adapterClassName,
            boolean required,
            String originalMethodName) {
        this.childInterface = childInterface;
        this.childXmlNamespace = childXmlNamespace;
        this.childXmlTag = childXmlTag;
        this.childXmlAlias = childXmlAlias;
        this.childType = childType;
        this.givenDefault = givenDefault;
        this.aliased = aliased;
        this.childXmlWrapperTag = childXmlWrapperTag;
        this.adapterClassName = adapterClassName;
        this.required = required;
        this.originalMethodName = originalMethodName;
    }
    
    public String getChildInterface() {
        return childInterface;
    }
    
    public String getChildXmlNamespace() {
        return childXmlNamespace;
    }
    
    public String getChildXmlTag() {
        return childXmlTag;
    }
    
    public String getChildXmlAlias() {
        return childXmlAlias;
    }
    
    public ChildType getChildType() {
        return childType;
    }
    
    public String getGivenDefault() {
        return givenDefault;
    }
    
    public String getXmlWrapperTag() {
        return childXmlWrapperTag;
    }
    
    public String getAdapter() {
        return adapterClassName;
    }
    
    public boolean isRequired() {
        return required;
    }
    
    public String getOriginalMethodName() {
        return originalMethodName;
    }
    
    @SuppressWarnings("unchecked")
    public XmlAdapter<?, ?> getAdapterObject() {
        synchronized (lock) {
            if (myLoader == null) {
                throw new IllegalStateException("Cannot call getChildModel before the classloader has been determined");
            }
            
            if (adapterClassName == null) return null;
            
            Class<? extends XmlAdapter<?,?>> adapterClass = (Class<? extends XmlAdapter<?,?>>) GeneralUtilities.loadClass(myLoader, adapterClassName);
            if (adapterClass == null) {
                throw new IllegalStateException("Adapter " + adapterClass + " could not be loaded by " + myLoader);
            }
            
            try {
              XmlAdapter<?,?> xa = adapterClass.newInstance();
              
              return xa;
            }
            catch (RuntimeException re) {
                throw re;
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        
    }
    
    public ModelImpl getChildModel() {
        synchronized (lock) {
            if (myLoader == null) {
                throw new IllegalStateException("Cannot call getChildModel before the classloader has been determined");
            }
            
            if (childModel != null) return childModel;
            
            Class<?> beanClass = GeneralUtilities.loadClass(myLoader, childInterface);
            if (beanClass == null) {
                throw new IllegalStateException("Interface " + childInterface + " could not be loaded by " + myLoader);
            }
            
            try {
                childModel = jaUtilities.getModel(beanClass);
            }
            catch (RuntimeException re) {
                throw new RuntimeException("Could not get model for " + beanClass.getName() + " in " + this, re);
            }
            
            return childModel;
        }
    }
    
    public void setRuntimeInformation(JAUtilities jaUtilities, ClassLoader myLoader) {
        synchronized (lock) {
            this.jaUtilities = jaUtilities;
            this.myLoader = myLoader;
        }
    }
    
    public AliasType getAliasType() {
        return aliased;
    }
    
    @Override
    public String toString() {
        return "ParentedModel(interface=" + childInterface +
                ",xmlNamespace=" + childXmlNamespace +
                ",xmlTag=" + childXmlTag +
                ",xmlAlias=" + childXmlAlias +
                ",xmlWrapperTag=" + childXmlWrapperTag +
                ",type=" + childType +
                ",givenDefault=" + Utilities.safeString(givenDefault) +
                ",aliased=" + aliased +
                ",adapter=" + adapterClassName +
                ",originalMethodName=" + originalMethodName +
                ")";
    }
}
