package com.kyc.kycapp.controller;

import com.kyc.kycapp.repository.UserRepository;
import com.kyc.kycapp.repository.KycRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

@Controller
public class LoginController {

    private final UserRepository userRepository;
    private final KycRepository kycRepository;
    private final MessageSource messageSource;

    @Value("${login.max.attempts}")
    private int loginMaxAttempts;

    @Value("${login.lockout.minutes}")
    private int loginLockoutMinutes;

    public LoginController(
            UserRepository userRepository,
            KycRepository kycRepository,
            MessageSource messageSource) {
        this.userRepository = userRepository;
        this.kycRepository = kycRepository;
        this.messageSource = messageSource;
    }

    @GetMapping({ "", "/", "/login" })
    public String showLogin(HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username != null) {
            if (kycRepository.existsByUsername(username)) {
                return "redirect:/success";
            }
            return "redirect:/pan";
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        // Check if login is locked out
        Long lockoutTime = (Long) session.getAttribute("loginLockoutTime");
        if (lockoutTime != null && System.currentTimeMillis() < lockoutTime) {
            long remainingMinutes = (lockoutTime - System.currentTimeMillis()) / (60 * 1000);
            String msg = messageSource.getMessage(
                    "error.login.locked",
                    new Object[] { remainingMinutes + 1 },
                    LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("error", msg);
            redirectAttributes.addFlashAttribute("locked", true);
            return "redirect:/";
        } else if (lockoutTime != null && System.currentTimeMillis() >= lockoutTime) {
            session.removeAttribute("loginLockoutTime");
            session.removeAttribute("loginAttempts");
        }

        return userRepository
                .findByUsernameAndPassword(username, password)
                .map(user -> {
                    session.removeAttribute("loginAttempts");
                    session.removeAttribute("loginLockoutTime");
                    session.setAttribute("username", user.getUsername());

                    // Check if already submitted
                    if (kycRepository.existsByUsername(user.getUsername())) {
                        session.setAttribute("kycStep", "SUBMITTED");
                        return "redirect:/success";
                    }

                    session.setAttribute("kycStep", "LOGIN_COMPLETED");
                    return "redirect:/pan";
                })
                .orElseGet(() -> {
                    Integer attempts = (Integer) session.getAttribute("loginAttempts");
                    attempts = (attempts == null) ? 1 : attempts + 1;
                    session.setAttribute("loginAttempts", attempts);

                    int remainingAttempts = loginMaxAttempts - attempts;

                    if (remainingAttempts <= 0) {
                        long newLockoutTime = System.currentTimeMillis() + (loginLockoutMinutes * 60L * 1000);
                        session.setAttribute("loginLockoutTime", newLockoutTime);
                        session.setAttribute("loginAttempts", 0);

                        String msg = messageSource.getMessage(
                                "error.login.locked",
                                new Object[] { loginLockoutMinutes },
                                LocaleContextHolder.getLocale());
                        redirectAttributes.addFlashAttribute("error", msg);
                        redirectAttributes.addFlashAttribute("locked", true);
                    } else {
                        String msg = messageSource.getMessage(
                                "error.login.attempts_remaining",
                                new Object[] { remainingAttempts },
                                LocaleContextHolder.getLocale());
                        redirectAttributes.addFlashAttribute("error", msg);
                    }

                    return "redirect:/";
                });
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
