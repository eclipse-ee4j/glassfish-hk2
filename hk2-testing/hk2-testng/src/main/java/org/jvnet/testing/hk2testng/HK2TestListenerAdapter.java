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

package org.jvnet.testing.hk2testng;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.extras.ExtrasUtilities;
import org.glassfish.hk2.utilities.Binder;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.testng.IConfigurable;
import org.testng.IConfigureCallBack;
import org.testng.IExecutionListener;
import org.testng.IHookCallBack;
import org.testng.IHookable;
import org.testng.ITestResult;

/**
 *
 * @author saden
 */
public class HK2TestListenerAdapter implements IExecutionListener, IHookable, IConfigurable {

    private static final Map<String, ServiceLocator> serviceLocators = new ConcurrentHashMap<String, ServiceLocator>();
    private static final Map<Class<?>, Object> testClasses = new ConcurrentHashMap<Class<?>, Object>();
    private static final Map<Class<?>, Binder> binderClasses = new ConcurrentHashMap<Class<?>, Binder>();

    @Override
    public void onExecutionStart() {
    }

    @Override
    public void onExecutionFinish() {
        for (Map.Entry<String, ServiceLocator> entry : serviceLocators.entrySet()) {
            ServiceLocatorFactory.getInstance().destroy(entry.getValue());
        }

        serviceLocators.clear();
        testClasses.clear();
        binderClasses.clear();
    }

    @Override
    public void run(IHookCallBack callBack, ITestResult testResult) {
        try {
            injectTestInstance(testResult);
            callBack.runTestMethod(testResult);
        } catch (InstantiationException e) {
            testResult.setThrowable(e);
        } catch (IllegalAccessException e) {
            testResult.setThrowable(e);
        }
    }

    @Override
    public void run(IConfigureCallBack callBack, ITestResult testResult) {
        try {
            injectTestInstance(testResult);
            callBack.runConfigurationMethod(testResult);
        } catch (InstantiationException e) {
            testResult.setThrowable(e);
        } catch (IllegalAccessException e) {
            testResult.setThrowable(e);
        }
    }

    private static void initializeServiceLocator(ServiceLocator locator, HK2 hk2) {
      if (hk2.enableImmediate()) {
        ServiceLocatorUtilities.enableImmediateScope(locator);
      }

      if (hk2.enablePerThread()) {
        ServiceLocatorUtilities.enablePerThreadScope(locator);
      }

        if (hk2.enableInheritableThread()) {
        ServiceLocatorUtilities.enableInheritableThreadScope(locator);
        }

      if (hk2.enableLookupExceptions()) {
        ServiceLocatorUtilities.enableLookupExceptions(locator);
      }

      if (hk2.enableEvents()) {
          ExtrasUtilities.enableTopicDistribution(locator);
        }

    }

    private void injectTestInstance(ITestResult testResult) throws InstantiationException, IllegalAccessException {
        ServiceLocator locator = null;
        Object testInstance = testResult.getMethod().getInstance();

        if (testInstance != null) {
            HK2 hk2 = testInstance.getClass().getAnnotation(HK2.class);

            if (hk2 != null) {
                String locatorName = hk2.value();
                if ("hk2-testng-locator".equals(locatorName)) {
                    locatorName = locatorName + "." + testInstance.getClass().getSimpleName();
                }

                ServiceLocator existingLocator = serviceLocators.get(locatorName);

                if (!testClasses.containsKey(testInstance.getClass())) {
                    Class<? extends Binder>[] hk2BinderClasses = hk2.binders();

                    if (hk2.populate()) {
                        if (existingLocator == null) {
                            locator = ServiceLocatorUtilities.createAndPopulateServiceLocator(locatorName);
                            initializeServiceLocator(locator, hk2);

                            serviceLocators.put(locator.getName(), locator);
                        }
                        else {
                            locator = existingLocator;
                        }
                    }

                    if (hk2BinderClasses.length > 0) {
                        Binder[] binders = new Binder[hk2BinderClasses.length];
                        int index = 0;
                        for (Class<? extends Binder> binderClass : hk2BinderClasses) {
                            Binder binder = binderClasses.get(binderClass);

                            if (binder == null) {
                                binder = binderClass.newInstance();
                                binderClasses.put(binderClass, binder);
                            }

                            binders[index++] = binder;
                        }

                        if (locator == null) {
                            if (existingLocator == null) {
                                locator = ServiceLocatorUtilities.bind(locatorName, binders);
                                initializeServiceLocator(locator, hk2);

                                serviceLocators.put(locator.getName(), locator);
                            }
                            else {
                                locator = existingLocator;
                                ServiceLocatorUtilities.bind(locator, binders);
                            }
                        } else {
                            ServiceLocatorUtilities.bind(locator, binders);
                        }
                    }

                    if (locator != null) {
                        locator.inject(testInstance);
                    }

                    testClasses.put(testInstance.getClass(), testInstance);
                }
            }
        }
    }

}
