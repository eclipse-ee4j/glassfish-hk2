#
# Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License v. 2.0, which is available at
# http://www.eclipse.org/legal/epl-2.0.
#
# This Source Code may also be made available under the following Secondary
# Licenses when the conditions for such availability set forth in the
# Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
# version 2 with the GNU Classpath Exception, which is available at
# https://www.gnu.org/software/classpath/license.html.
#
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
#

This directory contains unit tests for the hk2-locator module that needed
to be separated from the hk2-locator directory itself.  At the time
of writing this is due to the fact that the test code needs to perform
operations that would normally require security checks (creating ClassLoaders
and getting/setting the ContextClassLoaders).  Since we want to keep the
set of security permissions granted in hk2-locator to a minimum, these
tests were moved here in order to run without the security manager.
