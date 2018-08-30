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

package org.glassfish.hk2.configuration.internal;

/**
 * @author jwells
 *
 */
public class BeanInfo {
    private final String typeName;
    private final String instanceName;
    private final Object bean;
    private final Object metadata;
    
    /* package */ BeanInfo(String typeName, String instanceName, Object bean, Object metadata) {
        this.typeName = typeName;
        this.instanceName = instanceName;
        this.bean = bean;
        this.metadata = metadata;
    }

    /**
     * @return the typeName
     */
    public String getTypeName() {
        return typeName;
    }

    /**
     * @return the instanceName
     */
    public String getInstanceName() {
        return instanceName;
    }

    /**
     * @return the bean
     */
    public Object getBean() {
        return bean;
    }
    
    public Object getMetadata() {
        return metadata;
    }
    
    @Override
    public String toString() {
        return "BeanInfo(" + typeName + "," + instanceName + "," + bean + "," + metadata + "," + System.identityHashCode(this) + ")";
    }

}
