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

package org.glassfish.hk2.xml.api.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Describes the string that constitutes the plural of this element or attribute name.
 * Can also be used to supply the exact method name for the adder, remover
 * and locator methods for this element.  This annotation will ONLY be read from
 * the method that also has an {@link javax.xml.bind.annotation.XmlElement} annotation
 * on it
 * <p>
 * The rules for determining the singular form of the element name is the following:<OL>
 * <LI>Remove the get or set from the method name.  The remainder is the element name</LI>
 * <LI>decapitalize the element name</LI>
 * <LI>If the element name has more than one letter and ends in &quot;s&quot;, remove the &quot;s&quot;</LI>
 * <LI>The remainder is the singular of the element name</LI>
 * </LI>
 * <p>
 * For example, if the method name is getDoctors, the singular element name will be &quot;doctor&quot;.
 * In that case the adder method will be addDoctor, the remover method will be removeDoctor and
 * the lookup method will be lookupDoctor.  In some cases the singular of a word is the same as the
 * plural.  If that word does not end in s the default behavior works fine.  For example if the
 * method name is getMoose, the singular element name will be &quot;moose&quot;.  The adder method
 * will be addMoose and so on.
 * <p>
 * In cases that do not conform to the above rule this annotation is provided, which allows the
 * user to specify what this element name is the plural of.  For example, if the method
 * name is getMice then this annotation should be used:
 * <code>
 * &#86;PluralOf("mouse")
 * </code>
 * In this case the adder method will become addMouse, the remover will be removeMouse and the
 * lookup will be lookupMouse.
 * <p>
 * This annotation can also be used to specify the exact method name that should be used for
 * the adder, remover and lookup.  If those fields are filled in the will override the algorithm
 * for determining the singular for this element name.
 * 
 * @author jwells
 *
 */
@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface PluralOf {
    /**
     * Returns the singular of the element name described by this
     * setter or getter method.  This name should be fully
     * capitalized as it should appear after the &quot;add&quot;,
     * &quot;remove&quot; or &quot;lookup&quot; in the method
     * name.
     * <p>
     * For example, if this is returning the singular for
     * &quot;mice&quot;, it should return &quot;Mouse&quot;.
     * 
     * @return The singular of the element name, or
     * {@link PluralOf#USE_NORMAL_PLURAL_PATTERN} if the
     * normal algorithm should be applied
     */
    public String value() default USE_NORMAL_PLURAL_PATTERN;
    
    /**
     * Returns the exact name of the method that should be
     * used as the adder for this element
     * 
     * @return The exact name of the method that should be
     * used as the adder for this element, or
     * {@link PluralOf#USE_NORMAL_PLURAL_PATTERN} if
     * the normal algorithm should be used (as modified
     * by the {@link PluralOf#value()} method)
     */
    public String add() default USE_NORMAL_PLURAL_PATTERN;
    
    /**
     * Returns the exact name of the method that should be
     * used as the remover for this element
     * 
     * @return The exact name of the method that should be
     * used as the remover for this element, or
     * {@link PluralOf#USE_NORMAL_PLURAL_PATTERN} if
     * the normal algorithm should be used (as modified
     * by the {@link PluralOf#value()} method)
     */
    public String remove() default USE_NORMAL_PLURAL_PATTERN;
    
    /**
     * Returns the exact name of the method that should be
     * used as the lookkup for this element
     * 
     * @return The exact name of the method that should be
     * used as the lookup for this element, or
     * {@link PluralOf#USE_NORMAL_PLURAL_PATTERN} if
     * the normal algorithm should be used (as modified
     * by the {@link PluralOf#value()} method)
     */
    public String lookup() default USE_NORMAL_PLURAL_PATTERN;
    
    /**
     * This value is used to indicate that the normal
     * algorithm should be used for determining the
     * singular of the element name
     */
    public final static String USE_NORMAL_PLURAL_PATTERN = "*";

}
