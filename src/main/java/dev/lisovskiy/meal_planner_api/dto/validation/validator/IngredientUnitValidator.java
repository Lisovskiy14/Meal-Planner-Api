package dev.lisovskiy.meal_planner_api.dto.validation.validator;

import dev.lisovskiy.meal_planner_api.common.IngredientUnit;
import dev.lisovskiy.meal_planner_api.dto.validation.ValidIngredientUnit;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.List;

public class IngredientUnitValidator implements ConstraintValidator<ValidIngredientUnit, String> {

    private List<String> acceptedValues;

    @Override
    public void initialize(ValidIngredientUnit constraintAnnotation) {
        acceptedValues = Arrays.stream(IngredientUnit.values())
                .map(Enum::name)
                .toList();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }

        return acceptedValues.contains(value);
    }

}
