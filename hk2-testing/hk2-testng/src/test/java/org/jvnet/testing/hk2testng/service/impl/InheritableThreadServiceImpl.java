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

package org.jvnet.testing.hk2testng.service.impl;

import org.glassfish.hk2.api.InheritableThread;
import org.jvnet.hk2.annotations.Service;
import org.jvnet.testing.hk2testng.service.InheritableThreadService;

/**
 * @author jwells
 *
 */
@Service
@InheritableThread
public class InheritableThreadServiceImpl implements InheritableThreadService {
    private final long threadId;

    private InheritableThreadServiceImpl() {
        threadId = Thread.currentThread().getId();
    }

    /* (non-Javadoc)
     * @see org.jvnet.testing.hk2testng.service.InheritableThreadService#getId()
     */
    @Override
    public long getId() {
        return threadId;
    }

}
