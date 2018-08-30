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

package org.glassfish.hk2.tests.locator.messaging.basic;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.glassfish.hk2.api.AnnotationLiteral;
import org.glassfish.hk2.api.messaging.Topic;

/**
 * @author jwells
 *
 */
@Singleton
public class ColorPublisher {
    @Inject
    private Topic<Black> blackPublisher;
    
    @Inject @Red
    private Topic<Color> redPublisher;
    
    @Inject @Green
    private Topic<Color> greenPublisher;
    
    @Inject
    private Topic<Color> genericPublisher;
    
    public void publishBlackEvent() {
        blackPublisher.publish(new Black());
    }
    
    public void publishRedEvent() {
        redPublisher.publish(new GenericColor(Color.RED));
    }
    
    public void publishGreenEvent() {
        greenPublisher.publish(new GenericColor(Color.GREEN));
    }
    
    public void publishGenericColor(String color) {
        if (Color.BLACK.equals(color)) {
            genericPublisher.publish(new GenericColor(Color.BLACK));
        }
        else if (Color.RED.equals(color)) {
            genericPublisher.qualifiedWith(new RedImpl()).publish(new GenericColor(Color.RED));
        }
        else if (Color.GREEN.equals(color)) {
            genericPublisher.qualifiedWith(new GreenImpl()).publish(new GenericColor(Color.GREEN));
        }
    }
    
    private static class RedImpl extends AnnotationLiteral<Red> implements Red  {
    }
    
    private static class GreenImpl extends AnnotationLiteral<Green> implements Green  {
    }

}
