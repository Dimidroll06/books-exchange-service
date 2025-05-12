package com.dmitry.books.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.dmitry.books.config.AuthFilter.UserData;

public class SecurityUtils {

    public static UserData getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getDetails() instanceof UserData userData) {
            return userData;
        }
        return null;
    }
}