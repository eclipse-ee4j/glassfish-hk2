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

import java.util.List;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.jvnet.hk2.osgiadapter.FelixPrettyPrinter.prettyPrintExceptionMessage;

public class FelixPrettyPrinterTest {

    @Test
    public void testFormatting() {
        String src = "org.osgi.framework.BundleException:"
            + " Unable to resolve org.glassfish.main.webservices.connector [207](R 207.0):"
            + " missing requirement [org.glassfish.main.webservices.connector [207](R 207.0)] osgi.wiring.package;"
            + " (&(osgi.wiring.package=jakarta.xml.ws)(version>=3.0.0)(!(version>=4.0.0))) [caused by:"
            + " Unable to resolve org.glassfish.metro.webservices-api-osgi [236](R 236.0):"
            + " missing requirement [org.glassfish.metro.webservices-api-osgi [236](R 236.0)] osgi.wiring.package;"
            + " (&(osgi.wiring.package=jakarta.xml.bind)(version>=3.0.0)(!(version>=4.0.0)))]"
            + " Unresolved requirements: [[org.glassfish.main.webservices.connector [207](R 207.0)] osgi.wiring.package;"
            + " (&(osgi.wiring.package=jakarta.xml.ws)(version>=3.0.0)(!(version>=4.0.0)))]";
        String message = FelixPrettyPrinter.prettyPrintExceptionMessage(src);
        assertThat(message,
            equalTo(
                "Unable to resolve\n"
                    + "    org.glassfish.main.webservices.connector [207]\n"
                    + "    missing requirement\n"
                    + "        &(package = jakarta.xml.ws) (version >= 3.0.0) (!(version >= 4.0.0))\n"
                    + "        caused by:\n"
                    + "            Unable to resolve\n"
                    + "                org.glassfish.metro.webservices-api-osgi [236]\n"
                    + "                missing requirement\n"
                    + "                    &(package = jakarta.xml.bind) (version >= 3.0.0) (!(version >= 4.0.0)))]\n"));

        assertThat(FelixPrettyPrinter.findBundleIds(message), contains(207L, 236L));
        assertThat(FelixPrettyPrinter.findBundleIds(src), contains(207L, 236L));
    }

    @Test
    public void testWeld() {
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
        assertThat(FelixPrettyPrinter.findBundleIds(text), contains(41L, 291L));
    }

    @Test
    public void testFelix() {
        String src = FelixPrettyPrinter.prettyPrintExceptionMessage("  Unable to resolve"
            + " org.apache.felix.scr [304](R 304.0):"
            + " missing requirement [org.apache.felix.scr [304](R 304.0)] osgi.wiring.package;"
            + " (&(osgi.wiring.package=org.osgi.framework)(version>=1.10.0)(!(version>=2.0.0)))"
            + " Unresolved requirements: [[org.apache.felix.scr [304](R304.0)] osgi.wiring.package;"
            + " (&(osgi.wiring.package=org.osgi.framework)(version>=1.10.0)(!(version>=2.0.0)))]\n"
            + "at org.apache.felix.framework.Felix.resolveBundleRevision(Felix.java:4398) ");
        String message = FelixPrettyPrinter.prettyPrintExceptionMessage(src);
        assertThat(message,
            stringContainsInOrder("Unable to resolve\n", "org.apache.felix.scr [304]\n", "missing requirement\n"));

        List<Long> ids = FelixPrettyPrinter.findBundleIds(message);
        assertThat(ids, contains(304L));
    }
}
