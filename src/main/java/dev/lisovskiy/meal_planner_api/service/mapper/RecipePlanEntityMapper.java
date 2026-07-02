package dev.lisovskiy.meal_planner_api.service.mapper;

import dev.lisovskiy.meal_planner_api.domain.RecipePlan;
import dev.lisovskiy.meal_planner_api.repository.entity.impl.RecipePlanEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {RecipeEntityMapper.class})
public interface RecipePlanEntityMapper {
    RecipePlan toRecipePlan(RecipePlanEntity recipePlanEntity);
}
