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

package org.jvnet.hk2.osgiadapter;

import static org.jvnet.hk2.osgiadapter.ServiceLocatorHk2MainTest.*;
import static org.ops4j.pax.exam.CoreOptions.cleanCaches;
import static org.ops4j.pax.exam.CoreOptions.frameworkProperty;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.provision;
import static org.ops4j.pax.exam.CoreOptions.systemPackage;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import static org.ops4j.pax.exam.CoreOptions.workingDirectory;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.ProxyCtl;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.hk2.utilities.HK2LoaderImpl;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import com.oracle.sdp.management.InstallSDPService;
import com.oracle.test.bar.Bar;
import com.oracle.test.bar.BarContract;
import com.oracle.test.contracts.FooContract;
import com.sun.enterprise.module.ModulesRegistry;
import com.sun.enterprise.module.bootstrap.Main;
import com.sun.enterprise.module.bootstrap.StartupContext;

/**
 * @author jwells
 *
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class OSGiTest {

    @Inject
    private BundleContext bundleContext;

    @Configuration
    public Option[] configuration() {
        String projectVersion = System.getProperty("project.version");
        String asmVersion = System.getProperty("asm.version");
        return options(
                workingDirectory(System.getProperty("basedir") + "/target/wd"),
                systemProperty("java.io.tmpdir").value(System.getProperty("basedir") + "/target"),
                systemProperty("pax.exam.osgi.unresolved.fail").value("true"),
                frameworkProperty("org.osgi.framework.storage").value(System.getProperty("basedir") + "/target/felix"),
                systemPackage("sun.misc"),
                systemPackage("javax.net.ssl"),
                systemPackage("javax.xml.bind"),
                systemPackage("javax.xml.bind.annotation"),
                systemPackage("javax.xml.bind.annotation.adapters"),
                systemPackage("javax.xml.namespace"),
                systemPackage("javax.xml.parsers"),
                systemPackage("javax.xml.stream"),
                systemPackage("javax.xml.stream.events"),
                systemPackage("javax.xml.transform"),
                systemPackage("javax.xml.transform.stream"),
                systemPackage("javax.xml.validation"),
                systemPackage("javax.script"),
                systemPackage("javax.management"),
                systemPackage("org.w3c.dom"),
                systemPackage("org.xml.sax"),
                junitBundles(),
                provision(mavenBundle().groupId(HK2_GROUP_ID).artifactId("hk2-utils").version(projectVersion).startLevel(4)),
                provision(mavenBundle().groupId(HK2_GROUP_ID).artifactId("hk2-api").version(projectVersion).startLevel(4)),
                provision(mavenBundle().groupId(HK2_GROUP_ID).artifactId("hk2-runlevel").version(projectVersion).startLevel(4)),
                provision(mavenBundle().groupId(HK2_GROUP_ID).artifactId("hk2-core").version(projectVersion).startLevel(4)),
                provision(mavenBundle().groupId(HK2_GROUP_ID).artifactId("hk2-locator").version(projectVersion).startLevel(4)),
                provision(mavenBundle().groupId(HK2_EXT_GROUP_ID).artifactId("jakarta.inject").version(projectVersion).startLevel(4)),
                provision(mavenBundle().groupId("org.javassist").artifactId("javassist").versionAsInProject().startLevel(4)),
                provision(mavenBundle().groupId(ASM_GROUP_ID).artifactId("asm").version(asmVersion).startLevel(4)),
                provision(mavenBundle().groupId(ASM_GROUP_ID).artifactId("asm-analysis").version(asmVersion).startLevel(4)),
                provision(mavenBundle().groupId(ASM_GROUP_ID).artifactId("asm-commons").version(asmVersion).startLevel(4)),
                provision(mavenBundle().groupId(ASM_GROUP_ID).artifactId("asm-tree").version(asmVersion).startLevel(4)),
                provision(mavenBundle().groupId(ASM_GROUP_ID).artifactId("asm-util").version(asmVersion).startLevel(4)),
                provision(mavenBundle().groupId(HK2_EXT_GROUP_ID).artifactId("aopalliance-repackaged").version(projectVersion).startLevel(4)),
                provision(mavenBundle().groupId(HK2_GROUP_ID).artifactId("osgi-resource-locator").version("1.0.1").startLevel(4)),
                provision(mavenBundle().groupId(HK2_GROUP_ID).artifactId("class-model").version(projectVersion).startLevel(4)),
                provision(mavenBundle().groupId(HK2_GROUP_ID).artifactId("osgi-adapter").version(projectVersion).startLevel(1)),
                provision(mavenBundle().groupId(HK2_GROUP_ID).artifactId("test-module-startup").version(projectVersion).startLevel(4)),
                provision(mavenBundle().groupId(HK2_GROUP_ID).artifactId("contract-bundle").version(projectVersion).startLevel(4)),
                provision(mavenBundle().groupId(HK2_GROUP_ID).artifactId("no-hk2-bundle").version(projectVersion).startLevel(4)),
                provision(mavenBundle().groupId(HK2_GROUP_ID).artifactId("sdp-management-bundle").version(projectVersion).startLevel(4)),
                provision(mavenBundle().groupId("jakarta.annotation").artifactId("jakarta.annotation-api").versionAsInProject()),
                provision(mavenBundle().groupId("jakarta.el").artifactId("jakarta.el-api").versionAsInProject()),
                provision(mavenBundle().groupId("javax.validation").artifactId("validation-api").versionAsInProject()),
                provision(mavenBundle().groupId("org.hibernate.validator").artifactId("hibernate-validator").versionAsInProject()),
                provision(mavenBundle().groupId("com.fasterxml").artifactId("classmate").versionAsInProject()),
                provision(mavenBundle().groupId("org.jboss.logging").artifactId("jboss-logging").versionAsInProject()),
                // systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level")
                //      .value("DEBUG"),
                cleanCaches()
        // systemProperty("com.sun.enterprise.hk2.repositories").value(cacheDir.toURI().toString()),
        // vmOption(
        // "-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005" )
        );
    }

    /**
     * Tests that late installation properly removes
     * services
     *
     * @throws Throwable
     */
    @Test
    public void testLateBundleInstallation() throws Throwable {
        ServiceLocator serviceLocator = getMainServiceLocator();

        ServiceTracker hk2Tracker = new ServiceTracker(
                this.bundleContext,
                InstallSDPService.class.getName(),
                null);
        hk2Tracker.open();
        InstallSDPService installationService = (InstallSDPService)
                hk2Tracker.waitForService(0);
        hk2Tracker.close();
        FooContract fooC = serviceLocator.getService(FooContract.class);
        Assert.assertNull(fooC);

        /**
         * First install and uninstall
         */
        installationService.install();
        List<ActiveDescriptor<?>> descriptors =
                serviceLocator.getDescriptors(
                        BuilderHelper.createContractFilter(
                                FooContract.class.getName()));
        Assert.assertEquals(1, descriptors.size());
        Assert.assertTrue(installationService.uninstall());

        descriptors =
                serviceLocator.getDescriptors(
                        BuilderHelper.createContractFilter(
                                FooContract.class.getName()));
        Assert.assertEquals(0, descriptors.size());
        fooC = serviceLocator.getService(FooContract.class);
        Assert.assertNull(fooC);

        /**
         * then install again
         */
        installationService.install();
        descriptors =
                serviceLocator.getDescriptors(
                        BuilderHelper.createContractFilter(
                                FooContract.class.getName()));
        Assert.assertEquals(1, descriptors.size());
        fooC = serviceLocator.getService(FooContract.class);
        Assert.assertNotNull(fooC);
    }

    /**
     * See https://java.net/jira/browse/HK2-163
     *
     * The problem was that the interface had no access to hk2 at
     * all, which caused classloading problems
     *
     * @throws Throwable
     */
    @Test
    public void testProxyInterfaceWithNoAccessToHK2() throws Throwable {
        ServiceLocator serviceLocator = getMainServiceLocator();

        Descriptor addMe = BuilderHelper.link(Bar.class.getName()).
                to(BarContract.class.getName()).
                in(Singleton.class.getName()).
                proxy().
                andLoadWith(new HK2LoaderImpl(Bar.class.getClassLoader())).
                build();

        ActiveDescriptor<?> added = ServiceLocatorUtilities.addOneDescriptor(serviceLocator, addMe);
        try {
            BarContract contract = serviceLocator.getService(BarContract.class);
            
            Assert.assertNotNull(contract);
            Assert.assertTrue(contract instanceof ProxyCtl);
        }
        finally {
            ServiceLocatorUtilities.removeOneDescriptor(serviceLocator, added);
        }
    }

    /**
     * See https://java.net/jira/browse/HK2-163
     *
     * The problem was that the interface had no access to hk2 at
     * all, which caused classloading problems
     *
     * @throws Throwable
     */
    @Test
    public void testProxyClassWithNoAccessToHK2() throws Throwable {
        ServiceLocator serviceLocator = getMainServiceLocator();

        // This time the interface is NOT in the set of contracts
        Descriptor addMe = BuilderHelper.link(Bar.class.getName()).
                in(Singleton.class.getName()).
                proxy().
                andLoadWith(new HK2LoaderImpl(Bar.class.getClassLoader())).
                build();

        ActiveDescriptor<?> added = ServiceLocatorUtilities.addOneDescriptor(serviceLocator, addMe);
        try {
            Bar bar = serviceLocator.getService(Bar.class);
            Assert.assertNotNull(bar);
            Assert.assertTrue(bar instanceof ProxyCtl);
        }
        finally {
            ServiceLocatorUtilities.removeOneDescriptor(serviceLocator, added);
        }
    }

    private ServiceLocator getMainServiceLocator() throws Throwable {
        StartupContext startupContext = new StartupContext();
        ServiceTracker hk2Tracker = new ServiceTracker(
                this.bundleContext, Main.class.getName(), null);
        hk2Tracker.open();
        Main main = (Main) hk2Tracker.waitForService(0);
        hk2Tracker.close();
        ModulesRegistry mr = (ModulesRegistry) bundleContext
                .getService(bundleContext
                        .getServiceReference(ModulesRegistry.class
                                .getName()));
        ServiceLocator serviceLocator = main.createServiceLocator(
                mr, startupContext,null,null);
        ServiceLocatorUtilities.enableLookupExceptions(serviceLocator);
        return serviceLocator;
    }
}
