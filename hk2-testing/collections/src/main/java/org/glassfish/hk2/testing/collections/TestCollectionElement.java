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

package org.glassfish.hk2.testing.collections;

/**
 * This is an interface for TestCollectionElements.
 * Items to be added to the collection under test
 * must implement this interface in order to
 * be tested.
 */
public interface TestCollectionElement {
  
  /**
   * TestCollectionElements will be created with a single string,
   * and must return that string with this method
   * when called.
   * 
   * @return The String that was passed in when creating
   * the TestCollectionElement
   */
  public String testCollectionValue();

}
