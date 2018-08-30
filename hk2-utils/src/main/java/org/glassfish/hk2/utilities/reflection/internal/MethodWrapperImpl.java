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

package org.glassfish.hk2.utilities.reflection.internal;

import java.lang.reflect.Method;

import org.glassfish.hk2.utilities.reflection.MethodWrapper;
import org.glassfish.hk2.utilities.reflection.Pretty;
import org.glassfish.hk2.utilities.reflection.ReflectionHelper;

/**
 * Wrapper of methods with an equals and hashCode that
 * makes methods hiding other methods be equal
 * 
 * @author jwells
 *
 */
public class MethodWrapperImpl implements MethodWrapper {
    private final Method method;
    private final int hashCode;
    
    public MethodWrapperImpl(Method method) {
        if (method == null) throw new IllegalArgumentException();
        
        this.method = method;
        
        int hashCode = 0;
        
        hashCode ^= method.getName().hashCode();
        hashCode ^= method.getReturnType().hashCode();
        for (Class<?> param : method.getParameterTypes()) {
            hashCode ^= param.hashCode();
        }
        
        this.hashCode = hashCode;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.utilities.reflection.MethodWrapper#getMethod()
     */
    @Override
    public Method getMethod() {
        return method;
    }
    
    @Override
    public int hashCode() {
        return hashCode;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof MethodWrapperImpl)) return false;
        
        MethodWrapperImpl other = (MethodWrapperImpl) o;
        
        if (!method.getName().equals(other.method.getName())) return false;
        if (!method.getReturnType().equals(other.method.getReturnType())) return false;
        
        Class<?> myParams[] = method.getParameterTypes();
        Class<?> otherParams[] = other.method.getParameterTypes();
        
        if (myParams.length != otherParams.length) return false;
        
        if (ReflectionHelper.isPrivate(method) || ReflectionHelper.isPrivate(other.method)) return false;
        
        for (int lcv = 0; lcv < myParams.length; lcv++) {
            if (!myParams[lcv].equals(otherParams[lcv])) return false;
        }
        
        return true;
    }
    
    @Override
    public String toString() {
        return "MethodWrapperImpl(" + Pretty.method(method) + "," + System.identityHashCode(this) + ")";
    }

}
