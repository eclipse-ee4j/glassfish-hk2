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

package org.glassfish.examples.events.threaded;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.glassfish.hk2.api.Rank;
import org.glassfish.hk2.api.messaging.Topic;
import org.glassfish.hk2.api.messaging.TopicDistributionService;
import org.jvnet.hk2.annotations.Service;

/**
 * This implementation of the {@link TopicDistributionService} enhances
 * the default HK2 implementation by running the subscribers on a
 * different thread rather than on the thread of the caller.
 * 
 * This has a higher rank than the default {@link TopicDistributionService}
 * to ensure it will get called by {@link Topic#publish(Object)}.
 * 
 * @author jwells
 *
 */
@Service @Singleton @Named @Rank(100)
public class ThreadedEventDistributor implements TopicDistributionService {
    @Inject @Named(TopicDistributionService.HK2_DEFAULT_TOPIC_DISTRIBUTOR)
    private TopicDistributionService defaultDistributor;

    @Override
    public void distributeMessage(Topic<?> topic, Object message) {
        Thread t = new Thread(new Distributor(topic, message), "TopicDistributor");
        t.setDaemon(true);
        
        t.start();
    }

    private final class Distributor implements Runnable {
        private final Topic<?> topic;
        private final Object message;
        
        private Distributor(Topic<?> topic, Object message) {
            this.topic = topic;
            this.message = message;
        }

        @Override
        public void run() {
            defaultDistributor.distributeMessage(topic, message);            
        }
    }
}
