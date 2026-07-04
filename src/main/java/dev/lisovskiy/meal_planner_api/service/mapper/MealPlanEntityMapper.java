package dev.lisovskiy.meal_planner_api.service.mapper;

import dev.lisovskiy.meal_planner_api.domain.MealPlan;
import dev.lisovskiy.meal_planner_api.repository.entity.impl.MealPlanEntity;
import dev.lisovskiy.meal_planner_api.service.mapper.util.JpaCondition;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {JpaCondition.class, RecipePlanEntityMapper.class})
public interface MealPlanEntityMapper {
    MealPlan toMealPlan(MealPlanEntity mealPlanEntity);
}
