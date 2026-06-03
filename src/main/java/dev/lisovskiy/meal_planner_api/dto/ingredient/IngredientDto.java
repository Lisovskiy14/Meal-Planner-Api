package dev.lisovskiy.meal_planner_api.dto.ingredient;

import lombok.Value;

@Value
public class IngredientDto {
    Long id;
    String name;
    String description;
}
