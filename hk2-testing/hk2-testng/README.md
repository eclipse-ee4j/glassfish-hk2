[//]: # " Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved. "
[//]: # "  "
[//]: # " This program and the accompanying materials are made available under the "
[//]: # " terms of the Eclipse Public License v. 2.0, which is available at "
[//]: # " http://www.eclipse.org/legal/epl-2.0. "
[//]: # "  "
[//]: # " This Source Code may also be made available under the following Secondary "
[//]: # " Licenses when the conditions for such availability set forth in the "
[//]: # " Eclipse Public License v. 2.0 are satisfied: GNU General Public License, "
[//]: # " version 2 with the GNU Classpath Exception, which is available at "
[//]: # " https://www.gnu.org/software/classpath/license.html. "
[//]: # "  "
[//]: # " SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0 "

# hk2-testng


This project adds HK2 Dependency Injection support to TestNG. It provides the ability to inject your test classes with HK2 services defined in inhibitent files and/or custom binders.

## Usage


### Service Discovery

By default the @HK2 annotation creates a new service locator with the name "hk2-testng-locator" and populates it with services defined in "META-INF/hk2-locator/default" classpath inhabitant files. Simply annotate your test class with @HK2 like this to inject discovered services:


```java
@HK2
public class PrimaryInjectionTest {

    @Inject
    PrimaryService primaryService;

    @Test
    public void assertPrimaryServiceInjecton() {
        assertThat(primaryService).isNotNull();
    }

    @Test
    public void assertSecondaryService() {
        assertThat(primaryService.getSecondaryService()).isNotNull();
    }
}
```

### Custom Binders Without Service Discovery

If service discovery and population is not desirable then you can turn it off by setting the populate parameter to false and defining your own Binder class(es):


```java
@HK2(populate = false, binders = {CombinedBinder.class})
public class BinderInjectionTest {

    @Inject
    PrimaryService primaryService;

    @Test
    public void assertPrimaryServiceInjecton() {
        assertThat(primaryService).isNotNull();
    }

    @Test
    public void assertSecondaryService() {
        assertThat(primaryService.getSecondaryService()).isNotNull();
    }
}
```

### Service Discovery and Custom Binders

You can also use both service population and your own binder class(es). Simply insure that populate flag is set to true (by default set to true) and specify your binders like this:

```java
@HK2(binders = {PrimaryBinder.class, SecondaryBinder.class})
public class MultipleBinderInjectionTest {

    @Inject
    PrimaryService primaryService;

    @Test
    public void assertPrimaryServiceInjecton() {
        assertThat(primaryService).isNotNull();
    }

    @Test
    public void assertSecondaryService() {
        assertThat(primaryService.getSecondaryService()).isNotNull();
    }
}
```

The above will create a single service locator instance named "hk2-testng-locator" that contains all the discovered services as well as services defined in your binder.


### Custom Service Locator Name

Finally if you wish to use a custom service locator name you can by specifying @HK2 annotation's value parameter:

```java
@HK2("custom")
public class CustomLocatorNameTest {

    @Inject
    ServiceLocator sericeLocator;

    @Inject
    PrimaryService primaryService;

    @Test
    public void assertPrimaryServiceInjecton() {
        assertThat(primaryService).isNotNull();
    }

    @Test
    public void assertSecondaryService() {
        assertThat(primaryService.getSecondaryService()).isNotNull();
    }

    @Test
    public void assertServiceLocatorIsCustom() {
        assertThat(sericeLocator.getName())
                .isEqualTo("custom");
    }
}
```

Note that if two test classes are annotated with @HK2("custom") then only one service locator will be created and the tests will share this service locator. 


### Isolated Service Locators

If you wish to use an isolated service locators per test or for certain tests then you will need to define a unique service locator name for these test classes:


```java
@HK2("serviceLocatorNameA")
public class Isolated1Test {
  ...
}

@HK2("serviceLocatorNameB")
public class Isolated2Test {
  ....
}
```
