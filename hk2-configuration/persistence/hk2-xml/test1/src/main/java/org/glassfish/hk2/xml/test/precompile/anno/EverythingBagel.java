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

package org.glassfish.hk2.xml.test.precompile.anno;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author jwells
 *
 */
@Retention(RUNTIME)
@Target( { TYPE, METHOD, FIELD, PARAMETER })
public @interface EverythingBagel {
    public byte byteValue();
    public boolean booleanValue();
    public char charValue();
    public short shortValue();
    public int intValue();
    public long longValue();
    public float floatValue();
    public double doubleValue();
    public GreekEnum enumValue();
    public String stringValue();
    public Class<?> classValue();
    
    public byte[] byteArrayValue();
    public boolean[] booleanArrayValue();
    public char[] charArrayValue();
    public short[] shortArrayValue();
    public int[] intArrayValue();
    public long[] longArrayValue();
    public float[] floatArrayValue();
    public double[] doubleArrayValue();
    public GreekEnum[] enumArrayValue();
    public String[] stringArrayValue();
    public Class<?>[] classArrayValue();

}
