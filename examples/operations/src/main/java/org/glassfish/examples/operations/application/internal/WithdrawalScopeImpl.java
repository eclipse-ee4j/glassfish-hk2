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

package org.glassfish.examples.operations.application.internal;

import org.glassfish.examples.operations.scopes.WithdrawalScope;
import org.glassfish.hk2.api.AnnotationLiteral;

/**
 * @author jwells
 *
 */
public class WithdrawalScopeImpl extends AnnotationLiteral<WithdrawalScope> implements WithdrawalScope {
    private static final long serialVersionUID = -6737713851216041475L;
    
    public static final WithdrawalScope INSTANCE = new WithdrawalScopeImpl();

}
