package com.saeedshahab.bashdown.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.saeedshahab.bashdown.annotations.CommonName;

import javax.security.auth.Subject;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@CommonName("user")
public class User implements Principal {

    public interface Roles {
        String ADMIN = "admin";
    }

    private String id;

    @Size(min = 5)
    private String name;

    @Size(min = 1)
    private String displayName;

    @Size(min = 5) @Pattern(regexp = ".*")
    private String email;

    @Size(min = 6)
    private String password;

    private List<String> roles;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = sha1Hash(password);
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", displayName='" + displayName + '\'' +
                ", email='" + email + '\'' +
                ", password='*******'" +
                ", roles=" + roles +
                '}';
    }

    public static String sha1Hash(String input) {
        if (Objects.isNull(input)) {
            return "*******";
        }

        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }

        String hexStr = "";
        md.reset();
        md.update(input.getBytes());
        byte[] digest = md.digest();
        for (byte aDigest : digest) {
            hexStr += Integer.toString((aDigest & 0xff) + 0x100, 16).substring(1);
        }

        return hexStr;
    }
}
