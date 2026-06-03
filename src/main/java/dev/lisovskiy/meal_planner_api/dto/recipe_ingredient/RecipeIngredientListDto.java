package dev.lisovskiy.meal_planner_api.dto.recipe_ingredient;

import lombok.Value;

import java.util.List;

@Value
public class RecipeIngredientListDto {
    List<RecipeIngredientDto> recipeIngredients;
}
