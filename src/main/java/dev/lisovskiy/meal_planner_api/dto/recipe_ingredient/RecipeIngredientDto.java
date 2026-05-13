package dev.lisovskiy.meal_planner_api.dto.recipe_ingredient;

import dev.lisovskiy.meal_planner_api.common.IngredientUnit;
import dev.lisovskiy.meal_planner_api.domain.Ingredient;
import lombok.Value;

@Value
public class RecipeIngredientDto {
    Long id;
    Ingredient ingredient;
    IngredientUnit unit;
    Double quantity;
}
