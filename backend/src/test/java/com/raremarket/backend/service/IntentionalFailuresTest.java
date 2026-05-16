package com.raremarket.backend.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class IntentionalFailuresTest {

    @Test
    @Disabled("Intentional failing test - disabled for CI")
    @DisplayName("Intentional failing assertion 1")
    void intentionalFail1() {
        assertEquals(1, 2, "Prueba intencional que debe fallar");
    }

    @Test
    @Disabled("Intentional failing test - disabled for CI")
    @DisplayName("Intentional failing assertion 2")
    void intentionalFail2() {
        assertTrue(false, "Segunda prueba intencional que debe fallar");
    }
}
