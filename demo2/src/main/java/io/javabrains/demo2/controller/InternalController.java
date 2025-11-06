package io.javabrains.demo2.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/internal")
public class InternalController {

    // Use a safe empty default to avoid startup placeholder resolution errors when the env var is not set
    @Value("${SENDGRID_API_KEY}")
    private String sendgridApiKey;

    @Value("${app.mail.from:}")
    private String appMailFrom;

    @GetMapping("/sendgrid/ok")
    public Map<String, Object> sendgridStatus() {
        Map<String, Object> m = new HashMap<>();
        String key = sendgridApiKey == null ? "" : sendgridApiKey.trim();
        boolean present = StringUtils.hasText(key);
        boolean looksEncrypted = present && key.startsWith("ENC(");
        boolean decrypted = present && !looksEncrypted;
        boolean hasFrom = StringUtils.hasText(appMailFrom);
        boolean canSend = decrypted && hasFrom;
        m.put("present", present);
        m.put("looksEncrypted", looksEncrypted);
        m.put("decrypted", decrypted);
        m.put("hasFrom", hasFrom);
        m.put("canSend", canSend);
        // non-secret info: length and masked preview
        m.put("keyLength", key.length());
        if (present && key.length() > 6) {
            String masked = key.substring(0, 3) + "..." + key.substring(key.length()-3);
            m.put("maskedPreview", masked);
        } else {
            m.put("maskedPreview", "");
        }
        return m;
    }

}
