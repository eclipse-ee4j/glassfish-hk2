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

package org.glassfish.hk2.extension;

import org.glassfish.hk2.api.ServiceLocator;

/**
 * An implementation of this class can be placed in META-INF/services
 * in order to customize the creation of the ServiceLocator
 * 
 * @author jwells
 *
 */
public interface ServiceLocatorGenerator {
  /**
   * Creates the ServiceLocator that will be used to
   * generate the ServiceLocators
   * 
   * @param name The name of the ServiceLocator to create
   * @param parent The parent of the ServiceLocator (can be null)
   * @return The created ServiceLocator
   */
  public ServiceLocator create(String name, ServiceLocator parent);

}
