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
 * This is the default implementation of TestCollectionElement
 * that will be used in tests if the getElement
 * method is not overridden
 */
public class DefaultTestCollectionElement implements TestCollectionElement,
    Comparable<TestCollectionElement> {
  private final String value;
  
  public DefaultTestCollectionElement(String paramValue) {
    value = paramValue;
  }

  /* (non-Javadoc)
   * @see com.oracle.weblogic.testing.collections.TestCollectionElement#testCollectionValue()
   */
  @Override
  public String testCollectionValue() {
    return value;
  }

  @Override
  public int compareTo(TestCollectionElement o) {
    if (o == null) return -1;
    return safeCompareTo(value, o.testCollectionValue());
  }
  
  public int hashCode() {
    return value.hashCode();
  }
  
  private final static int safeCompareTo(String a, String b) {
    if (a == b) return 0;
    if (a == null) return -1;
    if (b == null) return 1;
    return a.compareTo(b);
  }
  
  private final static boolean safeEquals(Object a, Object b) {
    if (a == b) return true;
    if (a == null) return false;
    if (b == null) return false;
    return a.equals(b);
  }
  
  public boolean equals(Object o) {
    if (o == null) return false;
    if (!(o instanceof DefaultTestCollectionElement)) return false;
    
    DefaultTestCollectionElement dtce = (DefaultTestCollectionElement) o;
    
    return safeEquals(value, dtce.value);
  }
  
  public String toString() {
    return "DefaultTestCollectionElement(" + value + "," + System.identityHashCode(this) + ")";
  }

}
