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

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.glassfish.hk2.api.IterableProvider;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.Unqualified;

/**
 * This is a local command (but it is named!)
 * 
 * @author jwells
 *
 */
@Named
public class ListCommandCommand implements Command {
    @Inject @Remote
    private IterableProvider<Command> remoteCommands;
    
    @Inject @Unqualified
    private IterableProvider<Command> localCommands;
    
    @Inject
    private IterableProvider<Command> allCommands;
    
    private final Command firstUnqualifiedCommand;
    
    private IterableProvider<Command> notRemoteCommands;
    
    @Inject 
    private ListCommandCommand(@Unqualified Command firstUnqualifiedCommand) {
        this.firstUnqualifiedCommand = firstUnqualifiedCommand;
        
    }
    
    @Inject 
    private void setNotRemoteCommands(@Unqualified(Remote.class) IterableProvider<Command> notRemoteCommands) {
        this.notRemoteCommands = notRemoteCommands;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.tests.locator.unqualified.Command#getName()
     */
    @Override
    public String getName() {
        return UnqualifiedTest.LIST_COMMAND_COMMAND;
    }
    
    public List<Command> getRemoteCommands() {
        LinkedList<Command> retVal = new LinkedList<Command>();
        
        for (Command remoteCommand : remoteCommands) {
            retVal.add(remoteCommand);
        }
        
        return retVal;
    }
    
    public List<Command> getLocalCommands() {
        LinkedList<Command> retVal = new LinkedList<Command>();
        
        for (Command localCommand : localCommands) {
            retVal.add(localCommand);
        }
        
        return retVal;
    }
    
    public List<Command> getAllCommands() {
        LinkedList<Command> retVal = new LinkedList<Command>();
        
        for (Command command : allCommands) {
            retVal.add(command);
        }
        
        return retVal;
    }
    
    public List<Command> getNotRemoteCommands() {
        LinkedList<Command> retVal = new LinkedList<Command>();
        
        for (Command command : notRemoteCommands) {
            retVal.add(command);
        }
        
        return retVal;
    }
    
    public Command getFirstUnqualifiedCommand() {
        return firstUnqualifiedCommand;
    }
    
    public Command getWithGetLocalCommand() {
        return localCommands.get();
    }
    
    public ServiceHandle<Command> getWithGetHandleLocalCommand() {
        return localCommands.getHandle();
    }

}
