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

package org.glassfish.hk2.configuration.tests.hierarchical;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author jwells
 *
 */
public class CBeans {
   private final LinkedList<CBean> beans = new LinkedList<CBean>();
    
   public synchronized List<CBean> getCBeans() {
       return new LinkedList<CBean>(beans);
   }
    
   public synchronized CBean addCBean(String name) {
       CBean cbean = new CBean(name);
       beans.add(cbean);
       return cbean;
   }
    
   public synchronized void removeCBean(String name) {
       Iterator<CBean> beanIterator = beans.iterator();
       while (beanIterator.hasNext()) {
           CBean cbean = beanIterator.next();
           if (name.equals(cbean.getName())) {
               beanIterator.remove();
           }
       }
   }

}
