/*
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.xml.test.dynamic.rawsets;

import java.util.List;

import javax.inject.Singleton;

import org.glassfish.hk2.configuration.hub.api.BeanDatabase;
import org.glassfish.hk2.configuration.hub.api.BeanDatabaseUpdateListener;
import org.glassfish.hk2.configuration.hub.api.Change;

@Singleton
public class UpdateListener implements BeanDatabaseUpdateListener {
    private List<Change> changes;

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.BeanDatabaseUpdateListener#prepareDatabaseChange(org.glassfish.hk2.configuration.hub.api.BeanDatabase, org.glassfish.hk2.configuration.hub.api.BeanDatabase, java.lang.Object, java.util.List)
     */
    @Override
    public void prepareDatabaseChange(BeanDatabase currentDatabase,
            BeanDatabase proposedDatabase, Object commitMessage,
            List<Change> changes) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.BeanDatabaseUpdateListener#commitDatabaseChange(org.glassfish.hk2.configuration.hub.api.BeanDatabase, org.glassfish.hk2.configuration.hub.api.BeanDatabase, java.lang.Object, java.util.List)
     */
    @Override
    public void commitDatabaseChange(BeanDatabase oldDatabase,
            BeanDatabase currentDatabase, Object commitMessage,
            List<Change> changes) {
        this.changes = changes;
        
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.BeanDatabaseUpdateListener#rollbackDatabaseChange(org.glassfish.hk2.configuration.hub.api.BeanDatabase, org.glassfish.hk2.configuration.hub.api.BeanDatabase, java.lang.Object, java.util.List)
     */
    @Override
    public void rollbackDatabaseChange(BeanDatabase currentDatabase,
            BeanDatabase proposedDatabase, Object commitMessage,
            List<Change> changes) {
        // TODO Auto-generated method stub
        
    }
    
    public List<Change> getChanges() {
        return changes;
    }
    
}
