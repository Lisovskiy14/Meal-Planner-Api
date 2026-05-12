package dev.lisovskiy.meal_planner_api.service.mapper;

import dev.lisovskiy.meal_planner_api.domain.RecipeIngredient;
import dev.lisovskiy.meal_planner_api.repository.entity.RecipeIngredientEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {IngredientEntityMapper.class})
public interface RecipeIngredientEntityMapper {
    RecipeIngredientEntity toRecipeIngredientEntity(RecipeIngredient recipeIngredient);
    RecipeIngredient toRecipeIngredient(RecipeIngredientEntity recipeIngredientEntity);
}
