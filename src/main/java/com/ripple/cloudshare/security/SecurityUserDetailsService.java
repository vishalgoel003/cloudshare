package com.ripple.cloudshare.security;

import com.ripple.cloudshare.data.dao.UserDAOService;
import com.ripple.cloudshare.data.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SecurityUserDetailsService implements UserDetailsService {

    private final UserDAOService userDAOService;

    public SecurityUserDetailsService(UserDAOService userDAOService) {
        this.userDAOService = userDAOService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDAOService.getByEmail(username);
        return SecurityUser.create(user);
    }

    public SecurityUser loadUserById(Long id) throws UsernameNotFoundException {
        User user = userDAOService.getById(id);
        return SecurityUser.create(user);
    }

}
