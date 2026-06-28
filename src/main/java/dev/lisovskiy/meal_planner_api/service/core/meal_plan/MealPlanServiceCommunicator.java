package dev.lisovskiy.meal_planner_api.service.core.meal_plan;

import dev.lisovskiy.meal_planner_api.repository.entity.MealPlanEntity;

public interface MealPlanServiceCommunicator {
    MealPlanEntity getMealPlanEntityById(Long id);
}
