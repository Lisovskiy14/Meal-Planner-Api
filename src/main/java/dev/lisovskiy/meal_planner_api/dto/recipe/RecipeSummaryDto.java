package dev.lisovskiy.meal_planner_api.dto.recipe;

import lombok.Value;

@Value
public class RecipeSummaryDto {
    Long id;
    String title;
    String instructions;
    int prepTimeMinutes;
}
