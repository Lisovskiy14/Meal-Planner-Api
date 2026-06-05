package dev.lisovskiy.meal_planner_api.service.core.meal_plan;

import dev.lisovskiy.meal_planner_api.domain.MealPlan;
import dev.lisovskiy.meal_planner_api.dto.meal_plan.CreateMealPlanDto;
import dev.lisovskiy.meal_planner_api.dto.meal_plan.UpdateMealPlanDto;

import java.util.List;

public interface MealPlanService {
    List<MealPlan> getAllMealPlans();
    MealPlan getMealPlanById(Long id);
    MealPlan createMealPlan(CreateMealPlanDto createMealPlanDto);
    MealPlan updateMealPlanById(Long id, UpdateMealPlanDto updateMealPlanDto);
    void deleteMealPlanById(Long id);
}
