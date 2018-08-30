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

package org.jvnet.hk2.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * An explicit list of contracts provided.  Overrides any other
 * contract metadata on subclasses or interfaces.
 * <p>
 * There are times when a service would like to either restrict 
 * {@link Contract}s that it provides, or would like to add
 * subclasses or interfaces that are not naturally marked
 * {@link Contract} to be contracts that it provides.  In that
 * case it should use ContractsProvided, which allows the service
 * to explicitly say the contracts that it should provide.
 * 
 * @author jwells
 *
 */
@Documented
@Retention(RUNTIME)
@Target({TYPE})
public @interface ContractsProvided {
    /**
     * The set of contracts that should be explicitly provided
     * by this service.
     * 
     * @return The set of contracts that should be provided
     * by this service
     */
    public Class<?>[] value();

}
