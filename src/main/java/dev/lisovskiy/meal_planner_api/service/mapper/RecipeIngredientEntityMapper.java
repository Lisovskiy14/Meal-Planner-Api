package dev.lisovskiy.meal_planner_api.service.mapper;

import dev.lisovskiy.meal_planner_api.domain.RecipeIngredient;
import dev.lisovskiy.meal_planner_api.repository.entity.RecipeIngredientEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {IngredientEntityMapper.class})
public interface RecipeIngredientEntityMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "recipe", ignore = true)
    RecipeIngredientEntity toRecipeIngredientEntity(RecipeIngredient recipeIngredient);
    RecipeIngredient toRecipeIngredient(RecipeIngredientEntity recipeIngredientEntity);
}
