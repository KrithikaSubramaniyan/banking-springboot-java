package com.github.bank.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class AgeValidatorTest {

    @Test
    public void testIsMinor_YoungMinor() {
        Date birthDate = Date.from(LocalDate.now().minusYears(10).atStartOfDay(ZoneId.systemDefault()).toInstant());
        assertTrue(AgeValidator.isMinor(birthDate), "The person should be a minor.");
    }

    @Test
    public void testIsMinor_YoungAdult() {
        Date birthDate = Date.from(LocalDate.now().minusYears(18).atStartOfDay(ZoneId.systemDefault()).toInstant());
        assertFalse(AgeValidator.isMinor(birthDate), "The person should be an adult.");
    }

    @Test
    public void testIsMinor_Adult() {
        Date birthDate = Date.from(LocalDate.now().minusYears(20).atStartOfDay(ZoneId.systemDefault()).toInstant());
        assertFalse(AgeValidator.isMinor(birthDate), "The person should be an adult.");
    }
}
