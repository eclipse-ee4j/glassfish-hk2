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

package org.glassfish.hk2.testing.collections.test;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.glassfish.hk2.testing.collections.AbstractListTest;

/**
 * Tests the subList of a LinkedList
 * 
 * This is mainly a demonstration of how to get higher code coverage of
 * Lists by testing their subList
 */
@SuppressWarnings({"rawtypes","unchecked"})
public class LinkedListSubListTest extends AbstractListTest {

  /* (non-Javadoc)
   * @see com.oracle.weblogic.testing.collections.AbstractListTest#createList()
   */
  @Override
  protected List createList() {
    List list = new LinkedList();
    return list.subList(0, 0);
  }

  /* (non-Javadoc)
   * @see com.oracle.weblogic.testing.collections.AbstractListTest#createList(java.util.Collection)
   */
  @Override
  protected List createList(Collection input) {
    List list = new LinkedList(input);
    return list.subList(0, list.size());
  }

}
