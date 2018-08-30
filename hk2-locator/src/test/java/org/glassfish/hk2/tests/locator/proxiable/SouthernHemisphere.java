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

package org.glassfish.hk2.tests.locator.proxiable;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.glassfish.hk2.api.ProxyCtl;
import org.junit.Assert;

/**
 * This class does nothing more than call the methods on the proxiable
 * NorthernHemisphere, to ensure that they can be called properly
 * 
 * @author jwells
 *
 */
@Singleton
public class SouthernHemisphere {
	@Inject
	private NorthernHemisphere north;
	
	public void check() {
	    Assert.assertTrue(north instanceof ProxyCtl);
	    
	    Object id = north.iAmAPublicMethod();
	    Assert.assertNotNull(id);
	    
	    // There is a bug such that package methods are not being intercepted properly
	    // See https://java.net/jira/browse/HK2-257
		// Object pId = north.iAmAPackageMethod();
		// Assert.assertEquals(id, pId);
		
		Object prId = north.iAmAProtectedMethod();
		Assert.assertEquals(id, prId);
	}

}
