/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.classmodel.reflect.util;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.PackageAdmin;

/**
 * Created with IntelliJ IDEA.
 * User: makannan
 * Date: 3/4/13
 * Time: 8:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClassModelActivator
    implements BundleActivator {

    public void start(BundleContext context) throws Exception {
        ServiceReference ref = context.getServiceReference(PackageAdmin.class.getName());
        CommonModelRegistry.getInstance().initialize(context,
                PackageAdmin.class.cast(context.getService(ref)));
    }

    public void stop(BundleContext context)
            throws Exception {
    }
}
