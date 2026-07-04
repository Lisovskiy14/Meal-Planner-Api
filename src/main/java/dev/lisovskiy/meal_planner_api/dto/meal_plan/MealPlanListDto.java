package dev.lisovskiy.meal_planner_api.dto.meal_plan;

import lombok.Value;

import java.util.List;

@Value
public class MealPlanListDto {
    List<MealPlanSummaryDto> mealPlans;
}
