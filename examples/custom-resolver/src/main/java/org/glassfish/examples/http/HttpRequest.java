/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.examples.http;

import java.util.ArrayList;

/**
 * This is not a true HttpRequest, but is just here for illustration purposes.
 * It would get the individual items out of the real HttpRequest in a real system.
 * In this case, it just spits out whatever is put in
 * 
 * @author jwells
 *
 */
@RequestScope
public class HttpRequest {
    private final ArrayList<String> elements = new ArrayList<String>();
    
    /**
     * Gets the path element from the given index
     * 
     * @param index the element to get the index from
     * @return The element at this index (as a string)
     */
    public String getPathElement(int index) {
        if (elements.size() <= index) {
            throw new AssertionError("There is no element at index " + index);
        }
        
        return elements.get(index);
    }
    
    /**
     * Sets the next element in the request
     * 
     * @param element The element to put next in the elements list
     */
    public void addElement(String element) {
        elements.add(element);
    }
}
