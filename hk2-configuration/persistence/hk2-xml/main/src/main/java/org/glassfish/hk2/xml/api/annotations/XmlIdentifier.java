/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
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
 * This annotation denotes the single Xml attribute
 * or element that should be used as the unique (within
 * the xpath) identifier.  The type of this Xml attribute
 * or element must be String.  There may only be one
 * attribute or element in the Java bean that uses this
 * annotation
 * <p>
 * This annotation is very much like the standard
 * JAXB annotation {@link javax.xml.bind.annotation.XmlID} except
 * that the uniqueness of this field need only be per xpath from
 * the root, and not over the entire tree.  For example, consider
 * a Java Bean such as a PropertyBean that is used all over the tree
 * for those Beans that have a set of properties.  The PropertyBean
 * might have identical keys in two different xpaths from the root, and
 * therefore could not use {@link javax.xml.bind.annotation.XmlID},
 * since the {@link javax.xml.bind.annotation.XmlID} requires uniqueness
 * over the entire tree, and not over just one xpath
 * <p>
 * Furthermore, whereas {@link javax.xml.bind.annotation.XmlID} can be referred
 * to with {@link javax.xml.bind.annotation.XmlIDREF} there is no corresponding
 * automatic reference with this annotation.  If both
 * {@link javax.xml.bind.annotation.XmlID} and this annotation are found
 * on different properties of this bean then this annotation will be used
 * in preference over {@link javax.xml.bind.annotation.XmlID}
 * 
 * @author jwells
 *
 */
@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface XmlIdentifier {

}
