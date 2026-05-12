package dev.lisovskiy.meal_planner_api.util;

import dev.lisovskiy.meal_planner_api.common.IngredientUnit;
import lombok.experimental.UtilityClass;

@UtilityClass
public class IngredientUnitMapper {

    public IngredientUnit fromString(String stringUnit) {
        return IngredientUnit.valueOf(stringUnit
                .trim().toUpperCase());
    }
}
