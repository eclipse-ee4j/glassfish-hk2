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

package org.glassfish.hk2.xml.internal.alt.papi;

import java.util.Collections;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.glassfish.hk2.xml.internal.Utilities;
import org.glassfish.hk2.xml.internal.alt.AltAnnotation;
import org.glassfish.hk2.xml.internal.alt.AltClass;
import org.glassfish.hk2.xml.internal.alt.AltMethod;

/**
 * @author jwells
 *
 */
public class ArrayTypeAltClassImpl implements AltClass {
    private final ArrayType arrayType;
    private final ProcessingEnvironment processingEnv;
    private String name;
    private String simpleName;
    
    public ArrayTypeAltClassImpl(ArrayType arrayType, ProcessingEnvironment processingEnv) {
        this.arrayType = arrayType;
        this.processingEnv = processingEnv;
    }
    
    private void calculateNames() {
        StringBuffer sb = new StringBuffer();
        
        TypeMirror currentMirror = arrayType;
        
        int numBraces = 0;
        while (TypeKind.ARRAY.equals(currentMirror.getKind())) {
            sb.append("[");
            
            currentMirror = ((ArrayType) currentMirror).getComponentType();
            
            numBraces++;
        }
        
        // currentMirror is NOT an array
        String lSimpleName;
        if (currentMirror.getKind().isPrimitive()) {
            if (TypeKind.INT.equals(currentMirror.getKind())) {
                sb.append("I");
                lSimpleName = "int";
            }
            else if (TypeKind.LONG.equals(currentMirror.getKind())) {
                sb.append("J");
                lSimpleName = "long";
            }
            else if (TypeKind.BYTE.equals(currentMirror.getKind())) {
                sb.append("B");
                lSimpleName = "byte";
            }
            else if (TypeKind.BOOLEAN.equals(currentMirror.getKind())) {
                sb.append("Z");
                lSimpleName = "boolean";
            }
            else if (TypeKind.CHAR.equals(currentMirror.getKind())) {
                sb.append("C");
                lSimpleName = "char";
            }
            else if (TypeKind.DOUBLE.equals(currentMirror.getKind())) {
                sb.append("D");
                lSimpleName = "double";
            }
            else if (TypeKind.FLOAT.equals(currentMirror.getKind())) {
                sb.append("F");
                lSimpleName = "float";
            }
            else if (TypeKind.SHORT.equals(currentMirror.getKind())) {
                sb.append("S");
                lSimpleName = "short";
            }
            else {
                throw new AssertionError("Unknown primitive type " + currentMirror.getKind() + " for array " + arrayType);
            }
        }
        else if (TypeKind.DECLARED.equals(currentMirror.getKind())) {
            AltClass ac = Utilities.convertTypeMirror(currentMirror, processingEnv);
            
            sb.append("L" + ac.getName() + ";");
            
            lSimpleName = ac.getSimpleName();
        }
        else {
            throw new AssertionError("Unknown array type: " + currentMirror.getKind() + " for array " + arrayType);
        }
        
        name = sb.toString();
        
        StringBuffer simpleNameSB = new StringBuffer(lSimpleName);
        for (int lcv = 0; lcv < numBraces; lcv++) {
            simpleNameSB.append("[]");
        }
        
        simpleName = simpleNameSB.toString();
        
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.internal.alt.AltClass#getName()
     */
    @Override
    public synchronized String getName() {
        if (name != null) return name;
        
        calculateNames();
        
        return name;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.internal.alt.AltClass#getSimpleName()
     */
    @Override
    public synchronized String getSimpleName() {
        if (simpleName != null) return simpleName;
        
        calculateNames();
        
        return simpleName;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.internal.alt.AltClass#getAnnotations()
     */
    @Override
    public List<AltAnnotation> getAnnotations() {
        return Collections.emptyList();
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.internal.alt.AltClass#getMethods()
     */
    @Override
    public List<AltMethod> getMethods() {
        return Collections.emptyList();
    }
    
    @Override
    public AltClass getSuperParameterizedType(AltClass superclass,
            int paramIndex) {
        return null;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.internal.alt.AltClass#isInterface()
     */
    @Override
    public boolean isInterface() {
        return false;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.internal.alt.AltClass#isArray()
     */
    @Override
    public boolean isArray() {
        return true;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.internal.alt.AltClass#getComponentType()
     */
    @Override
    public AltClass getComponentType() {
        TypeMirror compTypeAsMirror = arrayType.getComponentType();
        
        return Utilities.convertTypeMirror(compTypeAsMirror, processingEnv);
    }

    

}
