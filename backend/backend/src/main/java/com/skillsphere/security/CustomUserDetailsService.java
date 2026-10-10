package com.skillsphere.security;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.skillsphere.entity.User;
import com.skillsphere.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        String rawRole = user.getRole() != null && !user.getRole().trim().isEmpty() 
                ? user.getRole().trim().toUpperCase() 
                : "STUDENT";

        String roleWithPrefix = rawRole.startsWith("ROLE_") ? rawRole : "ROLE_" + rawRole;
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(roleWithPrefix);

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                List.of(authority)
        );
    }
}
