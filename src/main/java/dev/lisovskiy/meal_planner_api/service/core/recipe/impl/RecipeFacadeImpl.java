package dev.lisovskiy.meal_planner_api.service.core.recipe.impl;

import dev.lisovskiy.meal_planner_api.domain.Recipe;
import dev.lisovskiy.meal_planner_api.domain.RecipeIngredient;
import dev.lisovskiy.meal_planner_api.dto.recipe.CreateRecipeDto;
import dev.lisovskiy.meal_planner_api.dto.recipe_ingredient.CreateRecipeIngredientDto;
import dev.lisovskiy.meal_planner_api.service.core.recipe.RecipeFacade;
import dev.lisovskiy.meal_planner_api.service.core.recipe_ingredient.RecipeIngredientService;
import dev.lisovskiy.meal_planner_api.service.core.recipe.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecipeFacadeImpl implements RecipeFacade {

    private final RecipeService recipeService;
    private final RecipeIngredientService recipeIngredientService;

    @Override
    @Transactional
    public Recipe createRecipeWithIngredients(CreateRecipeDto createRecipeDto) {
        Recipe recipe = recipeService.createRecipe(createRecipeDto);

        List<CreateRecipeIngredientDto> createRecipeIngredientDtoList = createRecipeDto.getRecipeIngredients();
        if (createRecipeIngredientDtoList == null || createRecipeIngredientDtoList.isEmpty()) {
            return recipe;
        }

        List<RecipeIngredient> recipeIngredients = recipeIngredientService
                .createRecipeIngredients(recipe.getId(), createRecipeIngredientDtoList);

        recipe.setRecipeIngredients(recipeIngredients);

        return recipe;
    }
}
