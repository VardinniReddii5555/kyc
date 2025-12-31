package com.kyc.kycapp.controller;

import com.kyc.kycapp.repository.KycRepository;
import com.kyc.kycapp.service.PanApiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Controller
public class PanVerifyController {

    private final KycRepository kycRepository;
    private final PanApiService panApiService;
    private final MessageSource messageSource;

    @Value("${pan.max.attempts}")
    private int panMaxAttempts;

    @Value("${pan.lockout.minutes}")
    private int panLockoutMinutes;

    @Value("${pan.verification.enabled}")
    private boolean panVerificationEnabled;

    @Value("${pan.name.match.threshold}")
    private double panNameMatchThreshold;

    public PanVerifyController(
            KycRepository kycRepository,
            PanApiService panApiService,
            MessageSource messageSource) {
        this.kycRepository = kycRepository;
        this.panApiService = panApiService;
        this.messageSource = messageSource;
    }

    @PostMapping("/verify-pan")
    @ResponseBody
    public Map<String, String> verifyPan(
            @RequestBody Map<String, String> request,
            HttpSession session,
            Locale locale) {

        Map<String, String> response = new HashMap<>();

        String username = (String) session.getAttribute("username");
        if (username == null) {
            response.put("status", "ERROR");
            response.put("message", "Session expired. Please login again.");
            return response;
        }

        String panNumber = request.get("panNumber");
        String fullName = request.get("fullName");

        Long lockoutTime = (Long) session.getAttribute("panLockoutTime");
        if (lockoutTime != null && System.currentTimeMillis() < lockoutTime) {
            long remainingMinutes =
                    Math.max(1, (lockoutTime - System.currentTimeMillis()) / (60_000));
            response.put("status", "LOCKED");
            response.put("message",
                    messageSource.getMessage(
                            "error.pan.locked",
                            new Object[]{remainingMinutes},
                            locale));
            return response;
        }

        if (panNumber == null || fullName == null) {
            response.put("status", "ERROR");
            response.put("message",
                    messageSource.getMessage("error.pan.invalid", null, locale));
            return response;
        }

        panNumber = panNumber.trim().toUpperCase();
        if (!panNumber.matches("[A-Z]{5}[0-9]{4}[A-Z]")) {
            response.put("status", "ERROR");
            response.put("message",
                    messageSource.getMessage("error.pan.invalid", null, locale));
            return response;
        }

        if (kycRepository.existsByPanNumber(panNumber)) {
            response.put("status", "ERROR");
            response.put("message",
                    messageSource.getMessage("error.pan.already_registered", null, locale));
            return response;
        }

        String panHolderName = panApiService.getPanHolderName(panNumber);
        if (panHolderName == null || panHolderName.trim().isEmpty()) {
            if (!panVerificationEnabled) {
                panHolderName = "TEST USER";
            } else {
                panHolderName = fullName;
            }
        }

        double similarity = calculateSimilarity(
                fullName.toUpperCase().replaceAll("[^A-Z]", ""),
                panHolderName.toUpperCase().replaceAll("[^A-Z]", "")
        );

        if (similarity < panNameMatchThreshold) {
            Integer attempts = (Integer) session.getAttribute("panAttempts");
            attempts = (attempts == null) ? 1 : attempts + 1;
            session.setAttribute("panAttempts", attempts);

            if (attempts >= panMaxAttempts) {
                session.setAttribute(
                        "panLockoutTime",
                        System.currentTimeMillis() + panLockoutMinutes * 60L * 1000);
                session.setAttribute("panAttempts", 0);

                response.put("status", "LOCKED");
                response.put("message",
                        messageSource.getMessage(
                                "error.pan.locked",
                                new Object[]{panLockoutMinutes},
                                locale));
            } else {
                response.put("status", "ERROR");
                response.put("message",
                        messageSource.getMessage(
                                "error.pan.attempts_remaining",
                                new Object[]{panMaxAttempts - attempts},
                                locale));
            }
            return response;
        }

        session.removeAttribute("panAttempts");
        session.removeAttribute("panLockoutTime");
        session.setAttribute("panNumber", panNumber);
        session.setAttribute("fullName", fullName);
        session.setAttribute("kycStep", "PAN_COMPLETED");

        response.put("status", "SUCCESS");
        response.put("message",
                messageSource.getMessage("success.pan.verified", null, locale));
        return response;
    }

    /* ---------- Similarity logic unchanged ---------- */

    private double calculateSimilarity(String s1, String s2) {
        if (s1.equals(s2)) return 1.0;
        if (s1.isEmpty() || s2.isEmpty()) return 0.0;

        double jaro = jaroSimilarity(s1, s2);
        int prefixLength = 0;

        for (int i = 0; i < Math.min(Math.min(s1.length(), s2.length()), 4); i++) {
            if (s1.charAt(i) == s2.charAt(i)) prefixLength++;
            else break;
        }
        return jaro + prefixLength * 0.1 * (1.0 - jaro);
    }

    private double jaroSimilarity(String s1, String s2) {
        int len1 = s1.length(), len2 = s2.length();
        int matchDistance = Math.max(len1, len2) / 2 - 1;
        if (matchDistance < 0) matchDistance = 0;

        boolean[] s1Matches = new boolean[len1];
        boolean[] s2Matches = new boolean[len2];

        int matches = 0, transpositions = 0;

        for (int i = 0; i < len1; i++) {
            int start = Math.max(0, i - matchDistance);
            int end = Math.min(i + matchDistance + 1, len2);

            for (int j = start; j < end; j++) {
                if (s2Matches[j] || s1.charAt(i) != s2.charAt(j)) continue;
                s1Matches[i] = true;
                s2Matches[j] = true;
                matches++;
                break;
            }
        }

        if (matches == 0) return 0.0;

        int k = 0;
        for (int i = 0; i < len1; i++) {
            if (!s1Matches[i]) continue;
            while (!s2Matches[k]) k++;
            if (s1.charAt(i) != s2.charAt(k)) transpositions++;
            k++;
        }

        return (
                matches / (double) len1
                        + matches / (double) len2
                        + (matches - transpositions / 2.0) / matches
        ) / 3.0;
    }
}
