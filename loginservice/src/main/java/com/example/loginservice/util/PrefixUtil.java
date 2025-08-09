package com.example.loginservice.util;

import lombok.Getter;

import java.util.Arrays;

public class PrefixUtil {
    public static String getKeyWithPrefix(Prefix prefix, String key) {
        return prefix.getPrefix() + key;
    }

    private static String getKeyWithPrefixByNumber(int number, String key) {
        return getPrefixByNumber(number).getPrefix() + key;
    }

    private static Prefix getPrefixByNumber(int number) {
        return Arrays.stream(Prefix.values()).filter(e -> e.number == number).findFirst().orElse(Prefix.EMAIL);
    }

    @Getter
    public enum Prefix {
        EMAIL(1, "email:"),
        MOBILE_PHONE(2, "mobilePhone:");

        private final int number;
        private final String prefix;

        Prefix(int number, String prefix) {
            this.number = number;
            this.prefix = prefix;
        }

    }
}
