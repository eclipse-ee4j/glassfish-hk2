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

package org.jvnet.hk2.testing.test;

import org.junit.Assert;
import org.junit.Test;
import org.jvnet.hk2.testing.junit.HK2Runner;
import org.jvnet.hk2.testing.junit.annotations.Classes;

/**
 * @author jwells
 *
 */
@Classes(UnmarkedAndNotInhabitantService.class)
public class ClassesAnnotationTest extends HK2Runner {
    /**
     * The unmarked and not in any inhabitant file
     * service is available (and not via JIT either)
     */
    @Test
    public void testClassesWorks() {
        Assert.assertNotNull(testLocator.getService(UnmarkedAndNotInhabitantContract.class));
    }

}
