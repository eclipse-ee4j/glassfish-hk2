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

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import org.jvnet.hk2.annotations.Contract;

@Contract
public interface Environments {
  @XmlElement(name="environment")
  void setEnvironments(List<Environment> environments);
  List<Environment> getEnvironments();

  /*
  @DuckTyped
  Environment getOrCreateEnvironment(String name);

  @DuckTyped
  Environment createEnvironment(String name) throws TransactionFailure;

  @DuckTyped
  Environment getEnvironmentByName(String name);

  @DuckTyped
  Environment deleteEnvironment(Environment environment);

  @DuckTyped
  PartitionRef getPartitionRef(Partition partition);

  @DuckTyped
  Environment getReferencedEnvironment(Partition partition);

  class Duck  {
    public static Environment createEnvironment(final Environments environments, final String name) throws TransactionFailure {
      ConfigSupport.apply(new SingleConfigCode<Environments>() {
        @Override
        public Object run(Environments writeableEnvironments) throws TransactionFailure, PropertyVetoException {
          Environment environment = writeableEnvironments.createChild(Environment.class);
          environment.setName(name);
          writeableEnvironments.getEnvironments().add(environment);
          Associations associations = environment.createChild(Associations.class);
          environment.setAssociations(associations);
          return environment;
        }
      }, environments);

      // read-only view
      return getEnvironmentByName(environments, name);
    }

    public static Environment getEnvironmentByName(final Environments environments, final String name) {
      List<Environment> environmentConfigsList = environments.getEnvironments();
      for (Environment environment : environmentConfigsList) {
        if (name.equals(environment.getName())) {
          return environment;
        }
      }
      return null;
    }

    public static Environment getOrCreateEnvironment(final Environments environments, String name) throws TransactionFailure {
      Environment environment = getEnvironmentByName(environments, name);
      if (environment == null) {
        @SuppressWarnings("unused")
        Environment writeableEnvironment = createEnvironment(environments, name);
          return getEnvironmentByName(environments, name);
      } else {
          return environment;
      }
    }

    public static Environment deleteEnvironment(final Environments environments,
        final Environment environment) throws TransactionFailure {
      return (Environment) ConfigSupport.apply(new SingleConfigCode<Environments>() {

        @Override
        public Object run(Environments writeableEnvironments)
            throws TransactionFailure {
          writeableEnvironments.getEnvironments().remove(environment);
          return environment; 
        }

      }, environments);
    
    }

    public static PartitionRef getPartitionRef(final Environments environments,
        final Partition partition) throws TransactionFailure {
      List<Environment> environmentConfigs = environments.getEnvironments();
      for (Environment environment : environmentConfigs) {
    	PartitionRef partitionRef = environment.getPartitionRefById(partition.getId());
    	if (partitionRef != null) {
    		return partitionRef;
    	}
      }
      return null;
    }

    public static Environment getReferencedEnvironment(final Environments environments,
        final Partition partition) throws TransactionFailure {
      PartitionRef partitionRef = environments.getPartitionRef(partition);
      if (partitionRef != null) {
        return partitionRef.getParent(Environment.class);
      } else {
        return null;
      }
    }
  }
  */
}
