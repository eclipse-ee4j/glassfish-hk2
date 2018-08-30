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

package org.glassfish.hk2.tests.api;

import org.glassfish.hk2.api.ProxyForSameScope;
import org.glassfish.hk2.api.UseProxy;

/**
 * This guy extends a class that is not a contract but implements
 * an interface which is a contract.  That subclass also extends
 * a class that IS a contract and who implements an interface
 * that is NOT a contract.  This then in turn extends a parameterized
 * object that is NOT a contract while the parameterized interface
 * it implements IS a contract
 * 
 * Thus the generated contracts for
 * this object should be:<OL>
 * <LI>ComplexHierarch</LI>
 * <LI>MarkerInterfaceImpl</LI>
 * <LI>MarkerInterface2</LI>
 * <LI>ParameterizedInterface</LI>
 * </OL>
 * 
 * @author jwells
 *
 */
@UseProxy(false)
@ProxyForSameScope(true)
public class ComplexHierarchy extends MarkerInterface2Impl {

}
