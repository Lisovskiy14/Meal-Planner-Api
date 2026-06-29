package dev.lisovskiy.meal_planner_api.web.mapper;

import dev.lisovskiy.meal_planner_api.domain.RecipePlan;
import dev.lisovskiy.meal_planner_api.dto.recipe_plan.RecipePlanDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {RecipeWebMapper.class})
public interface RecipePlanWebMapper {
    RecipePlanDto toRecipePlanDto(RecipePlan recipePlan);
}
