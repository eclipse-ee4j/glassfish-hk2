/*
 * Copyright (c) 2019 Roughdot.nl and/or its affiliates. All rights reserved.
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

package org.jvnet.testing.hk2mockito.fixture.service;

import org.jvnet.hk2.annotations.Service;
import org.jvnet.testing.hk2mockito.fixture.BasicGreetingService;
import org.jvnet.testing.hk2mockito.fixture.NamedGreetingService;

import javax.inject.Inject;

/**
 *
 * @author Attila Houtkooper
 */
@Service
public class MultipleConstructorInjectionService {

    public BasicGreetingService basicGreetingService;
    public NamedGreetingService namedGreetingService;

    @Inject
    MultipleConstructorInjectionService(BasicGreetingService basicGreetingService, NamedGreetingService namedGreetingService) {
        this.basicGreetingService = basicGreetingService;
        this.namedGreetingService = namedGreetingService;
    }
}
