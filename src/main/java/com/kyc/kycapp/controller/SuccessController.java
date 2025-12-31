package com.kyc.kycapp.controller;

import com.kyc.kycapp.entity.KycDetails;
import com.kyc.kycapp.repository.KycRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SuccessController {

    private final KycRepository kycRepository;

    public SuccessController(KycRepository kycRepository) {
        this.kycRepository = kycRepository;
    }

    @GetMapping("/success")
    public String successPage(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null)
            return "redirect:/login";

        KycDetails kyc = kycRepository.findByUsername(username);
        if (kyc == null) {
            return "redirect:/pan";
        }

        model.addAttribute("kyc", kyc);
        return "success";
    }
}
