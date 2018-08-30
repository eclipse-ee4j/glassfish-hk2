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

package org.glassfish.hk2.runlevel.tests.ordering;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.inject.Singleton;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.InstanceLifecycleEvent;
import org.glassfish.hk2.api.InstanceLifecycleEventType;
import org.glassfish.hk2.api.InstanceLifecycleListener;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.runlevel.RunLevel;
import org.glassfish.hk2.runlevel.RunLevelController;
import org.glassfish.hk2.runlevel.tests.utilities.Utilities;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class OrderingTest {
    private final ServiceLocator locator = Utilities.getServiceLocator(Music.class, Opera.class, TimerActivator.class);
    
    /**
     * This ensures that we can get the proper timings for services
     * even when the RunLevelService will start the services out of order.
     * 
     * The test strategy here is to have the Music service depend on the
     * Opera service, but the RLS will get the Music service first, since
     * it has a higher ranking.  This test ensures that even though the
     * Opera service is a subordinate of the Music service that its timing
     * would still be accounted for properly
     * @throws TimeoutException 
     * @throws ExecutionException 
     * @throws InterruptedException 
     */
    @Test
    public void testCanGetCorrectTiming() throws InterruptedException, ExecutionException, TimeoutException {
        RunLevelController controller = locator.getService(RunLevelController.class);
        Assert.assertNotNull(controller);
        
        TimerActivator activator = locator.getService(TimerActivator.class);
        Assert.assertNotNull(activator);
        
        controller.proceedTo(5);
        
        LinkedList<ServiceData> records = activator.getRecords();
        
        ServiceData operaData = records.get(0);
        ServiceData musicData = records.get(1);
        
        Assert.assertEquals(operaData.descriptor.getImplementation(), Opera.class.getName());
        Assert.assertEquals(musicData.descriptor.getImplementation(), Music.class.getName());
        
        properRange(operaData.elapsedTime, "Opera");
        properRange(musicData.elapsedTime, "Music");
    }
    
    private void properRange(long elapsedTime, String who) {
        Assert.assertTrue("elapsed time of " + who + " is less than 50ms: " + elapsedTime,
                elapsedTime > 50L);
        
        Assert.assertTrue("elapsed time of " + who + " is more than 150ms: " + elapsedTime,
                elapsedTime < 150L);
        
        
    }
    
    @Singleton
    private static class TimerActivator implements InstanceLifecycleListener {
        private static final Filter FILTER = new Filter() {

            @Override
            public boolean matches(Descriptor d) {
                if (d.getScope() != null && d.getScope().equals(RunLevel.class.getName())) return true;
                
                return false;
            }
            
        };
        private final LinkedList<ServiceData> records = new LinkedList<ServiceData>();
        private final HashMap<String, Long> startTimes = new HashMap<String, Long>();
        
        public LinkedList<ServiceData> getRecords() {
            return records;
        }

        @Override
        public Filter getFilter() {
            return FILTER;
        }

        @Override
        public void lifecycleEvent(InstanceLifecycleEvent lifecycleEvent) {
            if (lifecycleEvent.getEventType().equals(InstanceLifecycleEventType.PRE_PRODUCTION)) {
                startTimes.put(lifecycleEvent.getActiveDescriptor().getImplementation(),
                        System.currentTimeMillis());
                return;
            }
            
            if (lifecycleEvent.getEventType().equals(InstanceLifecycleEventType.POST_PRODUCTION)) {
                Long startTime = startTimes.remove(lifecycleEvent.getActiveDescriptor().getImplementation());
                if (startTime == null) return;
                
                records.add(new ServiceData(lifecycleEvent.getActiveDescriptor(),
                        (System.currentTimeMillis() - startTime)));
            }
            
            // Ignore others
            
        }

        
        
    }
    
    private static class ServiceData {
        private final ActiveDescriptor<?> descriptor;
        private final long elapsedTime;
        
        private ServiceData(ActiveDescriptor<?> descriptor, long elapsedTime) {
            this.descriptor = descriptor;
            this.elapsedTime = elapsedTime;
        }
    }

}
