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

package org.glassfish.hk2.api.messaging;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

import org.glassfish.hk2.api.Metadata;

/**
 * This qualifier must be placed on any hk2 descriptor that can
 * receive messages.  This includes message receiver classes automatically
 * analyzed by hk2, or any {@link org.glassfish.hk2.api.Factory#provide()}
 * methods automatically analyzed by hk2 or any user-defined
 * {@link org.glassfish.hk2.api.Descriptor} who can receive messages
 * 
 * @author jwells
 *
 */
@Documented
@Retention(RUNTIME)
@Qualifier
@Target({TYPE, METHOD})
public @interface MessageReceiver {
    public static final String EVENT_RECEIVER_TYPES = "org.glassfish.hk2.messaging.messageReceiverTypes";
    
    /**
     * A list of message types that this service may receive.  The
     * default value of an empty array represents any message type.
     * Be warned that if the default value is used that any event
     * being fired will cause the descriptor with this qualifier
     * to get reified (classloaded) which may be expensive.  In order
     * to have a more efficient application it is better to fill
     * this value in with all the event types this service might
     * receive
     * 
     * @return A list of the classes that might be received as
     * topic messages.  If the empty set then this class might
     * receive any topic event
     */
    @Metadata(EVENT_RECEIVER_TYPES)
    public Class<?>[] value() default {};

}
