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

//import javax.validation.Payload;
//import javax.validation.constraints.NotNull;

// @ReferenceConstraint(skipDuringCreation=false, payload=PartitionRef.class)
public interface PartitionRef extends PropertyBag, Payload, Auditable {
	
  /**
   * Id of the referenced partition.
   *
   * @return name
   */
  @XmlAttribute(required=true /*, key=true */)
  // @NotNull
  // @ReferenceConstraint.RemoteKey(message="{resourceref.invalid.configref}", type=Partition.class)
  public String getId();
  public void setId(String value) throws PropertyVetoException;

  /**
   * Name of the runtime of the referenced partition.
   *
   * @return name
   */
  @XmlAttribute(required=true /* , key=false */)
  // @NotNull
  // @ReferenceConstraint.RemoteKey(message="{resourceref.invalid.configref}", type=Runtime.class)
  public String getRuntimeRef();
  public void setRuntimeRef(String value) throws PropertyVetoException;

  /*
  @DuckTyped
  Runtime getRuntime();

  @DuckTyped
  Environment getEnvironment();

  class Duck {

    public static Runtime getRuntime(final PartitionRef partitionRef) {
      LifecycleConfigBean bean = (LifecycleConfigBean) Dom.unwrap(partitionRef);
      ServiceLocator serviceLocator = bean.getHabitat();
      return serviceLocator.getService(Runtime.class, partitionRef.getRuntimeRef());
    }

    public static Environment getEnvironment(final PartitionRef partitionRef) {
      return partitionRef.getParent(Environment.class);
    }
  }
  */
}
