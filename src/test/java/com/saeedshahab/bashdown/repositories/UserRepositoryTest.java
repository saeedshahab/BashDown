package com.saeedshahab.bashdown.repositories;

import com.google.common.base.Optional;
import com.saeedshahab.bashdown.models.User;
import com.saeedshahab.bashdown.wrappers.DatabaseWrapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class UserRepositoryTest {

    private UserRepository userRepository;

    private User user;

    @Mock
    private DatabaseWrapper databaseWrapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        userRepository = new UserRepository(databaseWrapper);

        user = new User();
    }

    @Test
    public void testCreatePass() throws Exception {
        String displayName = "username";
        String name = "Name";
        String email = "user@e.mail";
        String password = "password";

        user.setId(null);
        user.setDisplayName(displayName);
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);

        when(databaseWrapper.create(user, User.class)).thenReturn(user);

        Optional<User> userOptional = userRepository.create(user);

        assertThat(userOptional.isPresent()).isTrue();

        User userReturn = userOptional.get();
        assertThat(userReturn.getId()).isNotNull();
        assertThat(userReturn.getDisplayName()).isEqualTo(displayName);
        assertThat(userReturn.getName()).isEqualTo(name);
        assertThat(userReturn.getEmail()).isEqualTo(email);
        assertThat(userReturn.getPassword()).isEqualTo("*******");
        verify(databaseWrapper, times(1)).create(user, User.class);
    }

    @Test
    public void testCreateFail() throws Exception {
        when(databaseWrapper.create(user, User.class)).thenThrow(new RuntimeException("database fail"));

        Optional<User> userOptional = userRepository.create(user);

        assertThat(userOptional.isPresent()).isFalse();
        verify(databaseWrapper, times(1)).create(user, User.class);
    }

    @Test
    public void testSearchOnePassWithRoles() throws Exception {
        String id = UUID.randomUUID().toString();
        String role = "role";
        user.setId(id);
        user.setPassword("passw0rd");
        user.setRoles(Collections.singletonList(role));
        Map<String, Object> query = Collections.singletonMap("id", id);
        when(databaseWrapper.search(query, User.class)).thenReturn(Collections.singletonList(user));

        Optional<User> userOptional = userRepository.searchOne(query);

        assertThat(userOptional.isPresent()).isTrue();

        User userReturn = userOptional.get();
        assertThat(userReturn.getId()).isEqualTo(id);
        assertThat(userReturn.getPassword()).isEqualTo("*******");
        assertThat(userReturn.getRoles()).isNotNull();
        assertThat(userReturn.getRoles().size()).isEqualTo(1);
        assertThat(userReturn.getRoles().get(0)).isEqualTo(role);
        verify(databaseWrapper, times(1)).search(query, User.class);
    }

    @Test
    public void testSearchOnePassWithoutRoles() throws Exception {
        String id = UUID.randomUUID().toString();
        user.setId(id);
        user.setPassword("passw0rd");
        user.setRoles(null);
        Map<String, Object> query = Collections.singletonMap("id", id);
        when(databaseWrapper.search(query, User.class)).thenReturn(Collections.singletonList(user));

        Optional<User> userOptional = userRepository.searchOne(query);

        assertThat(userOptional.isPresent()).isTrue();

        User userReturn = userOptional.get();
        assertThat(userReturn.getId()).isEqualTo(id);
        assertThat(userReturn.getPassword()).isEqualTo("*******");
        assertThat(userReturn.getRoles()).isNotNull();
        assertThat(userReturn.getRoles().size()).isEqualTo(0);
        verify(databaseWrapper, times(1)).search(query, User.class);
    }

    @Test
    public void testSearchOneNotFound() throws Exception {
        String id = UUID.randomUUID().toString();
        Map<String, Object> query = Collections.singletonMap("id", id);
        when(databaseWrapper.search(query, User.class)).thenReturn(Collections.emptyList());

        Optional<User> userOptional = userRepository.searchOne(query);

        assertThat(userOptional.isPresent()).isFalse();
        verify(databaseWrapper, times(1)).search(query, User.class);
    }

    @Test
    public void testSearchOneFail() throws Exception {
        String id = UUID.randomUUID().toString();
        user.setId(id);
        Map<String, Object> query = Collections.singletonMap("id", id);
        when(databaseWrapper.search(query, User.class)).thenThrow(new RuntimeException("database fail"));

        Optional<User> userOptional = userRepository.searchOne(query);

        assertThat(userOptional.isPresent()).isFalse();
        verify(databaseWrapper, times(1)).search(query, User.class);
    }
}
