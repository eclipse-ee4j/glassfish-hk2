/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.jvnet.hk2.metadata.tests.stub;

import java.util.List;
import java.util.Map;
import java.util.Random;

import org.jvnet.hk2.annotations.Contract;

/**
 * Imagine that this is an interface with a lot of methods on it
 * 
 * @author jwells
 *
 */
@Contract
public interface LargeInterface {
    
    
    /**
     * This method will not be overridden
     * 
     * @param param
     * @return
     */
    public boolean notOverridden(boolean param);
    
    public void methodVoids();
    public boolean methodBoolean(boolean param);
    public byte methodByte(byte param);
    public char methodChar(char param);
    public double methodDouble(double param);
    public float methodFloat(float param);
    public int methodInt(int param);
    public long methodInt(long param);
    public short methodShort(short param);
    public List<String> methodDeclared(Map<Object, String> param, String param1, Random param2);
    
    public boolean[] methodBooleanArray(boolean[] param);
    public byte[][][][] methodByteArray(byte[] param);
    public char[] methodCharArray(char[][] param);
    public double[][] methodDoubleArray(double[][][] param);
    public float[] methodFloatArray(float[][] param);
    public int[][] methodIntArray(int[] param);
    public long[] methodIntArray(long[][][][][] param);
    public short[] methodShortArray(short[] param);
    public List<String>[] methodDeclaredArray(Map<Object, String>[] param, String[] param1, Random... param2);
    
}
