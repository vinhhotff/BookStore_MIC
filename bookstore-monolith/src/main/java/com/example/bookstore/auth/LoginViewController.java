package com.example.bookstore.auth;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * MVC Controller rendering the custom login page.
 */
@Controller
public class LoginViewController {

    /**
     * Renders the custom login page. If the user is already authenticated,
     * redirects them to the book catalog page.
     *
     * @return the name of the login Thymeleaf template (login.html) or a redirect path.
     */
    @GetMapping("/login")
    public String showLoginPage() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/books";
        }
        return "login";
    }
}

