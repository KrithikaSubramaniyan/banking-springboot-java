package com.github.bank.util;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;

public class AgeValidator {
    public static boolean isMinor(Date birthDate) {
        LocalDate currentDate = LocalDate.now();
        LocalDate birthDateInLocalTime = birthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        int age = Period.between(birthDateInLocalTime, currentDate).getYears();
        return age < 18;
    }
}
