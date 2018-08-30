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

package org.glassfish.hk2.runlevel.utilities;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.runlevel.RunLevel;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * Run level service related utilities.
 *
 * @author tbeerbower
 */
public class Utilities {
    /**
     * Get the run level value from the metadata of the given descriptor.
     *
     * @param descriptor  the descriptor to get the run level for
     *
     * @return the run level
     */
    public static int getRunLevelValue(ServiceLocator locator, Descriptor descriptor) {
        boolean isReified = false;
        ActiveDescriptor<?> active = null;
        if (descriptor instanceof ActiveDescriptor) {
            active = (ActiveDescriptor<?>) descriptor;
            
            isReified = active.isReified();
            if (isReified) {
                Annotation anno = active.getScopeAsAnnotation();
                if (anno instanceof RunLevel) {
                    RunLevel runLevel = (RunLevel) anno;
                
                    return runLevel.value();
                }
            }
        }
        
        List<String> list = descriptor.getMetadata().
                get(RunLevel.RUNLEVEL_VAL_META_TAG);
        
        if (list == null || list.isEmpty()) {
            if (active != null && !isReified) {
                active = locator.reifyDescriptor(active);
                
                Annotation anno = active.getScopeAsAnnotation();
                if (anno instanceof RunLevel) {
                    RunLevel runLevel = (RunLevel) anno;
                
                    return runLevel.value();
                }
            }
            
            return RunLevel.RUNLEVEL_VAL_IMMEDIATE;
        }
        
        return Integer.parseInt(list.get(0));
    }

    /**
     * Get the run level mode from the metadata of the given descriptor.
     *
     * @param descriptor  the descriptor
     *
     * @return the mode
     */
    public static int getRunLevelMode(ServiceLocator locator, Descriptor descriptor, Integer modeOverride) {
        if (modeOverride != null) return modeOverride;
        
        boolean isReified = false;
        ActiveDescriptor<?> active = null;
        if (descriptor instanceof ActiveDescriptor) {
            active = (ActiveDescriptor<?>) descriptor;
            
            isReified = active.isReified();
            if (isReified) {
                Annotation anno = active.getScopeAsAnnotation();
                if (anno instanceof RunLevel) {
                    RunLevel runLevel = (RunLevel) anno;
                
                    return runLevel.mode();
                }
            }
        }
        
        List<String> list = descriptor.getMetadata().
                get(RunLevel.RUNLEVEL_MODE_META_TAG);
        
        if (list == null || list.isEmpty()) {
            if (active != null && !isReified) {
                active = locator.reifyDescriptor(active);
                
                Annotation anno = active.getScopeAsAnnotation();
                if (anno instanceof RunLevel) {
                    RunLevel runLevel = (RunLevel) anno;
                
                    return runLevel.mode();
                }
            }
            
            return RunLevel.RUNLEVEL_MODE_VALIDATING;
        }
        
        return Integer.parseInt(list.get(0));
    }
}
