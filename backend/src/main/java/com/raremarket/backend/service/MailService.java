package com.raremarket.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class MailService {
    private static final URI BREVO_EMAIL_ENDPOINT = URI.create("https://api.brevo.com/v3/smtp/email");

    private final HttpClient httpClient;
    private final String apiKey;
    private final String senderEmail;
    private final String senderName;

    public MailService(
            @Value("${brevo.api.key}") String apiKey,
            @Value("${app.mail.sender-email}") String senderEmail,
            @Value("${app.mail.sender-name}") String senderName
    ) {
        this.httpClient = HttpClient.newHttpClient();
        this.apiKey = apiKey;
        this.senderEmail = senderEmail;
        this.senderName = senderName;
    }

    public void sendPasswordResetEmail(String recipientEmail, String resetUrl) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Brevo API no esta configurada");
        }

        if (senderEmail == null || senderEmail.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "BREVO_SENDER_EMAIL no esta configurado");
        }

        String requestBody = "{" +
            "\"sender\":{" +
            "\"email\":\"" + jsonEscape(senderEmail) + "\"," +
            "\"name\":\"" + jsonEscape(senderName == null || senderName.isBlank() ? "Closely" : senderName) + "\"" +
            "}," +
            "\"to\":[{" +
            "\"email\":\"" + jsonEscape(recipientEmail) + "\"" +
            "}]," +
            "\"subject\":\"Recupera tu contrasena en Closely\"," +
            "\"htmlContent\":\"" + jsonEscape(buildResetEmailHtml(resetUrl)) + "\"," +
            "\"textContent\":\"" + jsonEscape(buildResetEmailText(resetUrl)) + "\"" +
            "}";

        HttpRequest request = HttpRequest.newBuilder(BREVO_EMAIL_ENDPOINT)
                .header("accept", "application/json")
                .header("content-type", "application/json")
                .header("api-key", apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                String responseBody = response.body() == null ? "" : response.body().trim();
                if (responseBody.length() > 300) {
                    responseBody = responseBody.substring(0, 300) + "...";
                }
                throw new ResponseStatusException(
                        HttpStatus.SERVICE_UNAVAILABLE,
                        "No se pudo enviar el correo de recuperacion (Brevo " + response.statusCode() + "): " + responseBody
                );
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "No se pudo enviar el correo de recuperacion", exception);
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "No se pudo enviar el correo de recuperacion", exception);
        }
    }

    private String buildResetEmailHtml(String resetUrl) {
        return """
                <div style="font-family: Arial, sans-serif; line-height: 1.6; color: #0f172a;">
                  <h2 style="margin: 0 0 12px;">Recupera tu contrasena</h2>
                  <p style="margin: 0 0 16px;">Hemos recibido una solicitud para cambiar tu contrasena en Closely.</p>
                  <p style="margin: 0 0 20px;">Haz clic en este enlace para crear una nueva contrasena:</p>
                  <p style="margin: 0 0 24px;"><a href="%s" style="color: #0f766e; font-weight: 700;">Restablecer contrasena</a></p>
                  <p style="margin: 0; color: #475569; font-size: 14px;">Si no pediste este cambio, puedes ignorar este mensaje.</p>
                </div>
                """.formatted(resetUrl);
    }

    private String buildResetEmailText(String resetUrl) {
        return "Recupera tu contrasena en Closely. Abre este enlace para restablecerla: " + resetUrl;
    }

    private String jsonEscape(String value) {
        if (value == null) {
            return "";
        }

        StringBuilder escaped = new StringBuilder(value.length() + 16);
        for (char character : value.toCharArray()) {
            switch (character) {
                case '"' -> escaped.append("\\\"");
                case '\\' -> escaped.append("\\\\");
                case '\b' -> escaped.append("\\b");
                case '\f' -> escaped.append("\\f");
                case '\n' -> escaped.append("\\n");
                case '\r' -> escaped.append("\\r");
                case '\t' -> escaped.append("\\t");
                default -> {
                    if (character < 0x20) {
                        escaped.append(String.format("\\u%04x", (int) character));
                    } else {
                        escaped.append(character);
                    }
                }
            }
        }

        return escaped.toString();
    }
}