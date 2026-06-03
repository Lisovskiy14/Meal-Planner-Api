package dev.lisovskiy.meal_planner_api.dto.recipe;

import dev.lisovskiy.meal_planner_api.dto.recipe_ingredient.RecipeIngredientDto;
import lombok.Value;

import java.util.List;

@Value
public class RecipeDto {
    Long id;
    String title;
    String instructions;
    int prepTimeMinutes;
    List<RecipeIngredientDto> recipeIngredients;
}
