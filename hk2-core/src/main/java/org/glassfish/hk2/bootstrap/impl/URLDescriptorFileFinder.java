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

package org.glassfish.hk2.bootstrap.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.glassfish.hk2.api.DescriptorFileFinder;

public class URLDescriptorFileFinder implements DescriptorFileFinder {
	private final URL url;

	public URLDescriptorFileFinder(URL url) {
		this.url = url;
	}

	@Override
	public List<InputStream> findDescriptorFiles() throws IOException {
		ArrayList<InputStream> returnList = new ArrayList<InputStream>();
		returnList.add(url.openStream());
		return returnList;
	}
}
