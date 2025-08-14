package com.example.common.config;

import com.example.common.common.CommonDuration;
import lombok.Getter;

import java.time.Duration;

@Getter
public enum CacheEnum {

    EMAIL(1, Prefix.EMAIL_PREFIX, CommonDuration.ONE_MINUTE.toMilliSeconds(30)),
    MOBILE(2, Prefix.MOBILE_PREFIX, CommonDuration.ONE_MINUTE.toMilliSeconds(5));

    private final int number;
    private final String prefix;
    private final long millisecond;
    public String getCacheKey() {
        return this.getPrefix();
    }

    CacheEnum(int number, String prefix, long millisecond) {
        this.number = number;
        this.prefix = prefix;
        this.millisecond = millisecond;
    }



    public class Prefix {
        public static final String EMAIL_PREFIX = "emailCache";
        public static final String MOBILE_PREFIX = "mobileCache";
    }
}
