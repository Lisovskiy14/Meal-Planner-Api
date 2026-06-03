package dev.lisovskiy.meal_planner_api.dto.recipe_ingredient;

import dev.lisovskiy.meal_planner_api.dto.validation.ValidIngredientUnit;
import dev.lisovskiy.meal_planner_api.dto.validation.ValidLongId;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class UpdateRecipeIngredientDto {

    @NotNull(message = "is required")
    @ValidLongId
    Long ingredientId;

    @NotBlank(message = "is required")
    @ValidIngredientUnit
    String unit;

    @Min(value = 1, message = "must be at least 1")
    Double quantity;
}
