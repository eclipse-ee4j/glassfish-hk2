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

package org.glassfish.hk2.tests.locator.validating;

import javax.inject.Singleton;

import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.ValidationService;
import org.glassfish.hk2.api.Validator;
import org.glassfish.hk2.tests.locator.utilities.TestModule;
import org.glassfish.hk2.utilities.BuilderHelper;

/**
 * @author jwells
 *
 */
public class ValidatingModule implements TestModule {

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Module#configure(org.glassfish.hk2.api.Configuration)
     */
    @Override
    public void configure(DynamicConfiguration configurator) {
        configurator.bind(BuilderHelper.link(SuperSecretService.class).qualifiedBy(Secret.class.getName()).build());
        configurator.bind(BuilderHelper.link(SystemService.class).build());
        configurator.bind(BuilderHelper.link(UserService.class).build());
        configurator.bind(BuilderHelper.link(NeverUnbindMeService.class).build());
        
        // Add validation services
        configurator.addActiveDescriptor(ValidationServiceImpl.class);
        configurator.bind(BuilderHelper.link(BindValidatorService.class.getName()).
                to(ValidationService.class.getName()).
                in(Singleton.class.getName()).
                build());
        configurator.bind(BuilderHelper.link(UnbindValidatorService.class.getName()).
                to(ValidationService.class.getName()).
                in(Singleton.class.getName()).
                build());
        
        // This is to test validation in the parent locator
        configurator.bind(BuilderHelper.link(DynamicValidator.class.getName()).
                to(Validator.class.getName()).
                in(Singleton.class.getName()).
                build());
        configurator.bind(BuilderHelper.link(DynamicServiceImpl1.class.getName()).
                to(DynamicService.class.getName()).
                in(Singleton.class.getName()).
                build());
        configurator.bind(BuilderHelper.link(DynamicServiceImpl2.class.getName()).
                to(DynamicService.class.getName()).
                in(Singleton.class.getName()).
                build());
        configurator.bind(BuilderHelper.link(DynamicValidationServiceImpl.class.getName()).
                to(ValidationService.class.getName()).
                in(Singleton.class.getName()).
                build());
        
    }

}
