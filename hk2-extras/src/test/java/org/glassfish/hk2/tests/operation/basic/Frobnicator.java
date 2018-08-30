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

package org.glassfish.hk2.tests.operation.basic;

import javax.inject.Inject;

import org.glassfish.hk2.api.ProxyCtl;
import org.glassfish.hk2.api.ServiceLocator;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@BasicOperationScope
public class Frobnicator {

  private final Caturgiator caturgiator;
  
  @Deprecated
  public Frobnicator() {
    super();
    this.caturgiator = null;
  }

  @Inject
  public Frobnicator(@Enscrofulated @Miserable final Caturgiator caturgiator) {
    super();
    assertNotNull(caturgiator);
    // BasicOperationScope is defined such that a proxy should not be
    // used when a BasicOperationScoped service is injected into
    // another BasicOperationScoped service.
    assertFalse(caturgiator instanceof ProxyCtl);
    this.caturgiator = caturgiator;
  }

  public void frobnicate() {
    this.caturgiator.caturgiate();
  }
  
}
