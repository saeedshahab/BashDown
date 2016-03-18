package com.saeedshahab.bashdown.auth;

import com.google.common.base.Optional;
import com.saeedshahab.bashdown.models.User;
import com.saeedshahab.bashdown.models.mock.MockUser;
import com.saeedshahab.bashdown.repositories.UserRepository;
import io.dropwizard.auth.basic.BasicCredentials;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class BashAuthenticatorTest {

    private BashAuthenticator bashAuthenticator;

    @Mock
    private UserRepository userRepository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        bashAuthenticator = new BashAuthenticator(userRepository);
    }

    @Test
    public void testAuthenticatePass() throws Exception {
        User user = MockUser.builder()
                .id(UUID.randomUUID().toString())
                .displayName("saeed")
                .name("Saeed Shahab")
                .email("saeed.0007@gmail.com")
                .password("mypassw0rd")
                .build();
        Mockito.when(userRepository.searchOne(anyMapOf(String.class, Object.class))).thenReturn(Optional.of(user));
        BasicCredentials credentials = new BasicCredentials("saeed", "mypassw0rd");

        Optional<User> userOptional = bashAuthenticator.authenticate(credentials);

        assertThat(userOptional.isPresent()).isTrue();
        assertThat(userOptional.get()).isEqualTo(user);

        verify(userRepository, times(1)).searchOne(anyMapOf(String.class, Object.class));
    }

    @Test
    public void testAuthenticateFail() throws Exception {
        Mockito.when(userRepository.searchOne(anyMapOf(String.class, Object.class))).thenReturn(Optional.absent());
        BasicCredentials credentials = new BasicCredentials("saeed", "mypassw0rd");

        Optional<User> userOptional = bashAuthenticator.authenticate(credentials);

        assertThat(userOptional.isPresent()).isFalse();

        verify(userRepository, times(1)).searchOne(anyMapOf(String.class, Object.class));
    }
}