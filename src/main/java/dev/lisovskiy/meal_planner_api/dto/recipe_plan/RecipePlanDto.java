package dev.lisovskiy.meal_planner_api.dto.recipe_plan;

import dev.lisovskiy.meal_planner_api.dto.recipe.RecipeDto;
import lombok.Value;

import java.time.LocalTime;

@Value
public class RecipePlanDto {
    Long id;
    RecipeDto recipe;
    String description;
    LocalTime time;
}
