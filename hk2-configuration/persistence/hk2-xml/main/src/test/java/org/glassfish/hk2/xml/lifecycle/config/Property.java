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

package org.glassfish.hk2.xml.lifecycle.config;

import java.beans.PropertyVetoException;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * @author jwells
 *
 */
public interface Property {
    /**
     * Gets the value of the name property.
     *
     * @return possible object is
     *         {@link String }
     */
    @XmlAttribute(required = true /*, key=true */)
    public String getName();

    /**
     * Sets the value of the name property.
     *
     * @param value allowed object is
     *              {@link String }
     * @throws PropertyVetoException if a listener vetoes the change
     */
    public void setName(String value) throws PropertyVetoException;

    /**
     * Gets the value of the value property.
     *
     * @return possible object is
     *         {@link String }
     */
    @XmlAttribute(required = true)
    public String getValue();

    /**
     * Sets the value of the value property.
     *
     * @param value allowed object is
     *              {@link String }
     * @throws PropertyVetoException if a listener vetoes the change
     */
    public void setValue(String value) throws PropertyVetoException;

    /**
     * Gets the value of the description property.
     *
     * @return possible object is
     *         {@link String }
     */
    @XmlAttribute
    public String getDescription();

    /**
     * Sets the value of the description property.
     *
     * @param value allowed object is
     *              {@link String }
     * @throws PropertyVetoException if a listener vetoes the change
     */
    public void setDescription(String value) throws PropertyVetoException;

}
