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

import org.glassfish.hk2.classmodel.reflect.AnnotatedElement;
import org.glassfish.hk2.classmodel.reflect.AnnotationModel;
import org.glassfish.hk2.classmodel.reflect.AnnotationType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Model a annotation instance
 */
public class AnnotationModelImpl implements AnnotationModel {
    
    final AnnotationType type;
    final AnnotatedElement element;
    private final Map<String, Object> values = new HashMap<String, Object>();

    public AnnotationModelImpl(AnnotatedElement element, AnnotationType type) {
        this.type = type;
        this.element = element;
    }
    
    @Override
    public String toString() {
      return "AnnotationModel:" + type + "-" + element;
    }

    @SuppressWarnings("unchecked")
    public void addValue(String name, Object value) {
        if (null == name) {
          // check for arrayed value(s)
          name = "value";
          Object prevVal = values.get(name);
          if (null != prevVal) {
            if (Collection.class.isInstance(prevVal)) {
              ((Collection<Object>)prevVal).add(unwrap(value));
              return;
            } else {
              Collection<Object> coll = new ArrayList<Object>();
              coll.add(prevVal);
              coll.add(unwrap(value));
              value = coll;
            }
          }
        }
        values.put(name, unwrap(value));
    }
    
    private Object unwrap(Object value) {
      if (org.objectweb.asm.Type.class.isInstance(value)) {
        return org.objectweb.asm.Type.class.cast(value).getClassName();
      }
      return value;
    }

    @Override
    public Map<String, Object> getValues() {
        return Collections.unmodifiableMap(values);
    }

    @Override
    public AnnotationType getType() {
        return type;
    }

    @Override
    public AnnotatedElement getElement() {
        return element;
    }

}
