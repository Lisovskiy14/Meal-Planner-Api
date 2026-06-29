package dev.lisovskiy.meal_planner_api.dto.validation.validator;

import dev.lisovskiy.meal_planner_api.dto.validation.ValidDayOfWeek;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.DayOfWeek;

public class DayOfWeekValidator implements ConstraintValidator<ValidDayOfWeek, String> {

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        try {
            DayOfWeek.valueOf(s.trim().toUpperCase());
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }
}