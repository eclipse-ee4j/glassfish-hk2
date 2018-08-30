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

package org.glassfish.hk2.xml.test.naked;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Both children have no XmlElement annotation, and
 * so will be ghosted with XmlElement annotations
 * 
 * @author jwells
 *
 */
@XmlRootElement(name="parent")
public interface ParentBean {
    /**
     * This naked child has a getter and a setter and
     * is of form List
     * 
     * @return
     */
    public List<ChildOne> getOne();
    public void setOne(List<ChildOne> one);
    
    /**
     * This naked child has only a getter and
     * is of form List
     * 
     * @return
     */
    public List<ChildTwo> getTwo();
    
    /**
     * This naked child has only a getter and
     * is of form Array
     * 
     * @return
     */
    public ChildOne[] getThree();
    
    /**
     * This naked child has a getter and a setter and
     * is of form Array
     * 
     * @return
     */
    public ChildTwo[] getFour();
    public void setFour(ChildTwo[] two);
    
    /**
     * This naked child has only a getter and
     * is of form direct
     * 
     * @return
     */
    public ChildThree getFive();
    
    /**
     * This naked child has a getter and a setter and
     * is of form direct
     * 
     * @return
     */
    public ChildThree getSix();
    public void setSix(ChildThree six);
}
