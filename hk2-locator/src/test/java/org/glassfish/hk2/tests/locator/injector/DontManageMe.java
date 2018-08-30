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

package org.glassfish.hk2.tests.locator.injector;

import javax.inject.Inject;

import org.jvnet.hk2.annotations.Optional;

/**
 * This class will be constructed by create, injected by the inject method,
 * post constructed and pre destroyed, but will never be managed by
 * ServiceLocator
 * 
 * @author jwells
 *
 */
public class DontManageMe {
    private final SimpleService byConstructor;
    
    @Inject
    private SimpleService byField;
    private SimpleService byMethod;
    private SpecialService saturdayNight;
    private UnknownService unknown;
    
    private SimpleService secondMethod;
    private SpecialService secondSpecial;
    
    private boolean postConstructCalled;
    private boolean preDestroyCalled;
    
    @Inject
    private DontManageMe(SimpleService byConstructor) {
        this.byConstructor = byConstructor;
    }
    
    @Inject
    private void setByMethod(SimpleService byMethod) {
        this.byMethod = byMethod;
    }
    
    @Special
    private void setToSpecial(SpecialService special) {
        saturdayNight = special;
    }
    
    @Inject
    private void setSpecialAndNormal(SimpleService byMethod, @Special SpecialService special, @Optional UnknownService unknown) {
        this.secondMethod = byMethod;
        this.secondSpecial = special;
        this.unknown = unknown;
    }
    
    @SuppressWarnings("unused")
    private void postConstruct() {
        postConstructCalled = true;
    }
    
    @SuppressWarnings("unused")
    private void preDestroy() {
        preDestroyCalled = true;
    }

    /**
     * @return the byConstructor
     */
    public SimpleService getByConstructor() {
        return byConstructor;
    }

    /**
     * @return the byField
     */
    public SimpleService getByField() {
        return byField;
    }

    /**
     * @return the byMethod
     */
    public SimpleService getByMethod() {
        return byMethod;
    }
    
    public SpecialService getSpecialService() {
        return saturdayNight;
    }
    
    public SimpleService getSecondMethod() {
        return secondMethod;
    }
    
    public SpecialService getSecondSpecial() {
        return secondSpecial;
    }
    
    public UnknownService getUnknown() {
        return unknown;
    }

    /**
     * @return the postConstructCalled
     */
    public boolean isPostConstructCalled() {
        return postConstructCalled;
    }

    /**
     * @return the preDestroyCalled
     */
    public boolean isPreDestroyCalled() {
        return preDestroyCalled;
    }
}
