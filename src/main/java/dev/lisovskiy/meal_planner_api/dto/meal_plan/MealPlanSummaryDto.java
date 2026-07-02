package dev.lisovskiy.meal_planner_api.dto.meal_plan;

import lombok.Value;

import java.time.DayOfWeek;

@Value
public class MealPlanSummaryDto {
    Long id;
    String description;
    DayOfWeek dayOfWeek;
}
