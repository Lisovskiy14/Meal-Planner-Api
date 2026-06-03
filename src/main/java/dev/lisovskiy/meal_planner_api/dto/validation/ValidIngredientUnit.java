package dev.lisovskiy.meal_planner_api.dto.validation;

import dev.lisovskiy.meal_planner_api.dto.validation.validator.IngredientUnitValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {IngredientUnitValidator.class})
public @interface ValidIngredientUnit {
    String message() default "Invalid ingredient unit.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
