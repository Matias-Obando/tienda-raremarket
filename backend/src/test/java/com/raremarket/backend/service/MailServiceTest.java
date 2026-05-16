package com.raremarket.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("MailService Tests")
class MailServiceTest {

    private MailService mailService;

    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpResponse<String> httpResponse;

    @BeforeEach
    void setUp() {
        // Provide valid defaults; tests will override when needed
        mailService = new MailService("valid-api-key", "sender@example.com", "Sender Name");
        ReflectionTestUtils.setField(mailService, "httpClient", httpClient);
    }

    @Test
    @DisplayName("Should throw when API key is missing")
    void testSendPasswordResetEmail_MissingApiKey() {
        MailService service = new MailService("", "sender@example.com", "Sender Name");
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.sendPasswordResetEmail("recipient@example.com", "http://localhost/reset?token=abc"));
        assertEquals(503, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("Brevo API no esta configurada"));
    }

    @Test
    @DisplayName("Should throw when sender email is missing")
    void testSendPasswordResetEmail_MissingSenderEmail() {
        MailService service = new MailService("valid-api-key", "", "Sender Name");
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.sendPasswordResetEmail("recipient@example.com", "http://localhost/reset?token=abc"));
        assertEquals(503, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("BREVO_SENDER_EMAIL no esta configurado"));
    }

    @Test
    @Disabled("Intentional failing test - disabled for CI")
    @DisplayName("Intentional failing test in MailServiceTest")
    void intentionalFail_MailService() {
        fail("Prueba intencional que debe fallar en MailServiceTest");
    }

    @Test
    @DisplayName("Should throw when Brevo responds with non-2xx")
    void testSendPasswordResetEmail_BrevoNon2xx() throws Exception {
        when(httpResponse.statusCode()).thenReturn(500);
        when(httpResponse.body()).thenReturn("server error");
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> mailService.sendPasswordResetEmail("recipient@example.com", "http://localhost/reset?token=abc"));
        assertEquals(503, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("Brevo 500"));
    }

    @Test
    @DisplayName("Should throw when HttpClient throws IOException")
    void testSendPasswordResetEmail_HttpClientIoException() throws Exception {
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenThrow(new IOException("network"));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> mailService.sendPasswordResetEmail("recipient@example.com", "http://localhost/reset?token=abc"));
        assertEquals(503, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("No se pudo enviar el correo de recuperacion"));
    }
}
