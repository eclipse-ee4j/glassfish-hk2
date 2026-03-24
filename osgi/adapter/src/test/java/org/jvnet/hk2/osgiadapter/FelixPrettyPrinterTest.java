/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation
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

package org.jvnet.hk2.osgiadapter;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.jvnet.hk2.osgiadapter.FelixPrettyPrinter.prettyPrintExceptionMessage;

public class FelixPrettyPrinterTest {

    @Test
    public void test() {
        String text = prettyPrintExceptionMessage(
            "org.osgi.framework.BundleException:"
            + " Unable to resolve org.glassfish.main.web.weld-integration [41](R 41.0):"
            + " missing requirement [org.glassfish.main.web.weld-integration [41](R 41.0)] osgi.wiring.package;"
            + " (&(osgi.wiring.package=jakarta.faces.application)(version>=4.1.0)(!(version>=5.0.0)))"
            + " [caused by: Unable to resolve org.glassfish.jakarta.faces [291](R 291.0):"
            + " missing requirement [org.glassfish.jakarta.faces [291](R 291.0)] osgi.wiring.package;"
            + " (&(osgi.wiring.package=jakarta.enterprise.inject)(version>=4.1.0)(!(version>=5.0.0)))]"
            + " Unresolved requirements: [[org.glassfish.main.web.weld-integration [41](R 41.0)]"
            + " osgi.wiring.package;"
            + " (&(osgi.wiring.package=jakarta.faces.application)(version>=4.1.0)(!(version>=5.0.0)))]");
        assertThat(text,
            stringContainsInOrder("Unable to resolve", "org.glassfish.main.web.weld-integration", "missing requirement",
                "jakarta.faces.application", "caused by:", "Unable to resolve", "org.glassfish.jakarta.faces",
                "missing requirement", "jakarta.enterprise.inject", "(version >= 4.1.0) (!(version >= 5.0.0))"));
    }

}
