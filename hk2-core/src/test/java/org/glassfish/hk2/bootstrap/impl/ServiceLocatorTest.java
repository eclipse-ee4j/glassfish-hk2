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

package org.glassfish.hk2.bootstrap.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import com.sun.enterprise.module.ModulesRegistry;
import com.sun.enterprise.module.common_impl.AbstractFactory;
import com.sun.enterprise.module.impl.HK2Factory;

import org.glassfish.hk2.api.ServiceLocator;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sun.enterprise.module.bootstrap.BootException;
import com.sun.enterprise.module.bootstrap.Main;
import com.sun.enterprise.module.bootstrap.StartupContext;

public class ServiceLocatorTest {

	static File testFile;

	static final String text = "# generated on 2 Apr 2012 18:04:09 GMT\n"
			+ "class=com.sun.enterprise.admin.cli.optional.RestoreDomainCommand,index=com.sun.enterprise.admin.cli.CLICommand:restore-domain\n"
			+ "class=com.sun.enterprise.admin.cli.optional.ListBackupsCommand,index=com.sun.enterprise.admin.cli.CLICommand:list-backups\n";

	@BeforeClass
	public static void createTestInhabitantsFile() throws Exception {
		testFile = File.createTempFile("aaaa", "bbbb");
		testFile.deleteOnExit();
		BufferedWriter output = null;
		try {
			output = new BufferedWriter(new FileWriter(testFile));
			output.write(text);
		} finally {
			output.close();
		}
        HK2Factory.initialize();
	}

	@AfterClass
	public static void deleteTestInhabitantsFile() throws Exception {
		testFile.delete();
	}

	@Test
	public void testCreateServiceLocator() throws BootException {

		StartupContext context = new StartupContext();

		Main main = new Main();

        ModulesRegistry mr = AbstractFactory.getInstance().createModulesRegistry();

		ServiceLocator serviceLocator = main.createServiceLocator(
                mr, new StartupContext(), null, null);

                assertNotNull("Main.createServiceLocator(StartupContext) should return a ServiceLocator", serviceLocator);

		assertEquals("ServiceLocator should be bound", serviceLocator,
				serviceLocator.getService(ServiceLocator.class));

	}

	static boolean postProcessorWasCalled;

	

}
