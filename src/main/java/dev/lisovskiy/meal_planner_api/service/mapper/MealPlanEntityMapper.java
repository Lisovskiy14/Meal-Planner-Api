package dev.lisovskiy.meal_planner_api.service.mapper;

import dev.lisovskiy.meal_planner_api.domain.MealPlan;
import dev.lisovskiy.meal_planner_api.repository.entity.MealPlanEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {RecipePlanEntityMapper.class})
public interface MealPlanEntityMapper {
    MealPlan toMealPlan(MealPlanEntity mealPlanEntity);
}
