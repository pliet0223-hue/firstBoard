package com.example.newboard.web.view;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * 뷰에서 ${currentUser}로 로그인 사용자 표시.
 * - 기본: authentication.getName() (대개 이메일/username)
 * - OAuth2 로그인 시 name/email 추출 시도
 * - 인증 안 됐으면 null
 */
@ControllerAdvice(annotations = Controller.class)
public class CurrentUserAdvice {

    @ModelAttribute("currentUser")
    public String currentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        // 1) 가장 안전한 기본값: username(=이메일)
        String username = authentication.getName();
        if (username != null && !username.isBlank()) {
            return username;
        }

        // 2) UserDetails (폼 로그인)
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails ud) {
            return ud.getUsername();
        }

        // 3) OAuth2 로그인
        if (principal instanceof OAuth2User ou) {
            Object name = ou.getAttributes().get("name");
            if (name != null) return String.valueOf(name);
            Object email = ou.getAttributes().get("email");
            if (email != null) return String.valueOf(email);
        }

        return null;
    }
}
