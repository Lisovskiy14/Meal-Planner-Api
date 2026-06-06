package dev.lisovskiy.meal_planner_api.dto.meal_plan;

import dev.lisovskiy.meal_planner_api.dto.recipe_plan.RecipePlanDto;
import lombok.Value;

import java.time.DayOfWeek;
import java.util.List;

@Value
public class MealPlanDto {
    Long id;
    String description;
    List<RecipePlanDto> recipePlans;
    DayOfWeek dayOfWeek;
}
