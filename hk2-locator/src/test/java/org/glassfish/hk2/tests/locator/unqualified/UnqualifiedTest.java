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

package org.glassfish.hk2.tests.locator.unqualified;

import java.util.List;

import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for the &#064;Unqualified directive
 * 
 * @author jwells
 *
 */
public class UnqualifiedTest {
    private final static String TEST_NAME = "UnqualifiedTest";
    private final static ServiceLocator locator = LocatorHelper.create(TEST_NAME, new UnqualifiedModule());
    
    public final static String SERVER_HEALTH_COMMAND = "ServerHealthCommand";
    public final static String SERVER_DATA_COMMAND = "ServerDataCommand";
    public final static String CLIENT_HEALTH_COMMAND = "ClientHealthCommand";
    public final static String CLIENT_DATA_COMMAND = "ClientDataCommand";
    public final static String LIST_COMMAND_COMMAND = "ListCommandCommand";
    
    public final static String TRACTOR_ELEPHANT_SHOE_TOY = "TractorElephantShoeToy";
    public final static String SHOE_TOY = "ShoeToy";
    public final static String UNKNOWN_TOY = "UnknownToy";
    
    @Test
    public void testUnqualifiedOnlyGetsUnqualified() {
        ListCommandCommand lcc = locator.getService(ListCommandCommand.class);
        Assert.assertNotNull(lcc);
        
        {
            List<Command> remoteCommands = lcc.getRemoteCommands();
            Assert.assertSame(2, remoteCommands.size());
        
            Assert.assertSame(SERVER_DATA_COMMAND, remoteCommands.get(0).getName());
            Assert.assertSame(SERVER_HEALTH_COMMAND, remoteCommands.get(1).getName());
        }
        
        {
            List<Command> allCommands = lcc.getAllCommands();
            Assert.assertSame(5, allCommands.size());
        
            Assert.assertSame(SERVER_DATA_COMMAND, allCommands.get(0).getName());
            Assert.assertSame(CLIENT_DATA_COMMAND, allCommands.get(1).getName());
            Assert.assertSame(CLIENT_HEALTH_COMMAND, allCommands.get(2).getName());
            Assert.assertSame(SERVER_HEALTH_COMMAND, allCommands.get(3).getName());
            Assert.assertSame(LIST_COMMAND_COMMAND, allCommands.get(4).getName());
        }
        
        // And  now the real test
        {
            List<Command> localCommands = lcc.getLocalCommands();  // Local commands are unqualified
            Assert.assertSame(2, localCommands.size());
        
            Assert.assertSame(CLIENT_DATA_COMMAND, localCommands.get(0).getName());
            Assert.assertSame(CLIENT_HEALTH_COMMAND, localCommands.get(1).getName());
        }
        
        // Lets make sure it also got the correct "first" unqualified service
        Assert.assertSame(CLIENT_DATA_COMMAND, lcc.getFirstUnqualifiedCommand().getName());
        
        // Now not just unqualified, but those not qualified with @Remote
        {
            List<Command> notRemoteCommands = lcc.getNotRemoteCommands();  // Local commands are unqualified
            Assert.assertSame(3, notRemoteCommands.size());
        
            Assert.assertSame(CLIENT_DATA_COMMAND, notRemoteCommands.get(0).getName());
            Assert.assertSame(CLIENT_HEALTH_COMMAND, notRemoteCommands.get(1).getName());
            Assert.assertSame(LIST_COMMAND_COMMAND, notRemoteCommands.get(2).getName());
        }
        
    }
    
    @Test
    public void testDirectlyInjectedUnqualified() {
        ToyService ts = locator.getService(ToyService.class);
        
        Assert.assertSame(TRACTOR_ELEPHANT_SHOE_TOY, ts.getNaturalToy().getName());
        Assert.assertSame(SHOE_TOY, ts.getShoeToy().getName());
        Assert.assertSame(UNKNOWN_TOY, ts.getUnknownToy().getName());
        
    }
    
    /**
     * Tests the get of the unqualified iterable provider
     */
    @Test
    public void testGetOfUnqualifiedIterableProvider() {
        ListCommandCommand lcc = locator.getService(ListCommandCommand.class);
        Assert.assertNotNull(lcc);
        
        Command viaGet = lcc.getWithGetLocalCommand();
        
        Assert.assertSame(CLIENT_DATA_COMMAND, viaGet.getName());
    }
    
    /**
     * Tests the getHandle of the unqualified iterable provider
     */
    @Test
    public void testGetHandleOfUnqualifiedIterableProvider() {
        ListCommandCommand lcc = locator.getService(ListCommandCommand.class);
        Assert.assertNotNull(lcc);
        
        ServiceHandle<Command> viaGetHandle = lcc.getWithGetHandleLocalCommand();
        
        Assert.assertSame(CLIENT_DATA_COMMAND, viaGetHandle.getService().getName());
    }

}
