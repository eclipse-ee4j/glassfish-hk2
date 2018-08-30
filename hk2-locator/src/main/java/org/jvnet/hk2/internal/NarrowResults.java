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

package org.jvnet.hk2.internal;

import java.util.LinkedList;
import java.util.List;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.MultiException;

/**
 * @author jwells
 *
 */
public class NarrowResults {
    private List<ActiveDescriptor<?>> unnarrowedResults;
    private final List<ActiveDescriptor<?>> goodResults = new LinkedList<ActiveDescriptor<?>>();
    private final List<ErrorResults> errors = new LinkedList<ErrorResults>();
    
    /* package */ void addGoodResult(ActiveDescriptor<?> result) {
        goodResults.add(result);
    }
    
    /* package */ void addError(ActiveDescriptor<?> fail, Injectee injectee, MultiException me) {
        errors.add(new ErrorResults(fail, injectee, me));
    }
    
    /* package */ List<ActiveDescriptor<?>> getResults() {
        return goodResults;
    }
    
    /* package */ List<ErrorResults> getErrors() {
        return errors;
    }
    
    /* package */ void setUnnarrowedResults(List<ActiveDescriptor<?>> unnarrowed) {
        unnarrowedResults = unnarrowed;
    }
    
    /* package */ ActiveDescriptor<?> removeUnnarrowedResult() {
        if (unnarrowedResults == null || unnarrowedResults.isEmpty()) return null;
        
        return unnarrowedResults.remove(0);
    }
    
    public String toString() {
        return "NarrowResults(goodResultsSize=" + goodResults.size() + ",errorsSize=" + errors.size() +
                "," + System.identityHashCode(this) + ")";
    }

}
