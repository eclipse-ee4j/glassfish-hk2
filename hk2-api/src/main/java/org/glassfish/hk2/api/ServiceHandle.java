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

package org.glassfish.hk2.api;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This service handle can be used to get a specific instance
 * of a service, and can be used to destroy that service as well
 * 
 * @author jwells
 * @param <T> The type of the service that can be returned
 *
 */
public interface ServiceHandle<T> extends Closeable {
    /**
     * Gets the underlying service object
     * @return May return null (if the backing ActiveDescriptor returned null)
     * @throws MultiException if there was an error creating the service
     * @throws IllegalStateException if the handle was previously destroyed
     */
    public T getService();
    
    /**
     * Returns the ActiveDescriptor associated with this service handle
     * 
     * @return The ActiveDescriptor associated with this handle. Can return
     * null in cases where the Handle describes a service not associated with
     * an hk2 service, such as a constant service
     */
    public ActiveDescriptor<T> getActiveDescriptor();
    
    /**
     * This returns true if the underlying service has already been
     * created
     * 
     * @return true if the underlying service has been created
     */
    public boolean isActive();
    
    /**
     * Will destroy this object and all PerLookup instances created
     * because of this service
     * @deprecated since 2.6. Use {@link #close} instead
     */
    @Deprecated
    default public void destroy() { 
        close();
}
    
     /**
     * Will destroy this object and all PerLookup instances created
     * because of this service
     */
    @Override
    default public void close() { destroy(); }
    
    /**
     * Service data can be set on a service handle.  If the service
     * data is set prior to the services associated Context has
     * created an instance then this service data can be used
     * to influence the context's creation of the service.  The
     * service data is associated with a handle, not with
     * the service itself
     * 
     * @param serviceData Sets the serviceData for the handle
     * (may be null)
     */
    public void setServiceData(Object serviceData);
    
    /**
     * Service data can be set on a service handle.  If the service
     * data is set prior to the services associated Context has
     * created an instance then this service data can be used
     * to influence the context's creation of the service.  The
     * service data is associated with a handle, not with
     * the service itself
     * 
     * @return The service data for this service handle
     * (may return null)
     */
    public Object getServiceData();
    
    /**
     * Returns a list of subordinate subhandles
     * to this root handle
     * 
     * @return A non-null but possibly empty
     * list of subhandles subordinate to this root
     */
    public List<ServiceHandle<?>> getSubHandles();
}
