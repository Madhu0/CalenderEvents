package com.kmsr.phonepe.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class TimeUtil {

  public static String getHumanReadableTimeFromEpoch(long epoch) {
    LocalDateTime localDateTime = Instant.ofEpochMilli(epoch).atOffset(ZoneOffset.UTC)
        .toLocalDateTime();
    return localDateTime.toString();
  }

  public static long epochFromString(String string) {
    LocalDateTime dateTime = LocalDateTime.parse(string);
    return dateTime.atOffset(ZoneOffset.UTC).toInstant().toEpochMilli();
  }
}
