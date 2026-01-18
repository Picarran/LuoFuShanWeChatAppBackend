package com.example.luofushan.util;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

public class TimeUtil {
    public static long remainSecondsToday(LocalDateTime now) {
        LocalDateTime tomorrowStart = now
                .toLocalDate()
                .plusDays(1)
                .atStartOfDay();

        return Duration.between(now, tomorrowStart).getSeconds();
    }

    public static long remainSecondsThisWeek(LocalDateTime now) {
        LocalDateTime nextWeekStart = now
                .with(TemporalAdjusters.next(DayOfWeek.MONDAY))
                .toLocalDate()
                .atStartOfDay();

        return Duration.between(now, nextWeekStart).getSeconds();
    }
}
