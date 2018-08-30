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

package org.glassfish.hk2.tests.locator.messaging.error;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.glassfish.hk2.api.messaging.Topic;

/**
 * @author jwells
 *
 */
@Singleton
public class Publisher {
    private final Topic<MyEvent> publisher;
    
    @Inject
    private Publisher(Topic<MyEvent> publisher) {
        this.publisher = publisher;
    }
    
    public void publish(MyEvent event) {
        publisher.publish(event);
    }
    
    public Topic<MyEvent> getTopic() {
        return publisher;
    }

}
