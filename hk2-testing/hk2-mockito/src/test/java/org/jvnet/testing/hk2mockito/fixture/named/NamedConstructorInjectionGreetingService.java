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

package org.jvnet.testing.hk2mockito.fixture.named;

import javax.inject.Inject;
import javax.inject.Named;
import org.jvnet.hk2.annotations.Service;
import org.jvnet.testing.hk2mockito.fixture.NamedGreetingService;

/**
 *
 * @author Sharmarke Aden
 */
@Service
public class NamedConstructorInjectionGreetingService {

    private final NamedGreetingService collaborator;

    @Inject
    NamedConstructorInjectionGreetingService(@Named("test") NamedGreetingService collaborator) {
        this.collaborator = collaborator;
    }

    public String greet() {
        return collaborator.greet();
    }

}
