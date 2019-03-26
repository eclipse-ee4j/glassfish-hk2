/*
 * Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.
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

import org.glassfish.hk2.classmodel.reflect.*;
import org.glassfish.hk2.classmodel.reflect.util.ParsingConfig;

import java.util.*;

/**
 * Implementation of an annotation model
 */
public class AnnotationTypeImpl extends InterfaceModelImpl implements AnnotationType {

    private final Set<AnnotatedElement> references = Collections.synchronizedSet(new HashSet<AnnotatedElement>());
    private final Map<String, Object> defValues = new HashMap<String, Object>();

    public AnnotationTypeImpl(String name, TypeProxy<Type> sink) {
        super(name, sink, null);
    }

    public Collection<AnnotatedElement> allAnnotatedTypes() {
        return Collections.unmodifiableSet(references);
    }

    public void addDefaultValue(String name, Object value) {
      if (org.objectweb.asm.Type.class.isInstance(value)) {
        defValues.put(name, org.objectweb.asm.Type.class.cast(value).getClassName());
      } else {
        defValues.put(name, value);
      }
    }
    
    @Override
    public Map<String, Object> getDefaultValues() {
      return Collections.unmodifiableMap(defValues);
    }
    
    Set<AnnotatedElement> getAnnotatedElements() {
        return references;
    }

    @Override
    protected void print(StringBuffer sb) {
        super.print(sb);
        sb.append(", referenced from [");
        for (AnnotatedElement a : allAnnotatedTypes()) {
            sb.append(" ").append(a.shortDesc());
        }
        sb.append("]");
    }
}
