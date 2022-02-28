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
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * This in only here to keep the main-line code less messy
 * 
 * @author jwells
 *
 */
public abstract class AbstractMethodVisitorImpl extends MethodVisitor {
    /**
     * The constructor that gives the implemented version to the superclass
     */
    public AbstractMethodVisitorImpl() {
        super(Opcodes.ASM9);
    }

    /* (non-Javadoc)
     * @see org.objectweb.asm.MethodVisitor#visitAnnotationDefault()
     */
    @Override
    public AnnotationVisitor visitAnnotationDefault() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.objectweb.asm.MethodVisitor#visitAttribute(org.objectweb.asm.Attribute)
     */
    @Override
    public void visitAttribute(Attribute arg0) {

    }
    
    /* (non-Javadoc)
     * @see org.objectweb.asm.MethodVisitor#visitEnd()
     */
    @Override
    public void visitEnd() {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.objectweb.asm.MethodVisitor#visitCode()
     */
    @Override
    public void visitCode() {

    }

    /* (non-Javadoc)
     * @see org.objectweb.asm.MethodVisitor#visitFieldInsn(int, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void visitFieldInsn(int arg0, String arg1, String arg2, String arg3) {

    }

    /* (non-Javadoc)
     * @see org.objectweb.asm.MethodVisitor#visitFrame(int, int, java.lang.Object[], int, java.lang.Object[])
     */
    @Override
    public void visitFrame(int arg0, int arg1, Object[] arg2, int arg3,
            Object[] arg4) {

    }

    /* (non-Javadoc)
     * @see org.objectweb.asm.MethodVisitor#visitIincInsn(int, int)
     */
    @Override
    public void visitIincInsn(int arg0, int arg1) {

    }

    /* (non-Javadoc)
     * @see org.objectweb.asm.MethodVisitor#visitInsn(int)
     */
    @Override
    public void visitInsn(int arg0) {

    }

    /* (non-Javadoc)
     * @see org.objectweb.asm.MethodVisitor#visitIntInsn(int, int)
     */
    @Override
    public void visitIntInsn(int arg0, int arg1) {

    }

    /* (non-Javadoc)
     * @see org.objectweb.asm.MethodVisitor#visitJumpInsn(int, org.objectweb.asm.Label)
     */
    @Override
    public void visitJumpInsn(int arg0, Label arg1) {

    }

    /* (non-Javadoc)
     * @see org.objectweb.asm.MethodVisitor#visitLabel(org.objectweb.asm.Label)
     */
    @Override
    public void visitLabel(Label arg0) {

    }

    /* (non-Javadoc)
     * @see org.objectweb.asm.MethodVisitor#visitLdcInsn(java.lang.Object)
     */
    @Override
    public void visitLdcInsn(Object arg0) {

    }

    /* (non-Javadoc)
     * @see org.objectweb.asm.MethodVisitor#visitLineNumber(int, org.objectweb.asm.Label)
     */
    @Override
    public void visitLineNumber(int arg0, Label arg1) {

    }

    /* (non-Javadoc)
     * @see org.objectweb.asm.MethodVisitor#visitLocalVariable(java.lang.String, java.lang.String, java.lang.String, org.objectweb.asm.Label, org.objectweb.asm.Label, int)
     */
    @Override
    public void visitLocalVariable(String arg0, String arg1, String arg2,
            Label arg3, Label arg4, int arg5) {

    }

    /* (non-Javadoc)
     * @see org.objectweb.asm.MethodVisitor#visitLookupSwitchInsn(org.objectweb.asm.Label, int[], org.objectweb.asm.Label[])
     */
    @Override
    public void visitLookupSwitchInsn(Label arg0, int[] arg1, Label[] arg2) {

    }

    /* (non-Javadoc)
     * @see org.objectweb.asm.MethodVisitor#visitMaxs(int, int)
     */
    @Override
    public void visitMaxs(int arg0, int arg1) {

    }

    /* (non-Javadoc)
     * @see org.objectweb.asm.MethodVisitor#visitMethodInsn(int, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void visitMethodInsn(int arg0, String arg1, String arg2, String arg3) {

    }

    /* (non-Javadoc)
     * @see org.objectweb.asm.MethodVisitor#visitMultiANewArrayInsn(java.lang.String, int)
     */
    @Override
    public void visitMultiANewArrayInsn(String arg0, int arg1) {

    }

    /* (non-Javadoc)
     * @see org.objectweb.asm.MethodVisitor#visitParameterAnnotation(int, java.lang.String, boolean)
     */
    @Override
    public AnnotationVisitor visitParameterAnnotation(int arg0, String arg1,
            boolean arg2) {
        return null;
    }

    /* (non-Javadoc)
     * @see org.objectweb.asm.MethodVisitor#visitTableSwitchInsn(int, int, org.objectweb.asm.Label, org.objectweb.asm.Label[])
     */
    @Override
    public void visitTableSwitchInsn(int arg0, int arg1, Label arg2,
            Label[] arg3) {

    }

    /* (non-Javadoc)
     * @see org.objectweb.asm.MethodVisitor#visitTryCatchBlock(org.objectweb.asm.Label, org.objectweb.asm.Label, org.objectweb.asm.Label, java.lang.String)
     */
    @Override
    public void visitTryCatchBlock(Label arg0, Label arg1, Label arg2,
            String arg3) {

    }

    /* (non-Javadoc)
     * @see org.objectweb.asm.MethodVisitor#visitTypeInsn(int, java.lang.String)
     */
    @Override
    public void visitTypeInsn(int arg0, String arg1) {

    }

    /* (non-Javadoc)
     * @see org.objectweb.asm.MethodVisitor#visitVarInsn(int, int)
     */
    @Override
    public void visitVarInsn(int arg0, int arg1) {

    }

}
