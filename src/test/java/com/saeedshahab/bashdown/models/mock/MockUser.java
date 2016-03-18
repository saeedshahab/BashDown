package com.saeedshahab.bashdown.models.mock;

import com.saeedshahab.bashdown.models.User;
import org.mockito.Mockito;

public class MockUser extends User {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private User user = Mockito.mock(User.class);

        public Builder id(String id) {
            Mockito.when(user.getId()).thenReturn(id);
            return this;
        }

        public Builder displayName(String displayName) {
            Mockito.when(user.getDisplayName()).thenReturn(displayName);
            return this;
        }

        public Builder name(String name) {
            Mockito.when(user.getName()).thenReturn(name);
            return this;
        }

        public Builder email(String email) {
            Mockito.when(user.getEmail()).thenReturn(email);
            return this;
        }

        public Builder password(String password) {
            Mockito.when(user.getPassword()).thenReturn(password);
            return this;
        }

        public User build() {
            return user;
        }

    }

}

