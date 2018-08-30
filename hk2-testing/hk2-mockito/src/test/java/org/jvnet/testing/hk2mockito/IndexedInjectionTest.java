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
import org.jvnet.testing.hk2mockito.fixture.BasicGreetingService;
import org.jvnet.testing.hk2mockito.fixture.service.IndexedInjectionGreetingService;
import org.jvnet.testing.hk2testng.HK2;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author Sharmarke Aden
 */
@HK2
public class IndexedInjectionTest {

    @SUT
    @Inject
    IndexedInjectionGreetingService sut;
    @SC(0)
    @Inject
    BasicGreetingService collaborator1;
    @SC(1)
    @Inject
    BasicGreetingService collaborator2;

    @BeforeMethod
    public void init() {
        reset(sut, collaborator1, collaborator2);
    }

    @BeforeClass
    public void verifyInjection() {
        assertThat(sut).isNotNull();
        assertThat(collaborator1).isNotNull();
        assertThat(collaborator2).isNotNull();
        assertThat(mockingDetails(sut).isSpy()).isTrue();
        assertThat(mockingDetails(collaborator1).isSpy()).isTrue();
        assertThat(mockingDetails(collaborator2).isSpy()).isTrue();
    }

    @Test
    public void callToGreetShouldCallCollboratorGreet() {
        String greeting = "Hello!Hello!";

        String result = sut.greet();

        assertThat(result).isEqualTo(greeting);
        verify(sut).greet();
        verify(collaborator1).greet();
        verify(collaborator2).greet();
    }

}
