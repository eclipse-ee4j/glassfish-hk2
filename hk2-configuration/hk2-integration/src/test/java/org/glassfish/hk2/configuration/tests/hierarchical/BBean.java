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
public class BBean extends NamedBean {
    private final LinkedList<DBean> beans = new LinkedList<DBean>();
    private final CBeans cbeans;
    
    /**
     * @param name
     */
    public BBean(String name, CBeans cbeans) {
        super(name);
        
        this.cbeans = cbeans;
    }
    
    public CBeans getCBeans() {
        return cbeans;
    }
    
    public synchronized List<DBean> getDBeans() {
        return new LinkedList<DBean>(beans);
    }
    
    public synchronized DBean addDBean(String name) {
        DBean dbean = new DBean(name);
        beans.add(dbean);
        return dbean;
    }
    
    public synchronized void removeDBean(String name) {
        Iterator<DBean> beanIterator = beans.iterator();
        while (beanIterator.hasNext()) {
            DBean cbean = beanIterator.next();
            if (name.equals(cbean.getName())) {
                beanIterator.remove();
            }
        }
    }

}
