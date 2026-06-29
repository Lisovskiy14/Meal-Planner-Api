package dev.lisovskiy.meal_planner_api.dto.validation.validator;

import dev.lisovskiy.meal_planner_api.dto.validation.ValidTime;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class TimeValidator implements ConstraintValidator<ValidTime, String> {

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        try {
            LocalTime.parse(s);
            return true;
        } catch (DateTimeParseException ex) {
            return false;
        }
    }
}
