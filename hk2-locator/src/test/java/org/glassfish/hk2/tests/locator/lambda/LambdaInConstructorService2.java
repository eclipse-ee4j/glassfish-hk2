/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.tests.locator.lambda;

import java.util.Arrays;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;

import javax.inject.Singleton;

/**
 * @author jwells
 *
 */
@Singleton
public class LambdaInConstructorService2 {
    private final int value;
    
    public LambdaInConstructorService2(@LambdaInjection Supplier<Integer> zeroSupplier) {
        final List<Integer> intList = Arrays.asList(1,2,3);
        value = doReduce(intList, (Integer a, Integer b) -> zeroSupplier.get() + intList.get(0) + a + b);
    }
    
    private int doReduce(final List<Integer> integers, final BinaryOperator<Integer> workFunc) {
        return integers.stream().reduce(0, workFunc);
    }
    
    public int getValue() { return value; }

}
