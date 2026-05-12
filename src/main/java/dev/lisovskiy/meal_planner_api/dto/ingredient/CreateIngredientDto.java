package dev.lisovskiy.meal_planner_api.dto.ingredient;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Value;

@Value
public class CreateIngredientDto {

    @NotBlank(message = "is required")
    @Size(min = 2, max = 100, message = "must be between 2 and 100 characters")
    String name;

    @Size(min = 5, max = 1000, message = "must be between 5 and 1000 characters")
    String description;
}
