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

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

import org.glassfish.hk2.api.Metadata;

/**
 * Has a Metadata annotation for every type of array
 * 
 * @author jwells
 *
 */
@Qualifier
@Retention(RUNTIME)
@Target( { TYPE, METHOD, FIELD, PARAMETER })
public @interface ArrayMetadataQualifier {
    public static final String STRING_KEY = "QualifierStringArray";
    public static final String BYTE_KEY = "QualifierByteArray";
    public static final String SHORT_KEY = "QualifierShortArray";
    public static final String INT_KEY = "QualifierIntArray";
    public static final String CHAR_KEY = "QualifierCharArray";
    public static final String LONG_KEY = "QualifierLongArray";
    public static final String CLASS_KEY = "QualifierClassArray";
    public static final String FLOAT_KEY = "QualifierFloatArray";
    public static final String DOUBLE_KEY = "QualifierDoubleArray";
    
    @Metadata(STRING_KEY)
    public String[] getString();
    
    @Metadata(BYTE_KEY)
    public byte[] getByte();
    
    @Metadata(SHORT_KEY)
    public short[] getShort();
    
    @Metadata(INT_KEY)
    public int[] getInt();
    
    @Metadata(CHAR_KEY)
    public char[] getChar();
    
    @Metadata(LONG_KEY)
    public long[] getLong();
    
    @Metadata(CLASS_KEY)
    public Class<?>[] getClasses();
    
    @Metadata(FLOAT_KEY)
    public float[] getFloat();
    
    @Metadata(DOUBLE_KEY)
    public double[] getDouble();

}
