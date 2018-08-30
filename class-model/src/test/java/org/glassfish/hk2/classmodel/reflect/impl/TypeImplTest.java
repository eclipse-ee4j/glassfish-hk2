/*
 * Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.classmodel.reflect.impl;

import static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

import org.glassfish.hk2.classmodel.reflect.impl.TypeImpl;
import org.junit.Test;

/**
 * Tests for {@link TypeImpl}.
 * 
 * @author Jeff Trent
 */
public class TypeImplTest {
  
  /**
   * Motivated by bug# 12376520 and http://java.net/jira/browse/GLASSFISH-16406
   */
  @Test
  public void wasContainedIn() throws URISyntaxException {
    TypeImpl ti = new TypeImpl(null ,null);
    URI uri = new URI("file:/var/folders/IF/IFNvUBVCFPWlqMFXX-mK2++++TI/-Tmp-/gfembed5393798310343802143tmp/applications/ejb-ejb31-embedded-profile-ejb/");
    ti.addDefiningURI(uri);
    assertTrue("uri should be defined in: " + uri, ti.wasDefinedIn(Collections.singleton(uri)));
  }
  

}
