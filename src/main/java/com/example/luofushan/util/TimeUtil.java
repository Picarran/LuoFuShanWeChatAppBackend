package com.example.luofushan.util;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Locale;

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

    public static long remainSecondsThisMonth(LocalDateTime now) {
        LocalDateTime nextMonthStart = now
                .toLocalDate()
                .withDayOfMonth(1)
                .plusMonths(1)
                .atStartOfDay();

        return Duration.between(now, nextMonthStart).getSeconds();
    }

    public static String formatByPrefix(String prefix, LocalDateTime time) {
        if (prefix.contains("day")) {
            return time.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        }
        if (prefix.contains("week")) {
            WeekFields wf = WeekFields.of(Locale.CHINA);
            return time.getYear() + "W" + time.get(wf.weekOfWeekBasedYear());
        }
        if (prefix.contains("month")) {
            return time.format(DateTimeFormatter.ofPattern("yyyyMM"));
        }
        throw new IllegalArgumentException("unknown prefix");
    }
}
