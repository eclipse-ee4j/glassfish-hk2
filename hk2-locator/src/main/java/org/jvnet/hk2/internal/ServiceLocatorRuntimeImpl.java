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

package org.jvnet.hk2.internal;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.Visibility;
import org.jvnet.hk2.external.runtime.ServiceLocatorRuntimeBean;

/**
 * @author jwells
 *
 */
@Singleton
@Visibility(DescriptorVisibility.LOCAL)
public class ServiceLocatorRuntimeImpl implements ServiceLocatorRuntimeBean {
    private final ServiceLocatorImpl locator;
    
    @Inject
    private ServiceLocatorRuntimeImpl(ServiceLocator locator) {
        this.locator = (ServiceLocatorImpl) locator;
    }

    /* (non-Javadoc)
     * @see org.jvnet.hk2.external.runtime.ServiceLocatorRuntimeBean#getNumberOfDescriptors()
     */
    @Override
    public int getNumberOfDescriptors() {
        return locator.getNumberOfDescriptors();
    }

    /* (non-Javadoc)
     * @see org.jvnet.hk2.external.runtime.ServiceLocatorRuntimeBean#getNumberOfChildren()
     */
    @Override
    public int getNumberOfChildren() {
        return locator.getNumberOfChildren();
    }

    /* (non-Javadoc)
     * @see org.jvnet.hk2.external.runtime.ServiceLocatorRuntimeBean#getServiceCacheSize()
     */
    @Override
    public int getServiceCacheSize() {
        return locator.getServiceCacheSize();
    }

    /* (non-Javadoc)
     * @see org.jvnet.hk2.external.runtime.ServiceLocatorRuntimeBean#getServiceCacheMaximumSize()
     */
    @Override
    public int getServiceCacheMaximumSize() {
        return locator.getServiceCacheMaximumSize();
    }

    /* (non-Javadoc)
     * @see org.jvnet.hk2.external.runtime.ServiceLocatorRuntimeBean#clearServiceCache()
     */
    @Override
    public void clearServiceCache() {
        locator.clearServiceCache();

    }

    /* (non-Javadoc)
     * @see org.jvnet.hk2.external.runtime.ServiceLocatorRuntimeBean#getReflectionCacheSize()
     */
    @Override
    public int getReflectionCacheSize() {
        return locator.getReflectionCacheSize();
    }

    /* (non-Javadoc)
     * @see org.jvnet.hk2.external.runtime.ServiceLocatorRuntimeBean#clearReflectionCache()
     */
    @Override
    public void clearReflectionCache() {
        locator.clearReflectionCache();

    }

}
