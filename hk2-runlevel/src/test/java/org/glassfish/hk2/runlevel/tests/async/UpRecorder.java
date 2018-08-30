/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.runlevel.tests.async;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.jvnet.hk2.annotations.Service;

/**
 * @author jwells
 *
 */
@Service
public class UpRecorder {
    private List<String> data;
    
    public synchronized void recordUp(String recordMe) {
        if (data == null) data = new LinkedList<String>();
            
        data.add(recordMe);
    }
    
    public synchronized List<String> getRecordsAndPurge() {
        if (data == null) return Collections.emptyList();
        
        List<String> retVal = data;
        data = null;
        return retVal;
        
    }

}
