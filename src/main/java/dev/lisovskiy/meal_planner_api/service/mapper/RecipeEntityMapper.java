package dev.lisovskiy.meal_planner_api.service.mapper;

import dev.lisovskiy.meal_planner_api.domain.Recipe;
import dev.lisovskiy.meal_planner_api.repository.entity.impl.RecipeEntity;
import dev.lisovskiy.meal_planner_api.service.mapper.util.JpaCondition;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {JpaCondition.class, RecipeIngredientEntityMapper.class})
public interface RecipeEntityMapper {
    RecipeEntity toRecipeEntity(Recipe recipe);
    Recipe toRecipe(RecipeEntity recipeEntity);
}
