package dev.lisovskiy.meal_planner_api.dto.validation.validator;

import dev.lisovskiy.meal_planner_api.dto.validation.ValidLongId;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class LongIdValidator implements ConstraintValidator<ValidLongId, Long> {

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext context) {
        return id != null && id >= 0;
    }
}
