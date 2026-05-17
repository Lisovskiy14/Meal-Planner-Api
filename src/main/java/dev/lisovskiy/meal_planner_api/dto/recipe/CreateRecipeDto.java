package dev.lisovskiy.meal_planner_api.dto.recipe;

import dev.lisovskiy.meal_planner_api.dto.recipe_ingredient.CreateRecipeIngredientDto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.util.List;

@Value
public class CreateRecipeDto {

    @NotBlank
    @Size(min = 2, max = 100, message = "must be between 2 and 100 characters")
    String title;

    @NotBlank
    @Size(min = 10, max = 2000, message = "must be between 10 and 2000 characters")
    String instructions;

    @Min(value = 1, message = "must be at least 1 minute")
    int prepTimeMinutes;

    List<CreateRecipeIngredientDto> recipeIngredients;
}
