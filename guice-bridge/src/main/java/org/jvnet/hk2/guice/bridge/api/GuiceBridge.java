/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.jvnet.hk2.guice.bridge.api;

import com.google.inject.internal.Annotations;
import jakarta.inject.Qualifier;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceLocator;
import org.jvnet.hk2.guice.bridge.internal.GuiceBridgeImpl;

/**
 * This class can be used to initialize a ServiceLocator for use with
 * the Guice/HK2 Bridge
 *
 * @author jwells
 *
 */
public abstract class GuiceBridge {
    
    private final static GuiceBridge INSTANCE = new GuiceBridgeImpl();
    
    /**
     * Configure Guice to allow for Jakarta annotations.
     * This function must be called before any annotation bindings
     * are created.
     */
    @SuppressWarnings("ResultOfObjectAllocationIgnored")
    public static void allowJakartaInject() {
        try {
            //Load the class so reflection will work
            new Annotations();
            //Get the list of binding annotations, which uses javax
            Field bindingAnnotations = Annotations.class.getDeclaredField("bindingAnnotationChecker");
            bindingAnnotations.setAccessible(true);
            //Get the AnnotationChecker class, which is the type of the above field and is private
            Class<?> annotationCheckerClass = Class.forName("com.google.inject.internal.Annotations$AnnotationChecker");
            //Access the annotation checker value
            Object annotationChecker = annotationCheckerClass.cast(bindingAnnotations.get(null));
            //The field, which is actually a collection containing jakarta.inject.Qualifier
            Field annotationTypesField = annotationChecker.getClass().getDeclaredField("annotationTypes");
            annotationTypesField.setAccessible(true);
            //Create a new List, which contains jakarta.inject.Qualifer as well as all the old ones.
            List<Class<? extends Annotation>> annotationsList = new ArrayList<>(3);
            annotationsList.add(Qualifier.class);
            annotationsList.addAll((List<Class<? extends Annotation>>) annotationTypesField.get(annotationChecker));
            //Set the value of the field to the new list
            annotationTypesField.set(annotationChecker, annotationsList);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException | ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static GuiceBridge getGuiceBridge() {
        return INSTANCE;
    }
    
    /**
     * This method will initialize the given service locator for use with the Guice/HK2
     * bridge.  It adds into the service locator an implementation of GuiceIntoHK2Bridge
     * and also the custom scope needed for Guice services.  This method is idempotent,
     * in that if these services have already been added to the service locator
     * they will not be added again
     * 
     * @param locator A non-null locator to use with the Guice/HK2 bridge
     * @throws MultiException On failure
     */
    public abstract void initializeGuiceBridge(ServiceLocator locator) throws MultiException;
}
