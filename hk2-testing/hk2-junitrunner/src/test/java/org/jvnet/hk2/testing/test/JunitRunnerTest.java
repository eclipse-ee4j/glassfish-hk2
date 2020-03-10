package org.jvnet.hk2.testing.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.jvnet.hk2.testing.junit.HK2JunitRunner;

import jakarta.inject.Inject;

import static org.junit.Assert.assertNotNull;

@RunWith(HK2JunitRunner.class)
public class JunitRunnerTest {

    @Inject
    private SimpleService injectMe;

    @Test
    public void testServiceInjection() {
        assertNotNull(injectMe);
    }
}
