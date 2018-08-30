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

package org.acme.security;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Singleton;

import org.jvnet.hk2.annotations.Service;


/**
 * This is a dummy security service that should be available to some code (Alice)
 * but not directly available to other code (Mallory).  That's because Mallory would
 * spam the service and do something evil to it, while Alice would use the service
 * as intended.
 * 
 * @author jwells
 */
@Service @Singleton
public class AuditService {
    private final LinkedList<String> audits = new LinkedList<String>();
    
    /**
     * Securely (not really) audits a message
     * 
     * @param logMe The message to log
     */
    public void auditLog(String logMe) {
        if (logMe == null) return;
        audits.add(logMe);
    }
    
    /**
     * Gets everything that has been logged
     * 
     * @return A non-null (but possibly empty) set of logged messages
     */
    public List<String> getAuditLogs() {
        return Collections.unmodifiableList(audits);
    }

}
