/********************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package org.glassfish.hk2.tests.locator.optional;

import java.util.Optional;

import jakarta.inject.Inject;
import jakarta.inject.Named;
/** @author Balthasar Schüss */
public class NamedOptionalInjection {
    @Inject
    @Named(NamedOptionalTest.NAMED)
    Optional<String> providedOptional;
  }