package dev.lisovskiy.meal_planner_api.service.mapper;

import dev.lisovskiy.meal_planner_api.domain.RecipePlan;
import dev.lisovskiy.meal_planner_api.repository.entity.RecipePlanEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RecipePlanEntityMapper {
    RecipePlan toRecipePlan(RecipePlanEntity recipePlanEntity);
}
