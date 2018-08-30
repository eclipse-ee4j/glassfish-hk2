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

package org.jvnet.hk2.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.glassfish.hk2.api.messaging.Topic;
import org.glassfish.hk2.api.messaging.TopicDistributionService;
import org.glassfish.hk2.utilities.NamedImpl;

/**
 * @author jwells
 *
 */
public class TopicImpl<T> implements Topic<T> {
    private final ServiceLocatorImpl locator;
    private final Type topicType;
    private final Set<Annotation> requiredQualifiers;
    
    /* package */ TopicImpl(ServiceLocatorImpl locator,
            Type topicType,
            Set<Annotation> requiredQualifiers) {
        this.locator = locator;
        this.topicType = topicType;
        this.requiredQualifiers = Collections.unmodifiableSet(requiredQualifiers);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.messaging.Topic#publish(java.lang.Object)
     */
    @Override
    public void publish(T message) {
        if (message == null) throw new IllegalArgumentException();
        
        TopicDistributionService distributor = locator.getService(TopicDistributionService.class);
            
        if (distributor == null) {
             throw new IllegalStateException("There is no implementation of the TopicDistributionService to distribute the message");
        }
            
        distributor.distributeMessage(this, message);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.messaging.Topic#named(java.lang.String)
     */
    @Override
    public Topic<T> named(String name) {
        return qualifiedWith(new NamedImpl(name));
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.messaging.Topic#ofType(java.lang.reflect.Type)
     */
    @Override
    public <U> Topic<U> ofType(Type type) {
        return new TopicImpl<U>(locator, type, requiredQualifiers);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.messaging.Topic#qualifiedWith(java.lang.annotation.Annotation[])
     */
    @Override
    public Topic<T> qualifiedWith(Annotation... qualifiers) {
        HashSet<Annotation> moreAnnotations = new HashSet<Annotation>(requiredQualifiers);
        for (Annotation qualifier : qualifiers) {
            moreAnnotations.add(qualifier);
        }
        
        return new TopicImpl<T>(locator, topicType, moreAnnotations);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.messaging.Topic#getTopicType()
     */
    @Override
    public Type getTopicType() {
        return topicType;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.messaging.Topic#getTopicQualifiers()
     */
    @Override
    public Set<Annotation> getTopicQualifiers() {
        return requiredQualifiers;
    }

}
