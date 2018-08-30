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

package org.jvnet.testing.hk2mockito.internal;

import java.lang.reflect.Type;
import org.jvnet.hk2.annotations.Service;
import org.mockito.MockSettings;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

/**
 * A factory class used to create various objects.
 *
 * @author Sharmarke Aden
 */
@Service
public class ObjectFactory {

    public Object newSpy(Object instance) {
        return spy(instance);
    }

    public Object newMock(Class<?> type, MockSettings settings) {
        return mock(type, settings);
    }

    public MockitoCacheKey newKey(Type type, Object value) {
        return new MockitoCacheKey(type, value);
    }
}
