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

package org.jvnet.hk2.generator.internal;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import org.glassfish.hk2.utilities.DescriptorImpl;

/**
 * This is a comparator making things that don't really compare, compare.
 * It is done to ensure that given the same set of descriptors we always
 * return the set in the same order, which will ensure that the output
 * of the generator is not different from run to run
 * 
 * @author jwells
 *
 */
public class DescriptorComparitor implements Comparator<DescriptorImpl> {
    private static <T> int safeCompare(Comparable<T> a, T b) {
        if (a == null && b == null) return 0;
        if (a == null) return -1;
        if (b == null) return 1;
        
        return a.compareTo(b);
    }
    
    private static int compareStringMaps(Set<String> s1, Set<String> s2) {
        int size1 = s1.size();
        int size2 = s2.size();
        
        if (size1 != size2) return (size1 - size2);
        
        TreeSet<String> s1sorted = new TreeSet<String>(s1);
        TreeSet<String> s2sorted = new TreeSet<String>(s2);
        
        StringBuffer s1b = new StringBuffer();
        for (String s1sv : s1sorted) {
            s1b.append(s1sv);
        }
        
        StringBuffer s2b = new StringBuffer();
        for (String s2sv : s2sorted) {
            s2b.append(s2sv);
        }
        
        return safeCompare(s1b.toString(), s2b.toString());
    }

    @Override
    public int compare(DescriptorImpl o1, DescriptorImpl o2) {
        int retVal = o2.getRanking() - o1.getRanking();
        if (retVal != 0) return retVal;
        
        retVal = safeCompare(o1.getImplementation(), o2.getImplementation());
        if (retVal != 0) return retVal;
        
        retVal = safeCompare(o1.getName(), o2.getName());
        if (retVal != 0) return retVal;
        
        retVal = safeCompare(o1.getScope(), o2.getScope());
        if (retVal != 0) return retVal;
        
        retVal = compareStringMaps(o1.getAdvertisedContracts(), o2.getAdvertisedContracts());
        if (retVal != 0) return retVal;
        
        retVal = compareStringMaps(o1.getQualifiers(), o2.getQualifiers());
        if (retVal != 0) return retVal;
        
        retVal = o1.getDescriptorType().compareTo(o2.getDescriptorType());
        if (retVal != 0) return retVal;
        
        retVal = o1.getDescriptorVisibility().compareTo(o2.getDescriptorVisibility());
        if (retVal != 0) return retVal;
        
        return 0;
    }
    
}
