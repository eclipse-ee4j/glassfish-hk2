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

package org.glassfish.hk2.tests.locator.inheritance;

/**
 * This class should have the qualifiers:
 * <OL>
 * <LI>HasWinner (from Games)</LI>
 * <LI>Outdoors (from Sports)</LI>
 * <LI>Superbowl (from itself)</LI>
 * </OL>
 * <p>
 * This class should have PerLookup scope because the non-inherited
 * scope of Sports wipes out the inherited scope of Games, but is
 * not inherited.  So this class ends up having whatever scope it declares,
 * which is PerLookup by default
 * 
 * @author jwells
 *
 */
@Superbowl
public class AmericanFootball extends Sports {

}
