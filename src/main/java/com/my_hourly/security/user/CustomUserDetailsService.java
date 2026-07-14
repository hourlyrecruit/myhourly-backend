package com.my_hourly.security.user;

import com.my_hourly.authentication.entity.Role;
import com.my_hourly.authentication.entity.RolePermission;
import com.my_hourly.authentication.entity.UserRole;
import com.my_hourly.authentication.repository.RolePermissionRepository;
import com.my_hourly.authentication.repository.UserRepository;
import com.my_hourly.authentication.repository.UserRoleRepository;
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
    private final UserRoleRepository userRoleRepository;

    private final RolePermissionRepository rolePermissionRepository;

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

        List<UserRole> userRoles =
                userRoleRepository.findAllByUser(user);

        for (UserRole userRole : userRoles) {

            Role role = userRole.getRole();

            authorities.add(
                    new SimpleGrantedAuthority(
                            "ROLE_" + role.getName()
                    )
            );

            List<RolePermission> rolePermissions =
                    rolePermissionRepository.findAllByRole(role);

            for (RolePermission rolePermission : rolePermissions) {

                authorities.add(
                        new SimpleGrantedAuthority(
                                rolePermission
                                        .getPermission()
                                        .getName()
                        )
                );

            }

        }

        return authorities;

    }

}
