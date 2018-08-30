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

package org.glassfish.hk2.tests.locator.qualifiers;

import org.glassfish.hk2.api.AnnotationLiteral;
import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.tests.locator.utilities.TestModule;
import org.glassfish.hk2.utilities.AbstractActiveDescriptor;
import org.glassfish.hk2.utilities.BuilderHelper;

import javax.inject.Singleton;

/**
 * @author jwells
 *
 */
public class QualifierModule implements TestModule {

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Module#configure(org.glassfish.hk2.api.Configuration)
     */
    @Override
    public void configure(DynamicConfiguration configurator) {
        configurator.bind(BuilderHelper.link(RedImpl.class).to(Color.class).qualifiedBy(Red.class.getName()).build());
        configurator.bind(BuilderHelper.link(BlueImpl.class).to(Color.class).qualifiedBy(Blue.class.getName()).build());
        configurator.bind(BuilderHelper.link(YellowImpl.class).to(Color.class).qualifiedBy(Yellow.class.getName()).build());
        
        configurator.bind(BuilderHelper.link(MauveQualified.class.getName()).qualifiedBy(Mauve.class.getName()).build());
        configurator.bind(BuilderHelper.link(MaroonQualified.class.getName()).qualifiedBy(Maroon.class.getName()).build());
        
        // Now the factory pairs
        configurator.bind(BuilderHelper.link(GreenFactory.class).
                to(Color.class).
                qualifiedBy(Green.class.getName()).
                buildFactory());
        
        configurator.bind(BuilderHelper.link(OrangeFactory.class).
                to(Color.class).
                qualifiedBy(Orange.class.getName()).
                buildFactory());
        
        configurator.bind(BuilderHelper.link(PurpleFactory.class).
                to(Color.class).
                qualifiedBy(Purple.class.getName()).
                buildFactory());
        
        // And the color wheel
        configurator.bind(BuilderHelper.link(ColorWheel.class).build());
        
        // This is to test Inheritance of qualifiers
        configurator.addActiveDescriptor(SpecifiedImplementation.class);


        Color unqualifiedColor = new Color() {
            @Override
            public String getColorName() {
                return QualifierTest.BLACK;
            }
        };

        AbstractActiveDescriptor<Color> descriptor = BuilderHelper.createConstantDescriptor(unqualifiedColor);
        descriptor.addContractType(Color.class);
        descriptor.addQualifierAnnotation(new BlackAnnotationImpl());

        configurator.bind(descriptor);

        configurator.bind(BuilderHelper.link(BlackInjectee.class).in(Singleton.class).build());
    }

    public static class BlackAnnotationImpl extends AnnotationLiteral<Black> implements Black {
    }
}
