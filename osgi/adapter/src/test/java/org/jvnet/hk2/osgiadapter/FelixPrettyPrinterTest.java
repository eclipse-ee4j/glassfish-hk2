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

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.jvnet.hk2.osgiadapter.FelixPrettyPrinter.findBundleIds;
import static org.jvnet.hk2.osgiadapter.FelixPrettyPrinter.prettyPrintExceptionMessage;

/**
 * Expected values are written as text blocks, because the indentation is the thing under test and concatenated literals hide it.
 */
public class FelixPrettyPrinterTest {

    private static final String FELIX_SCR_MESSAGE = "  Unable to resolve"
        + " org.apache.felix.scr [304](R 304.0):"
        + " missing requirement [org.apache.felix.scr [304](R 304.0)] osgi.wiring.package;"
        + " (&(osgi.wiring.package=org.osgi.framework)(version>=1.10.0)(!(version>=2.0.0)))"
        + " Unresolved requirements: [[org.apache.felix.scr [304](R304.0)] osgi.wiring.package;"
        + " (&(osgi.wiring.package=org.osgi.framework)(version>=1.10.0)(!(version>=2.0.0)))]\n"
        + "at org.apache.felix.framework.Felix.resolveBundleRevision(Felix.java:4398) ";

    @Test
    public void testFormatting() {
        String source = "org.osgi.framework.BundleException:"
            + " Unable to resolve org.glassfish.main.webservices.connector [207](R 207.0):"
            + " missing requirement [org.glassfish.main.webservices.connector [207](R 207.0)] osgi.wiring.package;"
            + " (&(osgi.wiring.package=jakarta.xml.ws)(version>=3.0.0)(!(version>=4.0.0))) [caused by:"
            + " Unable to resolve org.glassfish.metro.webservices-api-osgi [236](R 236.0):"
            + " missing requirement [org.glassfish.metro.webservices-api-osgi [236](R 236.0)] osgi.wiring.package;"
            + " (&(osgi.wiring.package=jakarta.xml.bind)(version>=3.0.0)(!(version>=4.0.0)))]"
            + " Unresolved requirements: [[org.glassfish.main.webservices.connector [207](R 207.0)] osgi.wiring.package;"
            + " (&(osgi.wiring.package=jakarta.xml.ws)(version>=3.0.0)(!(version>=4.0.0)))]";

        String message = prettyPrintExceptionMessage(source);
        assertThat(message, equalTo("""
                org.osgi.framework.BundleException:
                Unable to resolve
                    org.glassfish.main.webservices.connector [207]
                    missing requirement
                        package = jakarta.xml.ws & version >= 3.0.0 & !(version >= 4.0.0)
                        caused by:
                            Unable to resolve
                                org.glassfish.metro.webservices-api-osgi [236]
                                missing requirement
                                    package = jakarta.xml.bind & version >= 3.0.0 & !(version >= 4.0.0)
                """));

        assertThat(findBundleIds(message), contains(207L, 236L));
        assertThat(findBundleIds(source), contains(207L, 236L));
    }

    @Test
    public void testWeld() {
        String message = prettyPrintExceptionMessage("org.osgi.framework.BundleException:"
            + " Unable to resolve org.glassfish.main.web.weld-integration [41](R 41.0):"
            + " missing requirement [org.glassfish.main.web.weld-integration [41](R 41.0)] osgi.wiring.package;"
            + " (&(osgi.wiring.package=jakarta.faces.application)(version>=4.1.0)(!(version>=5.0.0)))"
            + " [caused by: Unable to resolve org.glassfish.jakarta.faces [291](R 291.0):"
            + " missing requirement [org.glassfish.jakarta.faces [291](R 291.0)] osgi.wiring.package;"
            + " (&(osgi.wiring.package=jakarta.enterprise.inject)(version>=4.1.0)(!(version>=5.0.0)))]"
            + " Unresolved requirements: [[org.glassfish.main.web.weld-integration [41](R 41.0)]"
            + " osgi.wiring.package;"
            + " (&(osgi.wiring.package=jakarta.faces.application)(version>=4.1.0)(!(version>=5.0.0)))]");

        assertThat(message,
            stringContainsInOrder("Unable to resolve", "org.glassfish.main.web.weld-integration", "missing requirement",
                "jakarta.faces.application", "caused by:", "Unable to resolve", "org.glassfish.jakarta.faces",
                "missing requirement", "jakarta.enterprise.inject", "version >= 4.1.0 & !(version >= 5.0.0)"));
        assertThat(findBundleIds(message), contains(41L, 291L));
    }

    @Test
    public void testFelix() {
        String message = prettyPrintExceptionMessage(FELIX_SCR_MESSAGE);
        assertThat(message, equalTo("""
                Unable to resolve
                    org.apache.felix.scr [304]
                    missing requirement
                        package = org.osgi.framework & version >= 1.10.0 & !(version >= 2.0.0)
                """));

        assertThat(findBundleIds(message), contains(304L));
    }

    /**
     * A requirement on an execution environment used to be reported under the wrong module, because the parser searched forward for a
     * package requirement without staying inside the clause it was describing.
     */
    @Test
    public void testExecutionEnvironment() {
        String message = prettyPrintExceptionMessage("org.osgi.framework.BundleException:"
            + " Unable to resolve org.glassfish.main.core [12](R 12.0):"
            + " missing requirement [org.glassfish.main.core [12](R 12.0)] osgi.ee;"
            + " (&(osgi.ee=JavaSE)(version=21))");

        assertThat(message, equalTo("""
                org.osgi.framework.BundleException:
                Unable to resolve
                    org.glassfish.main.core [12]
                    missing requirement
                        osgi.ee = JavaSE & version = 21
                """));
    }

    @Test
    public void testFragmentHost() {
        String message = prettyPrintExceptionMessage("Unable to resolve org.hibernate.validator.cdi [263](R 263.0):"
            + " missing requirement [org.hibernate.validator.cdi [263](R 263.0)] osgi.wiring.host;"
            + " (&(osgi.wiring.host=org.hibernate.validator)(bundle-version>=9.1.0))");

        assertThat(message, equalTo("""
                Unable to resolve
                    org.hibernate.validator.cdi [263]
                    missing requirement
                        host = org.hibernate.validator & bundle-version >= 9.1.0
                """));
    }

    @Test
    public void testRequirementWithoutVersionRange() {
        String message = prettyPrintExceptionMessage("Unable to resolve org.glassfish.main.foo [7](R 7.0):"
            + " missing requirement [org.glassfish.main.foo [7](R 7.0)] osgi.wiring.package;"
            + " (osgi.wiring.package=jakarta.validation)");

        assertThat(message, equalTo("""
                Unable to resolve
                    org.glassfish.main.foo [7]
                    missing requirement
                        package = jakarta.validation
                """));
    }

    /**
     * Nesting inside a filter keeps its parentheses, so that the grouping the resolver meant is still visible once the operators move
     * between their operands.
     */
    @Test
    public void testNestedDisjunctionKeepsItsGrouping() {
        String message = prettyPrintExceptionMessage("Unable to resolve org.glassfish.main.foo [7](R 7.0):"
            + " missing requirement [org.glassfish.main.foo [7](R 7.0)] osgi.wiring.package;"
            + " (&(osgi.wiring.package=org.bar)(|(version=1.0.0)(version=2.0.0)))");

        assertThat(message, containsString("package = org.bar & (version = 1.0.0 | version = 2.0.0)"));
    }

    /**
     * A message the parser does not fully understand must never be replaced by a shorter one that drops what it could not read.
     */
    @Test
    public void testUnrecognisedMessageKeepsTheOriginal() {
        String source = "Unable to resolve org.glassfish.main.foo [7](R 7.0): something the resolver has never said before";

        assertThat(prettyPrintExceptionMessage(source), containsString(source));
    }

    /**
     * Formatting an already formatted message is a mistake, but it still may not lose anything.
     */
    @Test
    public void testAlreadyFormattedMessageIsNotDestroyed() {
        String once = prettyPrintExceptionMessage(FELIX_SCR_MESSAGE);

        assertThat(prettyPrintExceptionMessage(once), containsString(once));
    }

    /**
     * Two failures are only nested when the resolver said one caused the other. Without a "caused by" they are siblings.
     */
    @Test
    public void testSiblingFailuresAreNotNested() {
        String message = prettyPrintExceptionMessage("Unable to resolve A [1](R 1.0):"
            + " missing requirement [A [1](R 1.0)] osgi.wiring.package; (osgi.wiring.package=p.one)"
            + " Unable to resolve B [2](R 2.0):"
            + " missing requirement [B [2](R 2.0)] osgi.wiring.package; (osgi.wiring.package=p.two)");

        assertThat(message, equalTo("""
                Unable to resolve
                    A [1]
                    missing requirement
                        package = p.one
                Unable to resolve
                    B [2]
                    missing requirement
                        package = p.two
                """));
    }

    @Test
    public void testNullAndEmptyMessage() {
        assertThat(prettyPrintExceptionMessage(null), nullValue());
        assertThat(prettyPrintExceptionMessage(""), equalTo(""));
        assertThat(findBundleIds(null), empty());
        assertThat(findBundleIds(""), empty());
    }
}