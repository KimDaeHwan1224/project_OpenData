package com.boot.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.boot.dto.UserDTO;
import com.boot.service.UserService;

@Service
public class SecurityUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String user_id) throws UsernameNotFoundException {
        UserDTO user = userService.getUserById(user_id);

        if (user == null) {
            throw new UsernameNotFoundException("해당 아이디가 존재하지 않습니다: " + user_id);
        }

        return new SecurityUser(user);
    }
}
