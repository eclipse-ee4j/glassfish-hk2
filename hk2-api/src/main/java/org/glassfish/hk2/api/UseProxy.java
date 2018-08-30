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

package org.glassfish.hk2.api;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This annotation is used when automatically analyzing a class or a 
 * {link {@link Factory#provide()} method to indicate that the descriptor
 * either should or should not use a proxy.  This annotation is only used
 * for automatic class analysis, and the value in a descriptor will not be
 * checked against this annotation at run time.
 * <p>
 * Note that this annotation is NOT inherited, and hence must be on
 * the analyzed class itself, and not superclasses or interfaces
 * of the analyzed class
 * <p>
 * @see Proxiable Unproxiable ProxyForSameScope
 * 
 * @author jwells
 *
 */
@Documented
@Retention(RUNTIME)
@Target( { METHOD, TYPE })
public @interface UseProxy {
    /**
     * If this returns true then the isProxiable will be set to true, but
     * if this returns false then the isProxiable will be set to false
     * 
     * @return The value that the isProxiable field of the descriptor should take
     */
    public boolean value() default true;
}
