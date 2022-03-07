package com.leadon.apigw.web.util;

import com.leadon.apigw.model.common.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

public class SecurityUtils {

    public static List<String> getAuthorities() {
        List<String> results = new ArrayList<>();
        List<GrantedAuthority> authorities = (List<GrantedAuthority>) (SecurityContextHolder.getContext().getAuthentication().getAuthorities());
        for (GrantedAuthority authority : authorities) {
            results.add(authority.getAuthority());
        }
        return results;
    }

    public static boolean isAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication().isAuthenticated();
    }

    public static String getName() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public static boolean isResetPassword() {
        return getPrincipal().isResetPassword();
    }

    public static String getStatus() {
        return getPrincipal().getStatus();
    }

    public static String getUserName() {
        return getPrincipal().getUsername();
    }

    public static CustomUserDetails getPrincipal() {
        return (CustomUserDetails) (SecurityContextHolder.getContext()).getAuthentication().getPrincipal();
    }

    public static void customLogout(HttpServletRequest request , HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
    }
}
