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

package org.glassfish.hk2.tests.locator.arrays;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author jwells
 *
 */
@Singleton
public class ArrayInjectee {
    @Inject
    private int[] arrayOfInt;
    
    @Inject
    private List<String>[] arrayOfListString;
    
    @Inject
    private Map<String,String>[] arrayOfMapStringString;
    
    @Inject
    private SimpleService[] arrayOfSimpleService;

    /**
     * @return the arrayOfInt
     */
    int[] getArrayOfInt() {
        return arrayOfInt;
    }

    /**
     * @return the arrayOfListString
     */
    List<String>[] getArrayOfListString() {
        return arrayOfListString;
    }

    /**
     * @return the arrayOfMapStringString
     */
    Map<String, String>[] getArrayOfMapStringString() {
        return arrayOfMapStringString;
    }

    /**
     * @return the arrayOfSimpleService
     */
    SimpleService[] getArrayOfSimpleService() {
        return arrayOfSimpleService;
    }
}
