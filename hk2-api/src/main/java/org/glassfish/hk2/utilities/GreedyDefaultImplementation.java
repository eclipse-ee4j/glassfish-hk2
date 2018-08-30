/*
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.utilities;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;


/**
 * This annotation can be put on interfaces in order
 * to provide the {@link GreedyResolver} the default
 * implementation that should be bound when this
 * interface is injected and there are no other
 * implementations
 * 
 * @author jwells
 *
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface GreedyDefaultImplementation {
    /**
     * A class that implements this interface that
     * should be used as a default if there are no
     * other implementations of this class.  This
     * is only used when the {@link GreedyResolver}
     * is being used
     * 
     * @return the default implementation of this class
     */
    public Class<?> value();
}
