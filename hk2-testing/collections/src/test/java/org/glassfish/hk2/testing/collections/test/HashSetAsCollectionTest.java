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
import java.util.HashSet;

import org.glassfish.hk2.testing.collections.AbstractCollectionTest;

/**
 * Tests a HashSet as a Collection
 */
@SuppressWarnings({"rawtypes","unchecked"})
public class HashSetAsCollectionTest extends AbstractCollectionTest {
  /**
   * A HashSet does not keep duplicate elements
   */
  protected boolean doesCollectionSupportDuplicateElements() {
    return false;
  }

  @Override
  protected Collection createCollection() {
    return new HashSet();
  }

  @Override
  protected Collection createCollection(Collection input) {
    return new HashSet(input);
  }

}
