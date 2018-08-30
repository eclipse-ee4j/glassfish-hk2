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

package org.glassfish.hk2.tests.locator.types;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.glassfish.hk2.api.ServiceLocator;

/**
 * @author jwells
 *
 */
public class AbstractListMapService<A, B, C> {
    @Inject
    private List<A> aList;
    
    @Inject
    private Map<B, C> aMap;
    
    private Map<B, C> iMap;
    private List<A> iList;
    private ServiceLocator locator;
    
    @Inject
    private void init(Map<B, C> iMap, ServiceLocator locator, List<A> iList) {
        this.iMap = iMap;
        this.locator = locator;
        this.iList = iList;
    }
    
    public List<A> getAList() {
        return aList;
    }
    
    public Map<B, C> getAMap() {
        return aMap;
    }
    
    public List<A> getIList() {
        return iList;
    }
    
    public Map<B, C> getIMap() {
        return iMap;
    }
    
    public ServiceLocator getLocator() {
        return locator;
    }

}
