package dev.lisovskiy.meal_planner_api.dto.validation;

import dev.lisovskiy.meal_planner_api.dto.validation.validator.DayOfWeekValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {DayOfWeekValidator.class})
public @interface ValidDayOfWeek {
    String message() default "Provided date is not valid.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
