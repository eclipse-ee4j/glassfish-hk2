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

package org.glassfish.hk2.tests.api;

/**
 * This service has arrays for scope and qualifiers
 * 
 * @author jwells
 *
 */
@ArrayMetadataScope(
        getString={"a","b"},
        getByte={ (byte) 1, (byte) 2 },
        getShort={ (short) 3, (short) 4 },
        getInt={ (int) 5, (int) 6 },
        getChar={ 'c', 'd' },
        getLong={ 7L, 8L },
        getClasses= { ArrayMetadataScope.class, ArrayMetadataQualifier.class },
        getFloat={ (float) 9.0, (float) 10.0 },
        getDouble={ (double) 11.0, (double) 12.0 }
        )
@ArrayMetadataQualifier(
        getString={"e","f"},
        getByte={ (byte) 13, (byte) 14 },
        getShort={ (short) 15, (short) 16 },
        getInt={ (int) 17, (int) 18 },
        getChar={ 'g', 'h' },
        getLong={ 19L, 20L },
        getClasses= { Blue.class, Green.class },
        getFloat={ (float) 21.0, (float) 22.0 },
        getDouble={ (double) 23.0, (double) 24.0 }
        )
public class ArrayMetadataService {

}
