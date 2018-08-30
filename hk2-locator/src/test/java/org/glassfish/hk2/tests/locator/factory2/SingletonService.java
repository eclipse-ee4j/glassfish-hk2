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

package org.glassfish.hk2.tests.locator.factory2;

import org.glassfish.hk2.api.InstantiationData;

/**
 * @author jwells
 *
 */
public class SingletonService {
    private InstantiationData data;

    /**
     * @return the data
     */
    public InstantiationData getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(InstantiationData data) {
        this.data = data;
    }

}
