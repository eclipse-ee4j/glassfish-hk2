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

package org.jvnet.hk2.generator.internal;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;

/**
 * This just makes the main-line code less messy
 * 
 * @author jwells
 *
 */
public abstract class AbstractAnnotationVisitorImpl extends AnnotationVisitor {
    /**
     * Gives the version implemented to the superclass
     */
    public AbstractAnnotationVisitorImpl() {
        super(Opcodes.ASM7);
    }
    
    /* (non-Javadoc)
     * @see org.objectweb.asm.AnnotationVisitor#visitAnnotation(java.lang.String, java.lang.String)
     */
    @Override
    public void visit(String name, Object value) {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.objectweb.asm.AnnotationVisitor#visitAnnotation(java.lang.String, java.lang.String)
     */
    @Override
    public AnnotationVisitor visitAnnotation(String arg0, String arg1) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.objectweb.asm.AnnotationVisitor#visitArray(java.lang.String)
     */
    @Override
    public AnnotationVisitor visitArray(String arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.objectweb.asm.AnnotationVisitor#visitEnd()
     */
    @Override
    public void visitEnd() {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.objectweb.asm.AnnotationVisitor#visitEnum(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void visitEnum(String arg0, String arg1, String arg2) {
        // TODO Auto-generated method stub

    }

}
