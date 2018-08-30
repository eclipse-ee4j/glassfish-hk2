/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.utilities.general;

import java.lang.reflect.Array;

import org.glassfish.hk2.utilities.general.internal.WeakHashClockImpl;
import org.glassfish.hk2.utilities.general.internal.WeakHashLRUImpl;

/**
 * This class contains utilities useful for any code
 * 
 * @author jwells
 *
 */
public class GeneralUtilities {
    /**
     * Returns true if a is equals to b, or both
     * and and b are null.  Is safe even if
     * a or b is null.  If a or b is null but
     * the other is not null, this returns false
     * 
     * @param a A possibly null object to compare
     * @param b A possibly null object to compare
     * @return true if equal, false if not
     */
    public static boolean safeEquals(Object a, Object b) {
        if (a == b) return true;
        if (a == null) return false;
        if (b == null) return false;
        
        return a.equals(b);
    }
    
    private static Class<?> loadArrayClass(ClassLoader cl, String aName) {
        Class<?> componentType = null;
        int[] dimensions = null;
        
        int dot = 0;
        while (componentType == null) {
            char dotChar = aName.charAt(dot);
            if (dotChar == '[') {
                dot++;
                continue;
            }
            
            dimensions = new int[dot];
            for (int lcv = 0; lcv < dot; lcv++) {
                dimensions[lcv] = 0;
            }
            
            if (dotChar == 'B') {
                componentType = byte.class;
            }
            else if (dotChar == 'I') {
                componentType = int.class;
            }
            else if (dotChar == 'J') {
                componentType = long.class;
            }
            else if (dotChar == 'Z') {
                componentType = boolean.class;
            }
            else if (dotChar == 'S') {
                componentType = short.class;
            }
            else if (dotChar == 'C') {
                componentType = char.class;
            }
            else if (dotChar == 'D') {
                componentType = double.class;
            }
            else if (dotChar == 'F') {
                componentType = float.class;
            }
            else {
                if (dotChar != 'L') {
                    throw new IllegalArgumentException("Unknown array type " + aName);
                }
                
                if (aName.charAt(aName.length() - 1) != ';') {
                    throw new IllegalArgumentException("Badly formed L array expresion: " + aName);
                }
                
                String cName = aName.substring(dot + 1, aName.length() - 1);
                
                componentType = loadClass(cl, cName);
                if (componentType == null) return null;
            }
        }
        
        Object retArray = Array.newInstance(componentType, dimensions);
        return retArray.getClass();
    }
    
    /**
     * Loads the class from the given classloader or returns null (does not throw).
     * Property handles array classes as well
     * 
     * @param cl The non-null classloader to load the class from
     * @param cName The fully qualified non-null name of the class to load
     * @return The class if it could be loaded from the classloader, or
     * null if it could not be found for any reason
     */
    public static Class<?> loadClass(ClassLoader cl, String cName) {
        if (cName.startsWith("[")) {
            return loadArrayClass(cl, cName);
        }
        
        try {
            return cl.loadClass(cName);
        }
        catch (Throwable th) {
            return null;
        }
    }
    
    /**
     * Creates a weak hash clock
     * @param isWeak if true this will keep weak keyes, if false the keys will
     * be hard and will not go away even if they do not exist anywhere else
     * but this cache
     * @return A weak hash clock implementation
     */
    public static <K,V> WeakHashClock<K,V> getWeakHashClock(boolean isWeak) {
        return new WeakHashClockImpl<K,V>(isWeak);
    }
    
    /**
     * Creates a weak hash clock
     * @param isWeak if true this will keep weak keyes, if false the keys will
     * be hard and will not go away even if they do not exist anywhere else
     * but this cache
     * @return A weak hash clock implementation
     */
    public static <K> WeakHashLRU<K> getWeakHashLRU(boolean isWeak) {
        return new WeakHashLRUImpl<K>(isWeak);
    }
    
    /**
     * Pretty prints a string of bytes in a hexadecimal format with
     * byte number at the start of the line
     * 
     * @param bytes A non-null string of bytes
     * @return A string that has the bytes pretty-printed
     */
    public static String prettyPrintBytes(byte bytes[]) {
        StringBuffer sb = new StringBuffer("Total buffer length: " + bytes.length + "\n");
        
        int numEntered = 0;
        for (byte b : bytes) {
            if ((numEntered % 16) == 0) {
                if (numEntered != 0) {
                    sb.append("\n");
                }
                
                String desc = String.format("%08X " , numEntered);
                sb.append(desc);
            }
            
            String singleByte = String.format("%02X ", b);
            sb.append(singleByte);
            
            numEntered++;
            
            if (((numEntered % 8) == 0) && ((numEntered % 16) != 0)) {
                sb.append(" ");
            }
        }
        
        
        return sb.toString();
    }

}
