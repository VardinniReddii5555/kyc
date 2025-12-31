package com.kyc.kycapp.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class PanApiService {

    private static final String API_URL =
            "https://idv-core-origin.emsigner.com/fintech-api/verify-pan";

    private static final String TOKEN =
            "panstatic.C7xLm9lTdNVay77bpseTh2XvYagWJZj";

    public String getPanHolderName(String pan) {

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(TOKEN);

        Map<String, String> body = new HashMap<>();
        body.put("pan", pan);
        body.put("consent", "Y");
        body.put("reason", "Identity Verification");

        HttpEntity<Map<String, String>> request =
                new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response =
                    restTemplate.postForEntity(API_URL, request, Map.class);

            Map res = response.getBody();

            System.out.println("PAN API RAW RESPONSE = " + res);

            if (res == null) return null;


            if (!"SUCCESS".equalsIgnoreCase(String.valueOf(res.get("verification")))) {
                return null;
            }
            if (res.get("data") instanceof Map) {
                Map data = (Map) res.get("data");
                Object fullName = data.get("full_name");
                if (fullName != null) {
                    return fullName.toString(); // ✅ REAL PAN HOLDER NAME
                }
            }

            return null;

        } catch (Exception e) {
            System.out.println("PAN API ERROR: " + e.getMessage());
            return null;
        }
    }
}
