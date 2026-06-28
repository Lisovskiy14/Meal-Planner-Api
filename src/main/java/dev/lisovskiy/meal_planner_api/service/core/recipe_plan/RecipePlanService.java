package dev.lisovskiy.meal_planner_api.service.core.recipe_plan;

import dev.lisovskiy.meal_planner_api.domain.RecipePlan;
import dev.lisovskiy.meal_planner_api.dto.recipe_plan.CreateRecipePlanDto;
import dev.lisovskiy.meal_planner_api.dto.recipe_plan.UpdateRecipePlanDto;

import java.util.List;

public interface RecipePlanService {
    List<RecipePlan> getAllRecipePlansByMealPlanId(Long mealPlanId);
    RecipePlan getRecipePlanById(Long recipePlanId);
    RecipePlan createRecipePlan(Long mealPlanId, CreateRecipePlanDto createRecipePlanDto);
    RecipePlan updateRecipePlanById(Long recipePlanId, UpdateRecipePlanDto updateRecipePlanDto);
    void deleteRecipePlanById(Long recipePlanId);
}
