package dev.lisovskiy.meal_planner_api.dto.validation;

import dev.lisovskiy.meal_planner_api.dto.validation.validator.TimeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {TimeValidator.class})
public @interface ValidTime {
    String message() default "Provided time is not valid.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
