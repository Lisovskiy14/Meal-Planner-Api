package dev.lisovskiy.meal_planner_api.service.mapper;

import dev.lisovskiy.meal_planner_api.domain.Recipe;
import dev.lisovskiy.meal_planner_api.repository.entity.RecipeEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {RecipeIngredientEntityMapper.class})
public interface RecipeEntityMapper {
    RecipeEntity toRecipeEntity(Recipe recipe);
    Recipe toRecipe(RecipeEntity recipeEntity);
}
