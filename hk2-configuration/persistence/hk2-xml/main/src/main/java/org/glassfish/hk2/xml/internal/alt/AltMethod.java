/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.xml.internal.alt;

import java.util.List;

/**
 * @author jwells
 *
 */
public interface AltMethod {
    /**
     * Returns the name of the method
     * 
     * @return The non null name of the method
     */
    public String getName();
    
    /**
     * Returns the return type of the method
     * @return The non-null return type (may still be
     * an AltClass representing void.class)
     */
    public AltClass getReturnType();
    
    public List<AltClass> getParameterTypes();
    
    /**
     * Returns the first type argument of the
     * return value.  For example, if the
     * return type is List&lt;String&gt; then
     * this would return an AltClass for String
     * 
     * @return The possibly null (if there is
     * no first type argument) Class that is
     * the first type argument of the return value
     */
    public AltClass getFirstTypeArgument();
    
    public AltClass getFirstTypeArgumentOfParameter(int index);
    
    /**
     * Returns the annotation if found on the method, or
     * null if not found
     * 
     * @param annotation
     * @return
     */
    public AltAnnotation getAnnotation(String annotation);
    
    public List<AltAnnotation> getAnnotations();
    
    public boolean isVarArgs();
    
    /**
     * Sets the method information once it has been calculated.  This
     * field is not set by the underlying provider, but is calculated
     * later
     * 
     * @param methodInfo The possibly null methodInformation
     */
    public void setMethodInformation(MethodInformationI methodInfo);
    
    /**
     * Gets the method information once it has been calculated.  This
     * field is not set by the underlying provider, but is calculated
     * later
     * 
     * @return The possibly null methodInformation
     */
    public MethodInformationI getMethodInformation();

}
