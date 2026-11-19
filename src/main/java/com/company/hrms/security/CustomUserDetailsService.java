package com.company.hrms.security;

import com.company.hrms.user.UserAccount;
import com.company.hrms.user.UserAccountRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserAccountRepository users;
    public CustomUserDetailsService(UserAccountRepository users) { this.users = users; }
    @Override public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount account = users.findByUsernameIgnoreCase(username)
            .orElseThrow(() -> new UsernameNotFoundException("Invalid username or password"));
        return User.withUsername(account.getUsername()).password(account.getPasswordHash())
            .disabled(!account.isActive())
            .authorities(account.getRoles().stream().map(r -> new SimpleGrantedAuthority("ROLE_" + r.getName())).collect(Collectors.toSet()))
            .build();
    }
}
