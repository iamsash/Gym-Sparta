package com.example.appgymsparta.data.converter;

import androidx.room.TypeConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DateTimeConverter {

    // 1. LocalDate -> String
    @TypeConverter
    public static String fromLocalDate(LocalDate date) {
        return date == null ? null : date.toString();
    }

    // 2. String -> LocalDate
    @TypeConverter
    public static LocalDate toLocalDate(String value) {
        return value == null ? null : LocalDate.parse(value);
    }

    // 3. LocalDateTime -> String
    @TypeConverter
    public static String fromLocalDateTime(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.toString();
    }

    // 4. String -> LocalDateTime
    @TypeConverter
    public static LocalDateTime toLocalDateTime(String value) {
        return value == null ? null : LocalDateTime.parse(value);
    }

    // 5. LocalTime -> String
    @TypeConverter
    public static String fromLocalTime(LocalTime time) {
        return time == null ? null : time.toString();
    }

    // 6. String -> LocalTime
    @TypeConverter
    public static LocalTime toLocalTime(String value) {
        return value == null ? null : LocalTime.parse(value);
    }
}
