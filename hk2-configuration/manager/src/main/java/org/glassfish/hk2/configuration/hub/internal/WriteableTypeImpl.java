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

package org.glassfish.hk2.configuration.hub.internal;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.glassfish.hk2.configuration.hub.api.Change;
import org.glassfish.hk2.configuration.hub.api.Instance;
import org.glassfish.hk2.configuration.hub.api.WriteableType;
import org.glassfish.hk2.utilities.reflection.BeanReflectionHelper;
import org.glassfish.hk2.utilities.reflection.ClassReflectionHelper;
import org.glassfish.hk2.utilities.reflection.internal.ClassReflectionHelperImpl;

/**
 * @author jwells
 *
 */
public class WriteableTypeImpl implements WriteableType {
    private final WriteableBeanDatabaseImpl parent;
    private final String name;
    private final HashMap<String, Instance> beanMap = new HashMap<String, Instance>();
    private final ClassReflectionHelper helper;
    private Object metadata;
    
    /* package */ WriteableTypeImpl(WriteableBeanDatabaseImpl parent, TypeImpl mother) {
        this.parent = parent;
        this.name = mother.getName();
        this.metadata = mother.getMetadata();
        beanMap.putAll(mother.getInstances());
        helper = mother.getHelper();
    }
    
    /* package */ WriteableTypeImpl(WriteableBeanDatabaseImpl parent, String name) {
        this.parent = parent;
        this.name = name;
        helper = new ClassReflectionHelperImpl();
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.Type#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.Type#getInstances()
     */
    @Override
    public synchronized Map<String, Instance> getInstances() {
        return Collections.unmodifiableMap(beanMap);
    }
    
    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.Type#getInstance(java.lang.Object)
     */
    @Override
    public synchronized Instance getInstance(String key) {
        return beanMap.get(key);
    }
    
    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.WriteableType#addInstance(java.lang.Object, java.lang.Object)
     */
    @Override
    public synchronized Instance addInstance(String key, Object bean) {
        return addInstance(key, bean, null);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.WriteableType#addInstance(java.lang.Object, java.lang.Object)
     */
    @Override
    public synchronized Instance addInstance(String key, Object bean, Object metadata) {
        if (key == null || bean == null) throw new IllegalArgumentException();
        
        InstanceImpl ii = new InstanceImpl(bean, metadata);
        
        parent.addChange(new ChangeImpl(Change.ChangeCategory.ADD_INSTANCE,
                                   this,
                                   key,
                                   ii,
                                   null,
                                   null));
        
        beanMap.put(key, ii);
        
        return ii;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.WriteableType#removeInstance(java.lang.Object)
     */
    @Override
    public synchronized Instance removeInstance(String key) {
        if (key == null) throw new IllegalArgumentException();
        
        Instance removedValue = beanMap.remove(key);
        if (removedValue == null) return null;
        
        parent.addChange(new ChangeImpl(Change.ChangeCategory.REMOVE_INSTANCE,
                this,
                key,
                removedValue,
                null,
                null));
        
        return removedValue;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.WriteableType#modifyInstance(java.lang.Object, java.lang.Object, java.beans.PropertyChangeEvent[])
     */
    @Override
    public synchronized PropertyChangeEvent[] modifyInstance(String key, Object newBean,
            PropertyChangeEvent... propChanges) {
        if (key == null || newBean == null) throw new IllegalArgumentException();
        
        Instance oldInstance = beanMap.get(key);
        if (oldInstance == null) {
            throw new IllegalStateException("Attempting to modify bean with key " + key + " but no such bean exists");
        }
        
        InstanceImpl newInstance = new InstanceImpl(newBean, oldInstance.getMetadata());
        
        if (propChanges.length == 0) {
            propChanges = BeanReflectionHelper.getChangeEvents(helper, oldInstance.getBean(), newInstance.getBean());
        }
        
        beanMap.put(key, newInstance);

        ArrayList<PropertyChangeEvent> propChangesList = new ArrayList<PropertyChangeEvent>(propChanges.length);
        for (PropertyChangeEvent pce : propChanges) {
            propChangesList.add(pce);
        }
        
        parent.addChange(new ChangeImpl(Change.ChangeCategory.MODIFY_INSTANCE,
                this,
                key,
                newInstance,
                oldInstance,
                propChangesList));
        
        return propChanges;
    }

    ClassReflectionHelper getHelper() {
        return helper;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.Type#getMetadata()
     */
    @Override
    public synchronized Object getMetadata() {
        return metadata;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.Type#setMetadata(java.lang.Object)
     */
    @Override
    public synchronized void setMetadata(Object metadata) {
        this.metadata = metadata;
    }
    
    @Override
    public String toString() {
        return "WriteableTypeImpl(" + name + "," + metadata + "," + System.identityHashCode(this) + ")";
    }

}
