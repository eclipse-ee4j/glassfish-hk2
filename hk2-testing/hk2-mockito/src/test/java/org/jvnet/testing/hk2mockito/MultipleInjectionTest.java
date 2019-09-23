package org.jvnet.testing.hk2mockito;

import org.jvnet.testing.hk2mockito.fixture.BasicGreetingService;
import org.jvnet.testing.hk2mockito.fixture.NamedGreetingService;
import org.jvnet.testing.hk2mockito.fixture.service.MultipleConstructorInjectionService;
import org.jvnet.testing.hk2testng.HK2;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockingDetails;

/**
 *
 * @author Attila Houtkooper
 */
@HK2
public class MultipleInjectionTest {

    @SUT
    @Inject
    private MultipleConstructorInjectionService sut;

    @MC
    @Inject
    private BasicGreetingService collaborator1;

    @MC
    @Inject
    private NamedGreetingService collaborator2;

    @BeforeClass
    public void verifyInjection() {
        assertThat(sut).isNotNull();
        assertThat(collaborator1).isNotNull();
        assertThat(collaborator2).isNotNull();
        assertThat(mockingDetails(sut).isMock()).isTrue();
        assertThat(mockingDetails(sut).isSpy()).isTrue();
        assertThat(mockingDetails(collaborator1).isMock()).isTrue();
        assertThat(mockingDetails(collaborator2).isMock()).isTrue();
        assertThat(mockingDetails(collaborator1).isSpy()).isFalse();
        assertThat(mockingDetails(collaborator2).isSpy()).isFalse();
    }

    @Test
    public void injectedServicesEqualSutProperties() {
        assertThat(sut.basicGreetingService).isEqualTo(collaborator1);
        assertThat(sut.namedGreetingService).isEqualTo(collaborator2);
    }

}
