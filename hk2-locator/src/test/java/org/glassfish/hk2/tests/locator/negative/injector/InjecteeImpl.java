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

package org.glassfish.hk2.tests.locator.negative.injector;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.util.Set;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.Unqualified;

/**
 * @author jwells
 *
 */
public class InjecteeImpl implements Injectee {

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Injectee#getRequiredType()
     */
    @Override
    public Type getRequiredType() {
        // returns null on purpose
        return null;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Injectee#getRequiredQualifiers()
     */
    @Override
    public Set<Annotation> getRequiredQualifiers() {
        throw new AssertionError("never called");
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Injectee#getPosition()
     */
    @Override
    public int getPosition() {
        throw new AssertionError("never called");
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Injectee#getParent()
     */
    @Override
    public AnnotatedElement getParent() {
        throw new AssertionError("never called");
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Injectee#isOptional()
     */
    @Override
    public boolean isOptional() {
        throw new AssertionError("never called");
    }
    
    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Injectee#isOptional()
     */
    @Override
    public boolean isSelf() {
        throw new AssertionError("never called");
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Injectee#getInjecteeClass()
     */
    @Override
    public Class<?> getInjecteeClass() {
        throw new AssertionError("never called");
    }

    @Override
    public Unqualified getUnqualified() {
        throw new AssertionError("never called");
    }

    @Override
    public ActiveDescriptor<?> getInjecteeDescriptor() {
        throw new AssertionError("never called");
    }

}
