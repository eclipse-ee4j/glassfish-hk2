/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.xml.test.defaulting;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.InstanceLifecycleEvent;
import org.glassfish.hk2.api.InstanceLifecycleEventType;
import org.glassfish.hk2.api.InstanceLifecycleListener;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.hk2.xml.api.XmlService;
import org.glassfish.hk2.xml.test.beans.SSLManagerBean;
import org.glassfish.hk2.xml.test.beans.SecurityManagerBean;

/**
 * @author jwells
 *
 */
@Singleton
public class SecurityManagerBeanDefaulter implements InstanceLifecycleListener {
    private final static Filter FILTER = BuilderHelper.createContractFilter(SecurityManagerBean.class.getName());
    
    @Inject
    private Provider<XmlService> xmlService;

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.InstanceLifecycleListener#getFilter()
     */
    @Override
    public Filter getFilter() {
        return FILTER;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.InstanceLifecycleListener#lifecycleEvent(org.glassfish.hk2.api.InstanceLifecycleEvent)
     */
    @Override
    public void lifecycleEvent(InstanceLifecycleEvent lifecycleEvent) {
        if (!InstanceLifecycleEventType.POST_PRODUCTION.equals(lifecycleEvent.getEventType())) return;
        
        if (!(lifecycleEvent.getLifecycleObject() instanceof SecurityManagerBean)) return;
        SecurityManagerBean bean = (SecurityManagerBean) lifecycleEvent.getLifecycleObject();
        
        if (bean.getSSLManager() != null) return;
        
        SSLManagerBean defaultBean = xmlService.get().createBean(SSLManagerBean.class);
        bean.setSSLManager(defaultBean);
    }

}
