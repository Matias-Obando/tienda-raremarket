package com.raremarket.backend.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

@Service
public class AuthTokenService {
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final Base64.Encoder BASE64_URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder BASE64_URL_DECODER = Base64.getUrlDecoder();

    private final byte[] secretBytes;
    private final long tokenLifetimeSeconds;

    public AuthTokenService(
            @Value("${app.auth.token.secret}") String tokenSecret,
            @Value("${app.auth.token.ttl-seconds:86400}") long tokenLifetimeSeconds
    ) {
        this.secretBytes = tokenSecret.getBytes(StandardCharsets.UTF_8);
        this.tokenLifetimeSeconds = tokenLifetimeSeconds;
    }

    public String issueToken(String userId) {
        try {
            long now = Instant.now().getEpochSecond();
            long exp = now + tokenLifetimeSeconds;

            String payload = userId + ":" + exp;
            byte[] payloadBytes = payload.getBytes(StandardCharsets.UTF_8);
            String payloadPart = BASE64_URL_ENCODER.encodeToString(payloadBytes);
            String signaturePart = sign(payloadPart);
            return payloadPart + "." + signaturePart;
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo generar el token", ex);
        }
    }

    public Optional<String> extractUserId(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        String[] parts = token.split("\\.");
        if (parts.length != 2) {
            return Optional.empty();
        }

        String payloadPart = parts[0];
        String signaturePart = parts[1];

        String expectedSignature = sign(payloadPart);
        if (!constantTimeEquals(signaturePart, expectedSignature)) {
            return Optional.empty();
        }

        try {
            String payload = new String(BASE64_URL_DECODER.decode(payloadPart), StandardCharsets.UTF_8);
            String[] payloadParts = payload.split(":", 2);
            if (payloadParts.length != 2) {
                return Optional.empty();
            }

            String userId = payloadParts[0].trim();
            if (userId.isEmpty()) {
                return Optional.empty();
            }

            long exp = Long.parseLong(payloadParts[1]);
            long now = Instant.now().getEpochSecond();
            if (now >= exp) {
                return Optional.empty();
            }

            return Optional.of(userId);
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    private String sign(String payloadPart) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secretBytes, HMAC_ALGORITHM));
            byte[] signature = mac.doFinal(payloadPart.getBytes(StandardCharsets.UTF_8));
            return BASE64_URL_ENCODER.encodeToString(signature);
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo firmar el token", ex);
        }
    }

    private boolean constantTimeEquals(String left, String right) {
        byte[] leftBytes = left.getBytes(StandardCharsets.UTF_8);
        byte[] rightBytes = right.getBytes(StandardCharsets.UTF_8);

        if (leftBytes.length != rightBytes.length) {
            return false;
        }

        int result = 0;
        for (int index = 0; index < leftBytes.length; index++) {
            result |= leftBytes[index] ^ rightBytes[index];
        }
        return result == 0;
    }
}
