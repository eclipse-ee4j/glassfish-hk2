/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
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
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * When this annotation is placed on an abstract class the methods of
 * the class that are abstract will be generated into a subclass by
 * the hk2-metadata-generator along with an empty
 * {@link org.jvnet.hk2.annotations.Service} annotation
 * <p>
 * Any {@link javax.inject.Named} or {@link org.jvnet.hk2.annotations.ContractsProvided}
 * annotation on the class marked with this annotation will also be copied to the
 * implementation.  No other qualifier or annotation will be copied to the concrete
 * implementation.  However, the {@link org.glassfish.hk2.api.Rank} annotation
 * on the stub class will be honored.
 * <p>
 * The methods generated into the subclass can either return null and fixed
 * values (for scalars) or can throw exceptions, depending on the
 * {@link Stub.Type} value of this annotation
 * <p>
 * Using this annotation is useful for testing, though it will work both with
 * test code and non-test code
 * 
 * @author jwells
 */
@Documented
@Retention(SOURCE)
@Target( { TYPE} )
public @interface Stub {
    /**
     * This value determines what the generated methods do
     * <p>
     * If set to {@link Type#VALUES} then the methods will return
     * nulls or fixed values for scalars.
     * <p>
     * If set to {@link Type#EXCEPTIONS} then the methods will
     * throw UnsupportedOperationException
     * 
     * @return The behavior of the generated methods
     */
    public Type value() default Type.VALUES;
    
    public enum Type {
        /** The generated methods of this stub will return null and fixed values */
        VALUES,
        
        /** The generated methods of this stub will throw an UnsupportedOperationException */
        EXCEPTIONS
    }

}
