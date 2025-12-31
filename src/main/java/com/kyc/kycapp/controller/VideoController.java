package com.kyc.kycapp.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;

@Controller
public class VideoController {

    @Value("${video.max.duration.seconds}")
    private int maxVideoDuration;

    @GetMapping("/video")
    public String videoPage(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null)
            return "redirect:/login";

        String kycStep = (String) session.getAttribute("kycStep");
        if (kycStep == null || !kycStep.equals("PAN_COMPLETED")) {
            // If already completed Video/Review, go to REVIEW
            if ("VIDEO_COMPLETED".equals(kycStep) || "REVIEW_COMPLETED".equals(kycStep)) {
                return "redirect:/review";
            }
            // If Submitted, go to Success
            if ("SUBMITTED".equals(kycStep)) {
                return "redirect:/success";
            }
            // Otherwise go back to PAN
            return "redirect:/pan";
        }

        model.addAttribute("maxVideoDuration", maxVideoDuration);
        return "video";
    }

    @PostMapping("/uploadVideo")
    @ResponseBody
    public String uploadVideo(@RequestParam("video") MultipartFile file, HttpSession session) throws IOException {
        String username = (String) session.getAttribute("username");
        if (username == null)
            return "UNAUTHORIZED";

        File dir = new File("C:/kyc_videos");
        if (!dir.exists())
            dir.mkdirs();

        String fileName = "kyc_" + username + "_" + System.currentTimeMillis() + ".webm";
        File dest = new File(dir, fileName);
        file.transferTo(dest);

        session.setAttribute("videoFileName", fileName);
        session.setAttribute("kycStep", "VIDEO_COMPLETED");
        return "OK";
    }

    @GetMapping("/video/{fileName}")
    public ResponseEntity<Resource> playVideo(@PathVariable String fileName) throws IOException {
        File file = new File("C:/kyc_videos/" + fileName);
        if (!file.exists())
            return ResponseEntity.notFound().build();
        Resource resource = new UrlResource(file.toURI());
        return ResponseEntity.ok().contentType(MediaType.parseMediaType("video/webm")).body(resource);
    }
}
