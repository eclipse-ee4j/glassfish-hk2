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

package org.glassfish.hk2.extras.events;

import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.messaging.Topic;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.jvnet.hk2.annotations.Contract;

/**
 * When using the TopicDistributionService added with
 * {@link ServiceLocatorUtilities#enableTopicDistribution(org.glassfish.hk2.api.ServiceLocator)}
 * if a subscriber throws an exception this service will be called.
 * All implementation of this service will be called.
 * 
 * @author jwells
 */
@Contract
public interface DefaultTopicDistributionErrorService {
    /**
     * This method will be called once per {@link Topic#publish(Object)}
     * call after the message has been distributed to all subscribers.
     * The {@link MultiException} will contain the errors from any
     * subscribers that threw exceptions.  This method will
     * not be called if no subscribers threw exceptions
     * 
     * @param topic The topic that the message was sent to
     * @param message The message that was sent to the topic
     * @param error The exceptions thrown by the subscribers of this {@link Topic}
     */
    public void subscribersFailed(Topic<?> topic, Object message, MultiException error);

}
