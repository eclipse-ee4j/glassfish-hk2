/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.runlevel;

import org.glassfish.hk2.api.AnnotationLiteral;
import org.glassfish.hk2.api.DuplicateServiceException;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.runlevel.internal.AsyncRunLevelContext;
import org.glassfish.hk2.runlevel.internal.RunLevelControllerImpl;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;

/**
 * Utilities for using the RunLevelService
 * 
 * @author jwells
 *
 */
public class RunLevelServiceUtilities {
    /**
     * Enables the RunLevelService in the given {@link ServiceLocator}.
     * If the {@link RunLevelContext} is already registered then
     * this method does nothing.
     * <p>
     * All services needed by the
     * RunLevelService feature are marked with {@link org.jvnet.hk2.annotations.Service} and
     * hence would be automatically picked up in environments that
     * use automatic service discovery
     * 
     * @param locator the non-null service locator to add
     * the run-level service to
     */
    public static void enableRunLevelService(ServiceLocator locator) {
        if (locator.getService(RunLevelContext.class) != null) return;
        
        try {
            ServiceLocatorUtilities.addClasses(locator, true,
                RunLevelContext.class,
                AsyncRunLevelContext.class,
                RunLevelControllerImpl.class);
        }
        catch (MultiException me) {
            if (!isDupException(me)) throw me;
        }
    }
    
    private static boolean isDupException(MultiException me) {
        boolean atLeastOne = false;
        
        for (Throwable error : me.getErrors()) {
            atLeastOne = true;
            
            if (!(error instanceof DuplicateServiceException)) return false;
        }
        
        return atLeastOne;
    }
    
    /**
     * Returns a {@link RunLevel} scope annotation with the
     * given value and RUNLEVEL_MODE_VALIDATING as the mode
     * 
     * @param value The value this RunLevel should take
     * @return A {@link RunLevel} scope annotation
     */
    public static RunLevel getRunLevelAnnotation(int value) {
        return getRunLevelAnnotation(value, RunLevel.RUNLEVEL_MODE_VALIDATING);
        
    }
    
    /**
     * Returns a {@link RunLevel} scope annotation with the
     * given value and mode
     * 
     * @param value The value this RunLevel should take
     * @param mode The mode the RunLevel should take:<UL>
     * <LI>RUNLEVEL_MODE_VALIDATING</LI>
     * <LI>RUNLEVEL_MODE_NON_VALIDATING</LI>
     * </UL>
     * @return A {@link RunLevel} scope annotation
     */
    public static RunLevel getRunLevelAnnotation(int value, int mode) {
        return new RunLevelImpl(value, mode);
    }
    
    private static class RunLevelImpl extends AnnotationLiteral<RunLevel> implements RunLevel {
        private static final long serialVersionUID = -359213687920354669L;
        
        private final int value;
        private final int mode;
        
        private RunLevelImpl(int value, int mode) {
            this.value = value;
            this.mode = mode;
        }

        /* (non-Javadoc)
         * @see org.glassfish.hk2.runlevel.RunLevel#value()
         */
        @Override
        public int value() {
            return value;
        }

        /* (non-Javadoc)
         * @see org.glassfish.hk2.runlevel.RunLevel#mode()
         */
        @Override
        public int mode() {
            return mode;
        }

        
    }

}
