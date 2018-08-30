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
import javax.xml.bind.annotation.XmlID;

import org.jvnet.hk2.annotations.Contract;

//import javax.validation.constraints.NotNull;

@Contract
public interface Partition extends PropertyBag, Auditable {

  @XmlID
  @XmlAttribute(required=true /*, key=true */)
  // @NotNull
  void setId(String id);
  String getId();
  
  
  @XmlAttribute(required=true /*, key=false */)
  // @NotNull
  void setName(String value) throws PropertyVetoException;
  String getName();

  /*
  @DuckTyped
  Runtime getRuntime();

  class Duck {

    public static Runtime getRuntime(final Partition partition) {
      return partition.getParent(Runtime.class);
    }
  }
  */
}
