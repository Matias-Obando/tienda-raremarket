package com.raremarket.backend.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class IntentionalFailuresTest {

    @Test
    @DisplayName("Intentional failing assertion 1")
    void intentionalFail1() {
        assertEquals(1, 2, "Prueba intencional que debe fallar");
    }

    @Test
    @DisplayName("Intentional failing assertion 2")
    void intentionalFail2() {
        assertTrue(false, "Segunda prueba intencional que debe fallar");
    }
}
