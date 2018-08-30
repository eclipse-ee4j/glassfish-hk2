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

package org.glassfish.hk2.tests.locator.locator;

/**
 * This service will get a lot of things backwards.  Though marked with a
 * qualifier and not a qualifier, it will be the NON qualifier that is used
 * as a qualifier.  Also, although it implements one interface that IS a contract
 * and another that is NOT a contract, it will only use the NON contract as
 * a contract.
 * <p>
 * This is done by binding this method with a non-reified ActiveDescriptor
 * 
 * @author jwells
 *
 */
@Dead @NotAQualifier
public class ChineseService implements IsAContract, IsNotAContract {

}
