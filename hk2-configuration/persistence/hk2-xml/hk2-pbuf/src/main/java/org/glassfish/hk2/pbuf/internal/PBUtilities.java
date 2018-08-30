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

package org.glassfish.hk2.pbuf.internal;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.glassfish.hk2.pbuf.api.annotations.OneOf;
import org.glassfish.hk2.utilities.reflection.ClassReflectionHelper;
import org.glassfish.hk2.utilities.reflection.MethodWrapper;
import org.glassfish.hk2.utilities.reflection.internal.ClassReflectionHelperImpl;

import com.google.protobuf.CodedInputStream;

/**
 * @author jwells
 *
 */
public class PBUtilities {
    private static final String GET = "get";
    private static final String IS = "is";
    private static final String SET = "set";
    
    private static final ClassReflectionHelper reflectionHelper = new ClassReflectionHelperImpl();
    
    public static String getOneOf(Class<?> oInterface, String methodName, Class<?> type) {
        Set<Method> allNamed = getAllMethodsWithName(oInterface, methodName);
        if (allNamed.isEmpty()) {
            throw new AssertionError("Could not find method " + methodName + " on bean " + oInterface.getName());
        }
        
        if (allNamed.size() == 1) {
            // An optimistic optimization
            Method method = allNamed.iterator().next();
            
            OneOf oneOf = method.getAnnotation(OneOf.class);
            if (oneOf == null) {
                return null;
            }
            
            return oneOf.value();
        }
        
        // Need to do this the difficult way
        Method found = null;
        if (isGetter(methodName)) {
            for (Method m : allNamed) {
                Class<?> retType = m.getReturnType();
                
                if (!retType.equals(type)) {
                    continue;
                }
                
                Class<?> allParams[] = m.getParameterTypes();
                if (allParams == null || allParams.length == 0) {
                    found = m;
                    break;
                }
            }
            
        }
        else if (isSetter(methodName)) {
            for (Method m : allNamed) {
                Class<?> retType = m.getReturnType();
                if (!void.class.equals(retType)) {
                    continue;
                }
                
                Class<?> allParams[] = m.getParameterTypes();
                if (allParams == null) {
                    continue;
                }
                if (allParams.length != 1) {
                    continue;
                }
                
                if (allParams[0].equals(type)) {
                    found = m;
                    break;
                }
            }
        }
        else {
            throw new AssertionError("Unable to analyze a method that is neiter a getter or a setter: " + methodName + " on " + oInterface.getName());
        }
        
        if (found == null) {
            throw new AssertionError("Could not find method " + methodName + " on bean " + oInterface.getName() + " with type " + type.getName());
            
        }
        
        OneOf oneOf = found.getAnnotation(OneOf.class);
        if (oneOf == null) {
            return null;
        }
        
        return oneOf.value();
    }
    
    private static boolean isGetter(String methodName) {
        if (methodName.startsWith(GET) && (methodName.length() > 3)) {
            return true;
        }
        if (methodName.startsWith(IS) && (methodName.length() > 2)) {
            return true;
        }
        
        return false;
    }
    
    private static boolean isSetter(String methodName) {
        if (methodName.startsWith(SET) && (methodName.length() > 3)) {
            return true;
        }
        
        return false;
    }
    
    private static Set<Method> getAllMethodsWithName(Class<?> oInterface, String methodName) {
        HashSet<Method> retVal = new HashSet<Method>();
        
        Set<MethodWrapper> allMethods = reflectionHelper.getAllMethods(oInterface);
        
        for (MethodWrapper wrapper : allMethods) {
            if (methodName.equals(wrapper.getMethod().getName())) {
                retVal.add(wrapper.getMethod());
            }
        }
        
        return retVal;
    }
    
    public static String camelCaseToUnderscore(String camelCase) {
        StringBuffer sb = new StringBuffer();
        
        char oneBackCache = 0;
        boolean firstAlreadyWritten = false;
        boolean previousLowerCase = false;
        for (int lcv = 0; lcv < camelCase.length(); lcv++) {
            char charAt = camelCase.charAt(lcv);
            if (Character.isUpperCase(charAt)) {
                charAt = Character.toLowerCase(charAt);
                if (oneBackCache != 0) {
                    if (firstAlreadyWritten && previousLowerCase) {
                        sb.append("_");
                    }
                    
                    sb.append(oneBackCache);
                    firstAlreadyWritten = true;
                    previousLowerCase = false;
                }
                
                oneBackCache = charAt;
            }
            else {
                if (oneBackCache != 0) {
                    if (firstAlreadyWritten) {
                        sb.append("_");
                        firstAlreadyWritten = true;
                    }
                    sb.append(oneBackCache);
                    firstAlreadyWritten = true;
                    
                    oneBackCache = 0;
                }
                
                sb.append(charAt);
                firstAlreadyWritten = true;
                
                previousLowerCase = true;
            }
        }
        
        if (oneBackCache != 0) {
            if (firstAlreadyWritten && previousLowerCase) {
                sb.append("_");
            }
            
            sb.append(oneBackCache);
        }
        
        return sb.toString();
    }
}
