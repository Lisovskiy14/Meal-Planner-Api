package dev.lisovskiy.meal_planner_api.dto.ingredient;

import lombok.Value;

import java.util.List;

@Value
public class IngredientListDto {
    List<IngredientDto> ingredients;
}
