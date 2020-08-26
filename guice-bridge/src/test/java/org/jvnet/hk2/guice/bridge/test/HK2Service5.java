/********************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package org.jvnet.hk2.guice.bridge.test;

import java.util.Optional;

import jakarta.inject.Inject;

/** @author Balthasar Schüss */
public class HK2Service5 {
  @Inject Optional<GuiceService5> optionalGuiceService;
}
