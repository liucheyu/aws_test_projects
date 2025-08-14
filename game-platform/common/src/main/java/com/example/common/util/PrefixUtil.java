package com.example.common.util;

import com.example.common.config.CacheEnum;
import lombok.Getter;

public class PrefixUtil {
    public static String getKeyWithPrefix(Prefix prefix, String key) {
        return prefix.getPrefix() + key;
    }

    public static String getKeyWithPrefix(CacheEnum prefix, String key) {
        return prefix.getPrefix() + key;
    }

    @Getter
    public enum Prefix {
        EMAILXXX(1, "email:"),
        MOBILE_PHONEXXX(2, "mobilePhone:");

        private final int number;
        private final String prefix;

        Prefix(int number, String prefix) {
            this.number = number;
            this.prefix = prefix;
        }

    }
}
