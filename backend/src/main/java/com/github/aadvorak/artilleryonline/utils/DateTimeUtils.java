package com.github.aadvorak.artilleryonline.utils;

import com.github.aadvorak.artilleryonline.error.exception.BadRequestAppException;
import com.github.aadvorak.artilleryonline.error.response.Validation;
import com.github.aadvorak.artilleryonline.error.response.ValidationResponse;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

public class DateTimeUtils {

    public static LocalDateTime parseLocalDateTime(String dateTime, String fieldName) {
        try {
            return LocalDateTime.parse(dateTime);
        } catch (DateTimeParseException e) {
            throw new BadRequestAppException(List.of(new ValidationResponse()
                    .setValidation(Validation.WRONG)
                    .setField(fieldName)));
        }
    }
}
