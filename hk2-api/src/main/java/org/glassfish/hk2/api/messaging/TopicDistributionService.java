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

package org.glassfish.hk2.api.messaging;

import org.jvnet.hk2.annotations.Contract;

/**
 * This service is responsible for distributing messages to Topic subscribers
 * <p>
 * A default implementation of this service is provided by HK2 and can
 * be added to the system with the
 * {@link org.glassfish.hk2.utilities.ServiceLocatorUtilities#enableTopicDistribution(org.glassfish.hk2.api.ServiceLocator)}
 * method.  The default implementation will be named &quot;HK2TopicDistributionService&quot;
 * 
 * @author jwells
 *
 */
@Contract
public interface TopicDistributionService {
    /** The name of the default TopicDistributionService that is added by {@link org.glassfish.hk2.utilities.ServiceLocatorUtilities} */
    public final static String HK2_DEFAULT_TOPIC_DISTRIBUTOR = "HK2TopicDistributionService";
    
    /**
     * Must distribute the message to all of the matching topic subscribers.  Any exception
     * thrown from this method will be ignored.  Instead error handling should
     * be performed by the implementation of this message
     * 
     * @param topic The topic to which to distribute the message.  Must not be null
     * @param message The message to send to the topic.  Must not be null
     */
    public void distributeMessage(Topic<?> topic, Object message);

}
