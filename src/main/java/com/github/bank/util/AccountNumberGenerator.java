package com.github.bank.util;

import java.util.UUID;

public class AccountNumberGenerator {
    // TODO: check if custom generator is needed
    public static String generateAccountNumber() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12);
    }
}
