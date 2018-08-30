/*
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.xml.test.dynamic.negative;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.configuration.hub.api.Hub;
import org.glassfish.hk2.xml.api.XmlRootHandle;
import org.glassfish.hk2.xml.api.XmlService;
import org.glassfish.hk2.xml.test.beans.DiagnosticsBean;
import org.glassfish.hk2.xml.test.beans.DomainBean;
import org.glassfish.hk2.xml.test.beans.SSLManagerBeanCustomizer;
import org.glassfish.hk2.xml.test.dynamic.rawsets.UpdateListener;
import org.glassfish.hk2.xml.test.utilities.Utilities;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for bad things that should be caught by the system
 * 
 * @author jwells
 *
 */
public class NegativeTest {
   /**
    * Tests that setting things in a direct bean is fine
    * 
    * @throws Exception
    */
   @Test
   // @org.junit.Ignore
   public void testKeyedDirectSetModification() throws Exception {
       ServiceLocator locator = Utilities.createLocator(UpdateListener.class,
               SSLManagerBeanCustomizer.class);
       XmlService xmlService = locator.getService(XmlService.class);
       Hub hub = locator.getService(Hub.class);
       
       XmlRootHandle<DomainBean> rootHandle = xmlService.createEmptyHandle(DomainBean.class);
       rootHandle.addRoot();
       
       DomainBean domain = rootHandle.getRoot();
       
       DiagnosticsBean unnamedBean = xmlService.createBean(DiagnosticsBean.class);
       
       try {
           domain.setDiagnostics(unnamedBean);
           Assert.fail("Should not have succeeded since there was no key set");
       }
       catch (IllegalArgumentException iae) {
           Assert.assertTrue(iae.getMessage().contains("diagnostics"));
       }
    }

}
