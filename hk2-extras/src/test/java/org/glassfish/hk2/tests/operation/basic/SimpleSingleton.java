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
import javax.inject.Singleton;

import org.glassfish.hk2.api.ProxyCtl;
import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceHandle;

import org.glassfish.hk2.extras.operation.OperationHandle;
import org.glassfish.hk2.extras.operation.OperationManager;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@Singleton
public class SimpleSingleton {

  private final static BasicOperationScope BASIC_OPERATION_ANNOTATION = new BasicOperationScopeImpl();
  
  private final ServiceLocator serviceLocator;
  
  @Inject
  public SimpleSingleton(final ServiceLocator serviceLocator) {
    super();
    assertNotNull(serviceLocator);
    this.serviceLocator = serviceLocator;
  }

  public void reticulateSplines() {
    final OperationManager operationManager = this.serviceLocator.getService(OperationManager.class);
    assertNotNull(operationManager);

    try (OperationHandle<BasicOperationScope> handle = operationManager.createAndStartOperation(BASIC_OPERATION_ANNOTATION)) {
      assertNotNull(handle);
    
      final ActiveDescriptor<?> fd = this.serviceLocator.getBestDescriptor(new FrobnicatorFilter());
      assertNotNull(fd);

      final ServiceHandle<?> sh = this.serviceLocator.getServiceHandle(fd);
      assert sh != null;
      
      final Frobnicator frobnicator = (Frobnicator)sh.getService();
      assertTrue(frobnicator instanceof ProxyCtl);
      frobnicator.toString();
    }
    
  }

  private static final class FrobnicatorFilter implements Filter {

    private FrobnicatorFilter() {
      super();
    }

    @Override
    public final boolean matches(final Descriptor descriptor) {
      boolean returnValue = false;
      if (descriptor != null &&
          Frobnicator.class.getName().equals(descriptor.getImplementation()) &&
          BasicOperationScope.class.getName().equals(descriptor.getScope())) {
        returnValue = true;
      }
      return returnValue;
    }
  }
  
}
