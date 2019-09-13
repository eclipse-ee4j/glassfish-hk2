package org.jvnet.hk2.testing.junit;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.jvnet.hk2.testing.junit.internal.TestServiceLocator;

/**
 * Runner class for having injected services inside of tests using the @RunWith annotation.
 *
 * @author Attila Houtkooper
 */
public class HK2JunitRunner extends Runner implements Filterable {
    private BlockJUnit4ClassRunner runner;

    public HK2JunitRunner(Class<?> testClass) throws InitializationError {
        runner = new BlockJUnit4ClassRunner(testClass) {
            @Override
            protected Statement withBefores(FrameworkMethod method, Object target, Statement statement) {
                Statement base = super.withBefores(method, target, statement);

                return new Statement() {
                    @Override
                    public void evaluate() throws Throwable {
                        TestServiceLocator testServiceLocator = new TestServiceLocator(target);
                        testServiceLocator.initializeOnBefore();
                        base.evaluate();
                    }
                };
            }
        };
    }

    @Override
    public Description getDescription() {
        return runner.getDescription();
    }

    @Override
    public void run(RunNotifier runNotifier) {
        runner.run(runNotifier);
    }

    @Override
    public void filter(Filter filter) throws NoTestsRemainException {
        runner.filter(filter);
    }
}
