package dev.lisovskiy.meal_planner_api.dto.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
public @interface ValidLongId {
    String message() default "Long id must be 0 or higher.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
