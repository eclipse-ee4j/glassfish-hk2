/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.tests.locator.negative.api;

import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.FactoryDescriptors;

/**
 * @author jwells
 *
 */
public class FactoryDescriptorsImpl implements FactoryDescriptors {
    private Descriptor asService;
    private Descriptor asFactory;
    
    /**
     * Sets the asService descriptor
     * @param d The descriptor to use for the asService
     */
    public void setAsService(Descriptor d) {
        asService = d;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.FactoryDescriptors#getFactoryAsService()
     */
    @Override
    public Descriptor getFactoryAsAService() {
        return asService;
    }
    
    /**
     * Sets the asFactory descriptor
     * @param d The descriptor to use for the asFactory
     */
    public void setAsFactory(Descriptor d) {
        asFactory = d;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.FactoryDescriptors#getFactoryAsAFactory()
     */
    @Override
    public Descriptor getFactoryAsAFactory() {
        return asFactory;
    }

}
