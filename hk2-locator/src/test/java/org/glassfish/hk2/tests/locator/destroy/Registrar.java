/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.tests.locator.destroy;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Singleton;

/**
 * @author jwells
 *
 */
@Singleton
public class Registrar {
    private final LinkedList<Object> births = new LinkedList<Object>();
    private final LinkedList<Object> deaths = new LinkedList<Object>();
    
    public void addBirth(Object born) {
        births.add(born);
        
    }
    
    public void addDeath(Object death) {
        deaths.add(death);
        
    }
    
    public List<Object> getBirths() {
        return births;
    }
    
    public List<Object> getDeaths() {
        return deaths;
    }
    
    public void clear() {
        births.clear();
        deaths.clear();
    }

}
