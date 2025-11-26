package com.boot.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import com.boot.dao.UserDAO;
import com.boot.dto.UserDTO;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserDAO userDAO;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {

        UserDTO user = userDAO.getUserById(userId);

        if (user == null) {
            throw new UsernameNotFoundException("존재하지 않는 사용자입니다: " + userId);
        }

        return new CustomUserDetails(user);
    }
}
