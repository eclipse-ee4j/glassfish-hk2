/*
 * Copyright (c) 2023 Payara Foundation and/or its affiliates. All rights reserved.
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
package org.glassfish.hk2.utilities;

import java.lang.ref.Cleaner;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.ThreadFactory;

/**
 * CleanerFactory provides a Cleaner reference which is created on the first
 * reference to the CleanerFactory.
 */
public final class CleanerFactory {

    /* The common Cleaner. */
    private final static Cleaner commonCleaner = Cleaner.create(new ThreadFactory() {
        @Override
        public Thread newThread(final Runnable r) {
            return AccessController.doPrivileged(new PrivilegedAction<>() {
                @Override
                public Thread run() {
                    Thread t = new Thread(null, r, "Common-Cleaner");
                    t.setPriority(Thread.MAX_PRIORITY - 2);
                    return t;
                }
            });
        }
    });


    /**
     * This Cleaner will run on a thread whose context class loader
     * is {@code null}. The system cleaning action to perform in
     * this Cleaner should handle a {@code null} context class loader.
     *
     * @return a common cleaner reference
     */
    public static Cleaner create() {
        return commonCleaner;
    }
}
