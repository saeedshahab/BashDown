package com.saeedshahab.bashdown.models;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ MessageDigest.class, User.class })
public class UserTest {

    @Test
    public void testSha1HashInvalidAlgorithm() throws Exception {
        PowerMockito.mockStatic(MessageDigest.class);
        PowerMockito.when(MessageDigest.getInstance("SHA1")).thenThrow(new NoSuchAlgorithmException());
        assertThat(User.sha1Hash("string")).isEqualTo(null);
    }
}