package com.kyc.kycapp.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReviewController {

    @GetMapping("/review")
    public String reviewPage(HttpSession session) {
        if (session.getAttribute("username") == null)
            return "redirect:/login";

        String kycStep = (String) session.getAttribute("kycStep");
        if (kycStep == null || (!kycStep.equals("VIDEO_COMPLETED") && !kycStep.equals("REVIEW_COMPLETED"))) {
            return "redirect:/video";
        }

        session.setAttribute("kycStep", "REVIEW_COMPLETED");
        return "review";
    }

    @GetMapping("/edit-pan")
    public String editPan(HttpSession session) {
        if (session.getAttribute("username") == null)
            return "redirect:/login";
        session.setAttribute("kycStep", "LOGIN_COMPLETED");
        return "redirect:/pan";
    }

    @GetMapping("/edit-video")
    public String editVideo(HttpSession session) {
        if (session.getAttribute("username") == null)
            return "redirect:/login";
        session.setAttribute("kycStep", "PAN_COMPLETED");
        return "redirect:/video";
    }
}
