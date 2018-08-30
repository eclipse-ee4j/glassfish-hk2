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

package org.glassfish.hk2.utilities.reflection;

import java.lang.annotation.Annotation;

/**
 * Data structure concerning scope annotations
 * 
 * @author jwells
 *
 */
public class ScopeInfo {
    private final Annotation scope;
    private final Class<? extends Annotation> annoType;

    /**
     * Constructor of the data structure concerning the scope annotation
     * 
     * @param scope The scope annotation
     * @param annoType The type of annotation
     */
    public ScopeInfo(Annotation scope, Class<? extends Annotation> annoType) {
        this.scope = scope;
        this.annoType = annoType;
    }
    
    /**
     * Returns the scope for this data structure
     * @return The non-null scope for this data structure
     */
    public Annotation getScope() {
        return scope;
    }

    /**
     * Returns the annotation class for this annotation type
     * 
     * @return the annotation type for this scope
     */
    public Class<? extends Annotation> getAnnoType() {
        return annoType;
    }

}
