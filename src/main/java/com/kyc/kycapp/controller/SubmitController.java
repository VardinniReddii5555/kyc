package com.kyc.kycapp.controller;

import com.kyc.kycapp.entity.KycDetails;
import com.kyc.kycapp.repository.KycRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.context.i18n.LocaleContextHolder;

@Controller
public class SubmitController {

    private final KycRepository kycRepository;
    private final MessageSource messageSource;

    public SubmitController(KycRepository kycRepository, MessageSource messageSource) {
        this.kycRepository = kycRepository;
        this.messageSource = messageSource;
    }

    @PostMapping("/submit")
    public String submitKyc(HttpSession session, RedirectAttributes ra) {
        String username = (String) session.getAttribute("username");
        if (username == null)
            return "redirect:/login";

        String kycStep = (String) session.getAttribute("kycStep");
        if (kycStep == null || !kycStep.equals("REVIEW_COMPLETED")) {
            return "redirect:/review";
        }

        if (kycRepository.existsByUsername(username)) {
            ra.addFlashAttribute("error",
                    messageSource.getMessage("error.kyc.already_submitted", null, LocaleContextHolder.getLocale()));
            return "redirect:/success";
        }

        KycDetails kyc = new KycDetails();
        kyc.setUsername(username);
        kyc.setPanNumber((String) session.getAttribute("panNumber"));
        kyc.setFullName((String) session.getAttribute("fullName"));
        kyc.setVideoPath("C:/kyc_videos/" + session.getAttribute("videoFileName"));

        kycRepository.save(kyc);
        session.setAttribute("kycStep", "SUBMITTED");
        return "redirect:/success";
    }
}
