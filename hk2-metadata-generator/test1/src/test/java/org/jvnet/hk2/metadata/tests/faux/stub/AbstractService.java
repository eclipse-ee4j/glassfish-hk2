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

package org.jvnet.hk2.metadata.tests.faux.stub;

import javax.inject.Inject;
import javax.inject.Named;

import org.glassfish.hk2.api.PerLookup;
import org.glassfish.hk2.api.Rank;
import org.glassfish.hk2.utilities.Stub;
import org.jvnet.hk2.annotations.Contract;
import org.jvnet.hk2.metadata.tests.InhabitantsGeneratorTest;
import org.jvnet.hk2.metadata.tests.stub.RandomBean;
import org.jvnet.hk2.metadata.tests.stub.impl.AbstractBaseService;

/**
 * @author jwells
 *
 */
@Contract @Rank(1) @Stub
public abstract class AbstractService extends AbstractBaseService {
    @Inject
    private RandomBeanStub randomBeanStub;
    
    @Contract @Rank(1) @Stub
    public static abstract class RandomBeanStub implements RandomBean {
    }
    
    public RandomBeanStub getRandomBeanStub() {
        return randomBeanStub;
    }
    
    @Stub(Stub.Type.VALUES) @Named
    public static abstract class NamedBeanStub implements NamedBean {
        @Override
        public String getName() {
            return "NamedBeanStub";
        }
    }
    
    @Stub @Named(InhabitantsGeneratorTest.ALICE) @PerLookup
    public static abstract class AliceBeanStub implements NamedBean {
        @Override
        public String getName() {
            return InhabitantsGeneratorTest.ALICE;
        }
    }

}
