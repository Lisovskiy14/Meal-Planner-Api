package dev.lisovskiy.meal_planner_api.dto.recipe_plan;

import lombok.Value;

import java.util.List;

@Value
public class RecipePlanListDto {
    List<RecipePlanDto> recipePlans;
}
