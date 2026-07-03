package dev.lisovskiy.meal_planner_api.dto.recipe;

import lombok.Value;

import java.util.List;

@Value
public class RecipeListDto {
    List<RecipeSummaryDto> recipes;
}
