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

package org.jvnet.hk2.testing.junit.internal;

import java.util.List;
import java.util.Set;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

/**
 * @author jwells
 *
 */
public class ClassVisitorImpl extends ClassVisitor {
    private final static String SERVICE_CLASS_FORM = "Lorg/jvnet/hk2/annotations/Service;";
    
    private final ServiceLocator locator;
    private final boolean verbose;
    
    private String implName;
    private boolean isAService = false;
    private final Set<String> excludes;
    
    /**
     * Creates this with the config to add to if this is a service
     * @param locator
     * @param verbose true if we should print out any service we are binding
     * @param excludes The set of implementations to NOT add to the locator
     */
    public ClassVisitorImpl(ServiceLocator locator, boolean verbose, Set<String> excludes) {
        super(Opcodes.ASM7);
        
        this.locator = locator;
        this.verbose = verbose;
        this.excludes = excludes;
    }

    /* (non-Javadoc)
     * @see org.objectweb.asm.ClassVisitor#visit(int, int, java.lang.String, java.lang.String, java.lang.String, java.lang.String[])
     */
    @Override
    public void visit(int version,
            int access,
            String name,
            String signature,
            String superName,
            String[] interfaces) {
        implName = name.replace("/", ".");
    }

    /* (non-Javadoc)
     * @see org.objectweb.asm.ClassVisitor#visitAnnotation(java.lang.String, boolean)
     */
    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        if (!visible) return null;
        
        if (SERVICE_CLASS_FORM.equals(desc)) {
            isAService = true;
        }
        
        return null;
    }

    /* (non-Javadoc)
     * @see org.objectweb.asm.ClassVisitor#visitEnd()
     */
    @Override
    public void visitEnd() {
        if (!isAService) return;
        if (excludes.contains(implName)) return;
        
        Class<?> implClass = null;
        try {
            implClass = Class.forName(implName);
        }
        catch (Throwable th) {
            System.out.println("HK2Runner could not classload service " + implName + ", skipping...");
            return;
        }
        
        List<ActiveDescriptor<?>> added = ServiceLocatorUtilities.addClasses(locator, implClass);
        
        if (verbose && !added.isEmpty()) {
            System.out.println("HK2Runner bound service " + added.get(0));
        }
    }
}
