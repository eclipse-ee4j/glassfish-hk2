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

import java.util.Map;

/**
 * This object contains information about a lifecycle
 * event.  Not all fields are valid for all 
 * lifecycle event types
 * 
 * @author jwells
 *
 */
public interface InstanceLifecycleEvent {
    /**
     * Gets the type of event this describes.  The values may be:<UL>
     * <LI>PRE_PRODUCTION</LI>
     * <LI>POST_PRODUCTION</LI>
     * <LI>PRE_DESTRUCTION</LI>
     * </UL>
     * 
     * @return The type of event being described
     */
    public InstanceLifecycleEventType getEventType();
    
    /**
     * The active descriptor that is being used for the operation.
     * For PRE_PRODUCTION and POST_PRODUCTION this is the descriptor that
     * will create or that created the object.  For PRE_DESTRUCTION this is the
     * descriptor that will be used to destroy the object
     * 
     * @return The descriptor associated with this event
     */
    public ActiveDescriptor<?> getActiveDescriptor();
    
    /**
     * The object that is being described by this event.  In the
     * POST_PRODUCTION case this is the object that was just produced.
     * In the PRE_DESTRUCTION case this is the object that will be
     * destroyed.  Will be null in the PRE_PRODUCTION case
     * 
     * @return The object that was produced or will be destroyed.  Will
     * be null in the PRE_PRODUCTION case
     */
    public Object getLifecycleObject();
    
    /**
     * A map from the Injectee to the object actually used
     * in the production, if known.  This will return null
     * in the PRE_DESTRUCTION case.  In the PRE_PRODUCTION and
     * POST_PRODUCTION cases this will return non-null if the
     * system knows the objects that will be or were injected into
     * the produced object.  If this method returns null in the PRE_PRODUCTION or
     * POST_PRODUCTION case then the system does not know what objects
     * were injected into the produced object, which happens in the case
     * of objects created by a {@link Factory} or objects created by
     * third-party (pre-reified) ActiveDescriptors.  If this
     * method returns an empty map then the system knows that
     * nothing will be or was injected into to produced object.
     * 
     * @return The known map of injection point to injected object,
     * if that information is known.  Will be null in the
     * PRE_DESTRUCTION case and in the case where the system does
     * not know the values.
     */
    public Map<Injectee, Object> getKnownInjectees();

}
