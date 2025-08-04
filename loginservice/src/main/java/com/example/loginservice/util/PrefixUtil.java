package com.example.loginservice.util;

import lombok.Getter;

public class PrefixUtil {
    public static String getKeyWithPrefix(Prefix prefix, String key) {
        return prefix.getPrefix() + key;
    }

    @Getter
    public enum Prefix {
        EMAIL("email:"),
        MOBILE_PHONE("mobilePhone:");

        private final String prefix;

        Prefix(String prefix) {
            this.prefix = prefix;
        }
    }
}
