package com.kyc.kycapp.controller;

import com.kyc.kycapp.repository.KycRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PanController {

    private final KycRepository kycRepository;
    private final MessageSource messageSource;

    public PanController(
            KycRepository kycRepository,
            MessageSource messageSource) {
        this.kycRepository = kycRepository;
        this.messageSource = messageSource;
    }

    @GetMapping("/pan")
    public String panPage(HttpSession session, RedirectAttributes redirectAttributes) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
        }

        // Strict flow check
        String kycStep = (String) session.getAttribute("kycStep");
        if (kycStep == null || !kycStep.equals("LOGIN_COMPLETED")) {
            // If already completed PAN, go to VIDEO
            if ("PAN_COMPLETED".equals(kycStep)) {
                return "redirect:/video";
            }
            // If completed Video or Review, go to REVIEW
            if ("VIDEO_COMPLETED".equals(kycStep) || "REVIEW_COMPLETED".equals(kycStep)) {
                return "redirect:/review";
            }
            // If Submitted, go to Success
            if ("SUBMITTED".equals(kycStep)) {
                return "redirect:/success";
            }
        }

        if (kycRepository.existsByUsername(username)) {
            return "redirect:/success";
        }

        // Check PAN lockout
        Long lockoutTime = (Long) session.getAttribute("panLockoutTime");
        if (lockoutTime != null && System.currentTimeMillis() < lockoutTime) {
            long remainingMinutes = (lockoutTime - System.currentTimeMillis()) / (60 * 1000);
            String msg = messageSource.getMessage(
                    "error.pan.locked",
                    new Object[] { remainingMinutes + 1 },
                    LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("error", msg);
            redirectAttributes.addFlashAttribute("locked", true);
        } else if (lockoutTime != null) {
            session.removeAttribute("panLockoutTime");
            session.removeAttribute("panAttempts");
        }

        return "pan";
    }
}
