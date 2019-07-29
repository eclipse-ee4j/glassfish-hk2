/*
 * Copyright (c) 2019 Payara Service Ltd. and/or its affiliates. All rights reserved.
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
package org.glassfish.hk2.tests.locator.optional;

import org.glassfish.hk2.api.Factory;

/**
 * A Factory that will always return null
 * @author jonathan coustick
 */
public class NullWidgetFactory implements Factory<Widget> {

    @Override
    public Widget provide() {
        return null;
    }

    @Override
    public void dispose(Widget instance) {
    }
    
}
