package dev.lisovskiy.meal_planner_api.dto.recipe_ingredient;

import dev.lisovskiy.meal_planner_api.common.IngredientUnit;
import dev.lisovskiy.meal_planner_api.domain.Ingredient;
import dev.lisovskiy.meal_planner_api.domain.Recipe;
import dev.lisovskiy.meal_planner_api.dto.ingredient.IngredientDto;
import dev.lisovskiy.meal_planner_api.dto.recipe.RecipeDto;
import lombok.Value;

@Value
public class RecipeIngredientDto {
    Long id;
    IngredientDto ingredient;
    IngredientUnit unit;
    Double quantity;
}
