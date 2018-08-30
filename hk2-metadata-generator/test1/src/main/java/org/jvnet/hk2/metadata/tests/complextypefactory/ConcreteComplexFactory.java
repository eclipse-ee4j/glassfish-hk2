/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.jvnet.hk2.metadata.tests.complextypefactory;

import org.jvnet.hk2.annotations.Service;

/**
 * @author jwells
 *
 */
@Service
public class ConcreteComplexFactory<A, B> extends MiddleComplexFactory<SomeInterface<String>, A> {

    /* (non-Javadoc)
     * @see org.jvnet.hk2.metadata.tests.complextypefactory.InterfaceWithOneType#one()
     */
    @Override
    public A one() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.jvnet.hk2.metadata.tests.complextypefactory.InterfaceWithTwoTypes#two(java.lang.Object)
     */
    @Override
    public Integer two(A v) {
        return null;
    }
    
    public B useTheOtherType() {
        return null;
    }

}
