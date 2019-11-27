package com.cwj.express.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class LocalDateTimeUtils {
    public static String formatToYMDHMS(LocalDateTime time){
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return time.format(df);
    }

    public static LocalDateTime ymdhmsParseToLocalDataTime(String timeString){
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(timeString, df);
    }

    public static String formatToYMD(LocalDateTime time){
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return time.format(df);
    }

    public static LocalDate ymdParseToLocalData(String timeString){
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(timeString, df);
    }

    public static void main(String[] args) {
        LocalDate now = LocalDate.now();
        LocalDate nowPlus = now.plusDays(1);
        LocalDate yes = now.plusDays(-1);
        LocalDateTime nowDateTime = now.atTime(0, 0, 0);
        System.out.println(now);
        System.out.println(nowDateTime);
        System.out.println(nowPlus);
        System.out.println(yes);

        System.out.println(ChronoUnit.DAYS.between(LocalDate.of(2019, 11,1), LocalDate.of(2019, 11,2)));
    }

}
