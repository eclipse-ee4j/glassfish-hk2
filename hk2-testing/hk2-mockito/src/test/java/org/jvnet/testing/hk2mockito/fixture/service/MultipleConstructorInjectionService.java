package org.jvnet.testing.hk2mockito.fixture.service;

import org.jvnet.hk2.annotations.Service;
import org.jvnet.testing.hk2mockito.fixture.BasicGreetingService;
import org.jvnet.testing.hk2mockito.fixture.NamedGreetingService;

import javax.inject.Inject;

/**
 *
 * @author Attila Houtkooper
 */
@Service
public class MultipleConstructorInjectionService {

    public BasicGreetingService basicGreetingService;
    public NamedGreetingService namedGreetingService;

    @Inject
    MultipleConstructorInjectionService(BasicGreetingService basicGreetingService, NamedGreetingService namedGreetingService) {
        this.basicGreetingService = basicGreetingService;
        this.namedGreetingService = namedGreetingService;
    }
}
