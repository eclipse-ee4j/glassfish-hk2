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

package org.jvnet.testing.hk2mockito;

import javax.inject.Inject;
import static org.assertj.core.api.Assertions.assertThat;
import org.jvnet.testing.hk2mockito.fixture.service.ConstructorInjectionGreetingService;
import org.jvnet.testing.hk2testng.HK2;
import static org.mockito.Mockito.mockingDetails;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author Sharmarke Aden
 */
@HK2
public class WithoutSUTTest {

    @Inject
    ConstructorInjectionGreetingService sut;


    @BeforeClass
    public void verifyInjection() {
        assertThat(sut).isNotNull();
        assertThat(mockingDetails(sut).isSpy()).isFalse();
    }

    @Test
    public void callToGreetShouldCallCollboratorGreet() {
        String greeting = "Hello!";

        String result = sut.greet();

        assertThat(result).isEqualTo(greeting);
    }

}
