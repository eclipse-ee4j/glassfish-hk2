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

package org.glassfish.hk2.tests.proxiableshared;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Global component that is managed by its own "bean manager".
 * The component is made accessible in HK2 via {@link GlobalComponentFactory}
 * and is being injected by HK2 via a simple {@link ComponentInjector} SPI.
 *
 * @author Jakub Podlesak (jakub.podlesak at oracle.com)
 */
@Singleton
public class GlobalComponent {
    /**
     * HK2 injected field. A dynamic proxy will be injected here,
     * that will unfortunately keep reference to the first HK2 locator
     * used to inject this.
     */
    @Inject
    private ReqData request;

    /**
     * Our "bean manager" implementation. All we do here
     * is we mimic CDI application scope.
     */
    public static class BeanManager {
        // ...

    }

    @Inject
    private GlobalComponent() {
        // disable instantiation
    }

    /**
     * Get me actual request name, so that i can check you have the right guy.
     *
     * @return actual request name.
     */
    public String getRequestName() {
        return request.getRequestName();
    }
    
    @Override
    public String toString() {
        return "GlobalComponent(" + System.identityHashCode(this) + ")";
    }
}
