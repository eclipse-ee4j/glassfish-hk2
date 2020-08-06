/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
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
package org.glassfish.hk2.classmodel.reflect.impl;

import java.util.ArrayList;
import java.util.List;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureVisitor;

/**
 * Signature visitor to visit field and respective generic types
 *
 * @author gaurav.gupta@payara.fish
 */
public class FieldSignatureVisitorImpl extends SignatureVisitor {

    private final List<String> typeVariable = new ArrayList<>();

    public FieldSignatureVisitorImpl() {
        super(Opcodes.ASM7);
    }

    @Override
    public void visitTypeVariable(String typeVariable) {
        this.typeVariable.add(typeVariable);
    }

    public List<String> getTypeVariable() {
        return typeVariable;
    }

}
