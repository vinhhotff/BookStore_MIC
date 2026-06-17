package com.example.bookstore.auth;

import com.example.bookstore.user.UserCreationRequest;
import com.example.bookstore.exception.AppException;
import com.example.bookstore.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * MVC Controller handling registration views and processing.
 */
@Controller
@RequiredArgsConstructor
public class RegisterViewController {

    private final UserService userService;

    /**
     * Renders the registration page. If the user is already authenticated,
     * redirects them to the book catalog page.
     *
     * @return the name of the register Thymeleaf template (register.html) or a redirect path.
     */
    @GetMapping("/register")
    public String showRegisterPage() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/books";
        }
        return "register";
    }

    /**
     * Processes user registration submitted from the web form.
     *
     * @param username  the username chosen by the user.
     * @param password  the password.
     * @param firstName the user's first name.
     * @param lastName  the user's last name.
     * @param model     the UI Model to pass back input fields and error messages.
     * @return redirect path on success, or the register template name on failure.
     */
    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String email,
                               @RequestParam String password,
                               @RequestParam String firstName,
                               @RequestParam String lastName,
                               Model model) {
        try {
            // Build creation request
            UserCreationRequest request = UserCreationRequest.builder()
                    .username(username)
                    .email(email)
                    .password(password)
                    .firstName(firstName)
                    .lastName(lastName)
                    .build();

            // Call Service to create user
            userService.createUser(request);

            // Redirect to login page on success with a parameter
            return "redirect:/login?registerSuccess";

        } catch (AppException e) {
            // If business validation fails, show error and preserve input values
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("username", username);
            model.addAttribute("email", email);
            model.addAttribute("firstName", firstName);
            model.addAttribute("lastName", lastName);
            return "register";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Đăng ký thất bại: " + e.getMessage());
            model.addAttribute("username", username);
            model.addAttribute("email", email);
            model.addAttribute("firstName", firstName);
            model.addAttribute("lastName", lastName);
            return "register";
        }
    }
}
