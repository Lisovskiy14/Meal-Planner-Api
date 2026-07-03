package dev.lisovskiy.meal_planner_api.dto.recipe_plan;

import dev.lisovskiy.meal_planner_api.dto.recipe.RecipeSummaryDto;
import lombok.Value;

import java.time.LocalTime;

@Value
public class RecipePlanDto {
    Long id;
    RecipeSummaryDto recipe;
    String description;
    LocalTime time;
}
