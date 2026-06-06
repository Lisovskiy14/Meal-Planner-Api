package dev.lisovskiy.meal_planner_api.web.mapper;

import dev.lisovskiy.meal_planner_api.domain.MealPlan;
import dev.lisovskiy.meal_planner_api.dto.meal_plan.MealPlanDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {RecipePlanWebMapper.class})
public interface MealPlanWebMapper {
    MealPlanDto toMealPlanDto(MealPlan mealPlan);
}
