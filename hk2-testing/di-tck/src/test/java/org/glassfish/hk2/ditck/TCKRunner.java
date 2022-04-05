/*
 * Copyright (c) 2021 Payara Services Ltd. and/or its affiliates. All rights reserved.
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
package org.glassfish.hk2.ditck;

import jakarta.inject.Singleton;
import org.atinject.tck.Tck;
import org.atinject.tck.auto.*;
import org.atinject.tck.auto.accessories.*;
import org.glassfish.hk2.api.AnnotationLiteral;
import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;

/**
 * Runner for the DI TCK
 * @author Jonathan Coustick
 */
public class TCKRunner {

    public static junit.framework.Test suite() {

        ServiceLocator locator = ServiceLocatorUtilities.createAndPopulateServiceLocator("test-locator");
        DynamicConfiguration dynamicConfig = ServiceLocatorUtilities.createDynamicConfiguration(locator);
        
        dynamicConfig.bind(BuilderHelper.link(FuelTank.class).build());
        dynamicConfig.bind(BuilderHelper.link(Seat.class).in(Singleton.class).build());
        dynamicConfig.bind(BuilderHelper.activeLink(DriversSeat.class).to(Seat.class).qualifiedBy(new DriverAnnotation()).build());
        dynamicConfig.bind(BuilderHelper.link(Seatbelt.class).build());
        dynamicConfig.bind(BuilderHelper.link(V8Engine.class).to(GasEngine.class).to(Engine.class).build());
        dynamicConfig.bind(BuilderHelper.link(Cupholder.class).in(Singleton.class).build());
        dynamicConfig.bind(BuilderHelper.link(Tire.class).build());
        dynamicConfig.bind(BuilderHelper.link(SpareTire.class).to(Tire.class).named("spare").build());
        dynamicConfig.bind(BuilderHelper.link(Convertible.class).to(Car.class).build());
        
        dynamicConfig.commit();
        Car tckCar = locator.getService(Car.class);
        return Tck.testsFor(tckCar, false, true);
    }
    
    static class DriverAnnotation extends AnnotationLiteral<Drivers> implements Drivers {
        
    }

}
