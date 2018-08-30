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

import java.util.Collection;
import java.util.Set;

/**
 * This class can be used to test Sets for basic
 * Set functionality.
 * <p>
 * Subclasses must implement the methods that return
 * Sets from the two most common types of constructors,
 * the zero argument constructor and the single argument
 * Collection constructor.
 * <p>
 * In general, sets do not allow for duplicate entries, but
 * this may not be true of all sets.  All of the same questions
 * about Collections can also be overriden for Sets
 */
@SuppressWarnings({"rawtypes"})
public abstract class AbstractSetTest extends AbstractCollectionTest {
  /**
   * Subclasses should override this method if their Set
   * implementation allows duplicate elements.  In other words,
   * if you call "add(foo)" followed by "add(foo)" and the Set
   * will have two elements then your Set supports duplicate
   * elements.  Otherwise (if duplicates are overwritten) then this
   * method should return false.
   * 
   * @return true if the set allows an arbitrary number of
   * elements whose "equals" returns true to be in the collection, and
   * false if those objects will instead be overwritten
   */
  protected boolean doesCollectionSupportDuplicateElements() {
    return false;
  }

  /* (non-Javadoc)
   * @see com.oracle.weblogic.testing.collections.AbstractCollectionTest#createCollection()
   */
  @Override
  protected Collection createCollection() {
    return createSet();
  }

  /* (non-Javadoc)
   * @see com.oracle.weblogic.testing.collections.AbstractCollectionTest#createCollection(java.util.Collection)
   */
  @Override
  protected Collection createCollection(Collection input) {
    return createSet(input);
  }
  
  protected abstract Set createSet();
  
  protected abstract Set createSet(Collection input);

}
