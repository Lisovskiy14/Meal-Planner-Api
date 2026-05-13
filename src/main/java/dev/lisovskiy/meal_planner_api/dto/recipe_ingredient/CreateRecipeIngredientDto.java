package dev.lisovskiy.meal_planner_api.dto.recipe_ingredient;

import dev.lisovskiy.meal_planner_api.dto.validation.ValidIngredientUnit;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class CreateRecipeIngredientDto {

    @NotNull(message = "is required")
    @Min(value = 0, message = "must be higher or equals to 0")
    Long ingredientId;

    @NotBlank(message = "is required")
    @ValidIngredientUnit
    String unit;

    @Min(value = 1, message = "must be at least 1")
    Double quantity;
}
