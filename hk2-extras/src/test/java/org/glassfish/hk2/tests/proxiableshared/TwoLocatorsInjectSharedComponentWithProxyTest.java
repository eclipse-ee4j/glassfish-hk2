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

package org.glassfish.hk2.tests.proxiableshared;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.extras.ExtrasUtilities;
import org.glassfish.hk2.extras.operation.OperationManager;
import org.glassfish.hk2.tests.extras.internal.Utilities;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;


/**
 * Ensure that two distinct service locators could be utilized to inject
 * a component that is managed by a 3rd-party component manager. The 3rd-party component behaves as a global
 * singleton. It is required that the component is injected with proxy instances.
 *
 * @author Jakub Podlesak (jakub.podlesak at oracle.com)
 */
public class TwoLocatorsInjectSharedComponentWithProxyTest {

    private final static String TEST_NAME = TwoLocatorsInjectSharedComponentWithProxyTest.class.getSimpleName();
    
    private static ServiceLocator newApplicationLocator(String name) {
        ServiceLocator retVal = Utilities.getCleanLocator(name,
                GlobalReqContext.class,
                GlobalComponent.class);
        
        ExtrasUtilities.enableOperations(retVal);
        
        return retVal;
    }

    private static ServiceLocator newComponentLocator(String name, ServiceLocator appLocator) {
        return Utilities.getCleanLocator(name, appLocator, ReqContext.class, ReqData.class);
    }
    
    private static <T> ForeignActiveDescriptor<T> tellAppLocatorAboutFirstComponentService(ServiceLocator appLocator,
            ServiceLocator childLocator,
            ActiveDescriptor<T> requestScopedDescriptor) {
        ForeignActiveDescriptor<T> componentService = new ForeignActiveDescriptor<T>(appLocator.getService(OperationManager.class),
                childLocator, requestScopedDescriptor);
        
        ServiceLocatorUtilities.addOneDescriptor(appLocator, componentService, false);
        
        return componentService;
    }
    
    private static <T> void tellAppLocatorAboutSubsequentComponentService(
            ServiceLocator childLocator,
            ForeignActiveDescriptor<T> firstDescriptor,
            ActiveDescriptor<T> subsequentDescriptor) {
        firstDescriptor.addSimilarChild(childLocator, subsequentDescriptor);
    }
    
    private static ActiveDescriptor<?> getReqDataDescriptor(ServiceLocator componentLocator) {
        return componentLocator.getBestDescriptor(BuilderHelper.createContractFilter(ReqData.class.getName()));
    }

    /**
     * Tests that the proxy application works with a single ServiceLocator
     */
    @Test // @org.junit.Ignore
    public void testSingleAppWorksFine() {
        final ServiceLocator appLocator = newApplicationLocator(TEST_NAME + "_SingleApp");
        final ServiceLocator componentLocator = newComponentLocator(TEST_NAME + "_SingleComponent", appLocator);
        tellAppLocatorAboutFirstComponentService(appLocator, componentLocator, getReqDataDescriptor(componentLocator));

        final ReqContext request = componentLocator.getService(ReqContext.class);
        assertThat(request, is(notNullValue()));

        // req one:
        request.startRequest();
        ReqData reqData = componentLocator.getService(ReqData.class);
        assertThat(reqData, is(notNullValue()));
        reqData.setRequestName("one");

        final GlobalComponent globalComponentOne = componentLocator.getService(GlobalComponent.class);
        assertThat(globalComponentOne.getRequestName(), is(equalTo("one")));
        request.stopRequest();

        // req two:
        request.startRequest();
        reqData = componentLocator.getService(ReqData.class);
        assertThat(reqData, is(notNullValue()));
        reqData.setRequestName("two");

        final GlobalComponent globalComponentTwo = componentLocator.getService(GlobalComponent.class);
        assertThat(globalComponentOne.getRequestName(), is(equalTo("two")));
        assertThat(globalComponentTwo, is(equalTo(globalComponentOne)));
        request.stopRequest();
    }

    /**
     * Tests the same app as above but working on two different ServiceLocators
     * at once.  The two locators should not interfere with each other
     */
    @SuppressWarnings("unchecked")
    @Test // @Ignore
    public void testTwoAppsWorkFine() {
        // create the application locator
        final ServiceLocator appLocator = newApplicationLocator(TEST_NAME + "_MultiApp");
        
        // create two "apps"
        final ServiceLocator adamAppLocator = newComponentLocator(TEST_NAME + "_AdamApp", appLocator);
        final ServiceLocator evaAppLocator = newComponentLocator(TEST_NAME + "_EvaApp", appLocator);
        
        // Ensure the global knows about the request services
        ForeignActiveDescriptor<?> firstDesc = tellAppLocatorAboutFirstComponentService(appLocator, adamAppLocator, getReqDataDescriptor(adamAppLocator));
        tellAppLocatorAboutSubsequentComponentService(evaAppLocator, (ForeignActiveDescriptor<ReqData>) firstDesc,
                (ActiveDescriptor<ReqData>) getReqDataDescriptor(evaAppLocator));

        // get app context from both
        final ReqContext adamRequest = adamAppLocator.getService(ReqContext.class);
        assertThat(adamRequest, is(notNullValue()));

        final ReqContext evaRequest = evaAppLocator.getService(ReqContext.class);
        assertThat(evaRequest, is(notNullValue()));

        // req Adam/one:
        adamRequest.startRequest();
        ReqData reqData = adamAppLocator.getService(ReqData.class);
        assertThat(reqData, is(notNullValue()));
        reqData.setRequestName("adam/one");
        
        final GlobalComponent globalComponentOne = adamAppLocator.getService(GlobalComponent.class);
        assertThat(globalComponentOne.getRequestName(), is(equalTo("adam/one")));
        adamRequest.stopRequest();

        // req Adam/two:
        adamRequest.startRequest();
        reqData = adamAppLocator.getService(ReqData.class);
        assertThat(reqData, is(notNullValue()));
        reqData.setRequestName("adam/two");

        final GlobalComponent globalComponentTwo = adamAppLocator.getService(GlobalComponent.class);
        assertThat(globalComponentTwo.getRequestName(), is(equalTo("adam/two")));
        assertThat(globalComponentTwo, is(equalTo(globalComponentOne)));
        // uncomment this to see Adam request data "leaking" into the global component for Eva
        adamRequest.stopRequest();

        // req Eva/one:
        evaRequest.startRequest();
        reqData = evaAppLocator.getService(ReqData.class);
        assertThat(reqData, is(notNullValue()));
        reqData.setRequestName("eva/one");

        final GlobalComponent globalComponentEvaOne = evaAppLocator.getService(GlobalComponent.class);
        assertThat(globalComponentEvaOne.getRequestName(), is(equalTo("eva/one")));
        assertThat(globalComponentEvaOne, is(equalTo(globalComponentTwo)));
        evaRequest.stopRequest();
    }
}
