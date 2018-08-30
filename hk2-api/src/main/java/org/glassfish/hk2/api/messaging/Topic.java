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

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

/**
 * This service is used for publishing events to subscribers.  The type of the Topic is
 * the type of event that will be distributed
 * 
 * @author jwells
 * @param <T> The type of event to be distributed to subscribers
 *
 */
public interface Topic<T> {
    /**
     * Publishes a message to all subscribers
     * 
     * @param message The non-null message to send to all current subscribers
     * @throws IllegalStateException If there is no implementation of
     * {@link TopicDistributionService} to do the distribution of the message
     */
    public void publish(T message);
    
    /**
     * Returns an Topic that is further qualified
     * with the given name
     * 
     * @param name The value field of the Named annotation parameter.  Must
     * not be null
     * @return A topic further qualified with the given name
     */
    public Topic<T> named(String name);
    
    /**
     * Returns an Topic that is of the given type.  This type
     * must be more specific than the type of this Topic
     * 
     * @param type The type to restrict the returned Topic to
     * @return A Topic restricted to only producing messages of the given type
     */
    public <U> Topic<U> ofType(Type type);
    
    /**
     * A set of qualifiers to further restrict this Topic to.
     * 
     * @param qualifiers The qualifiers to further restrict this Topic to
     * @return An Topic restricted with the given qualifiers
     */
    public Topic<T> qualifiedWith(Annotation... qualifiers);
    
    /**
     * Gets the type of the topic, in order to match the message
     * to subscribers
     * 
     * @return the Type of this topic.  Will not return null
     */
    public Type getTopicType();
    
    /**
     * The qualifiers associated with this Topic.  Messages
     * should only be distributed to subscribers that have
     * matching qualifiers
     * 
     * @return the non-null but possibly empty set of
     * qualifiers associated with this Topic
     */
    public Set<Annotation> getTopicQualifiers();
}
