package com.ripple.cloudshare.security;

import com.ripple.cloudshare.data.entity.User;
import com.ripple.cloudshare.data.entity.UserType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class SecurityUser implements UserDetails {

    private Long id;
    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private boolean markedDeleted;

    public SecurityUser(Long id, String email, String password, UserType userType, boolean markedDeleted) {
        this.id = id;
        this.username = email;
        this.password = password;
        //Took so many hours to figure out Spring needs all roles to start with: ROLE_ and get RBAC working
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + userType.name()));
        this.markedDeleted = markedDeleted;
    }

    public static SecurityUser create(User user) {
        return new SecurityUser(user.getId(), user.getEmail(), user.getPassword(), user.getUserType(), user.getDeleted());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return !markedDeleted;
    }

    public Long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SecurityUser that = (SecurityUser) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
