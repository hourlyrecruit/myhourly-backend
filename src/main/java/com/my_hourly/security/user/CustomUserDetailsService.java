package com.my_hourly.security.user;

import com.my_hourly.authentication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import com.my_hourly.authentication.entity.User;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class CustomUserDetailsService
        implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail)
            throws UsernameNotFoundException {

        User user = userRepository
                .findByUsernameOrEmail(
                        usernameOrEmail,
                        usernameOrEmail
                )
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Invalid username or email."
                        ));

        Collection<GrantedAuthority> authorities = getAuthorities(user);

        return new CustomUserDetails(user, authorities);

    }

    private Collection<GrantedAuthority> getAuthorities(User user) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        com.my_hourly.common.enums.RoleName role = user.getRole();
        if (role != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));
            switch (role) {
                case SUPER_ADMIN:
                case MANAGER:
                case HR_ADMIN:
                    authorities.add(new SimpleGrantedAuthority("department:create"));
                    authorities.add(new SimpleGrantedAuthority("department:view"));
                    authorities.add(new SimpleGrantedAuthority("department:update"));
                    authorities.add(new SimpleGrantedAuthority("department:delete"));
                    authorities.add(new SimpleGrantedAuthority("designation:create"));
                    authorities.add(new SimpleGrantedAuthority("designation:view"));
                    authorities.add(new SimpleGrantedAuthority("designation:update"));
                    authorities.add(new SimpleGrantedAuthority("designation:delete"));
                    authorities.add(new SimpleGrantedAuthority("employee:view"));
                    authorities.add(new SimpleGrantedAuthority("employee:viewAll"));
                    authorities.add(new SimpleGrantedAuthority("employee:updateStatus"));
                    break;
                case EMPLOYEE:
                    authorities.add(new SimpleGrantedAuthority("department:view"));
                    authorities.add(new SimpleGrantedAuthority("designation:view"));
                    authorities.add(new SimpleGrantedAuthority("employee:create"));
                    authorities.add(new SimpleGrantedAuthority("employee:view"));
                    authorities.add(new SimpleGrantedAuthority("employee:update"));
                    break;
                default:
                    break;
            }
        }
        return authorities;
    }

}
