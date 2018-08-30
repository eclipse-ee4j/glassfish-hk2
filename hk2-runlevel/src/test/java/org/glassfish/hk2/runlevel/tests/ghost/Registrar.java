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

package org.glassfish.hk2.runlevel.tests.ghost;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Singleton;

/**
 * @author jwells
 *
 */
@Singleton
public class Registrar {
    private final List<Object> registrees = new LinkedList<Object>();
    private final List<Object> downers = new LinkedList<Object>();
    
    public void clear() {
        registrees.clear();
        downers.clear();
    }
    
    public void register(Object o) {
        registrees.add(o);
    }
    
    public void goingDown(Object o) {
        downers.add(o);
    }
    
    public List<Object> get() {
        return registrees;
    }
    
    public List<Object> getDown() {
        return downers;
    }

}
