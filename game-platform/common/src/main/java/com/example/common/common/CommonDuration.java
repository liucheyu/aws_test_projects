package com.example.common.common;

import lombok.Getter;

@Getter
public enum CommonDuration {
    ONE_SECOND(1000L),
    ONE_MINUTE(1000L*60),
    ONE_HOUR(1000L*60*60);
    private long millisecond;

    CommonDuration(long millisecond) {
        this.millisecond = millisecond;
    }

    public long toMilliSeconds(long times) {
        return this.getMillisecond() * times;
    }
}
