package com.my_hourly.security.util;

import com.my_hourly.authentication.entity.User;
import com.my_hourly.security.user.CustomUserDetails;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;



public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static Authentication getAuthentication() {

        return SecurityContextHolder
                .getContext()
                .getAuthentication();

    }

    public static boolean isAuthenticated() {

        Authentication authentication = getAuthentication();

        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);

    }

    public static CustomUserDetails getCurrentUserDetails() {

        if (!isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found.");
        }

        Object principal = getAuthentication().getPrincipal();

        if (!(principal instanceof CustomUserDetails customUserDetails)) {
            throw new IllegalStateException("Invalid authenticated principal.");
        }

        return customUserDetails;
    }

    public static User getCurrentUser() {
        return getCurrentUserDetails().getUser();
    }

    public static Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    public static String getCurrentUsername() {
        return getCurrentUser().getUsername();
    }

}
