package dev.lisovskiy.meal_planner_api.service.impl;

import dev.lisovskiy.meal_planner_api.domain.Recipe;
import dev.lisovskiy.meal_planner_api.dto.recipe.CreateRecipeDto;
import dev.lisovskiy.meal_planner_api.dto.recipe.UpdateRecipeDto;
import dev.lisovskiy.meal_planner_api.dto.recipe_ingredient.CreateRecipeIngredientDto;
import dev.lisovskiy.meal_planner_api.repository.RecipeRepository;
import dev.lisovskiy.meal_planner_api.repository.entity.RecipeEntity;
import dev.lisovskiy.meal_planner_api.service.RecipeService;
import dev.lisovskiy.meal_planner_api.service.exception.conflict.impl.RecipeAlreadyExistsException;
import dev.lisovskiy.meal_planner_api.service.exception.not_found.impl.RecipeNotFoundException;
import dev.lisovskiy.meal_planner_api.service.mapper.RecipeEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository recipeRepository;
    private final RecipeEntityMapper recipeEntityMapper;

    @Override
    @Transactional(readOnly = true)
    public List<Recipe> getAllRecipes() {
        List<RecipeEntity> recipeEntities = recipeRepository.findAll();
        return recipeEntities.stream()
                .map(recipeEntityMapper::toRecipe)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Recipe getRecipeById(Long id) {
        RecipeEntity recipeEntity = recipeRepository.findById(id)
                .orElseThrow(() -> new RecipeNotFoundException(id));
        return recipeEntityMapper.toRecipe(recipeEntity);
    }

    @Override
    @Transactional
    public Recipe createRecipe(CreateRecipeDto createRecipeDto) {
        boolean titleAlreadyExists = recipeRepository.findByTitle(createRecipeDto.getTitle());
        if (titleAlreadyExists) {
            throw new RecipeAlreadyExistsException(createRecipeDto.getTitle());
        }

        RecipeEntity recipeEntity = RecipeEntity.builder()
                .title(createRecipeDto.getTitle())
                .instructions(createRecipeDto.getInstructions())
                .prepTimeMinutes(createRecipeDto.getPrepTimeMinutes())
                .build();

        recipeEntity = recipeRepository.save(recipeEntity);

        return recipeEntityMapper.toRecipe(recipeEntity);
    }

    @Override
    @Transactional
    public Recipe updateRecipeById(Long id, UpdateRecipeDto updateRecipeDto) {
        RecipeEntity recipeEntity = recipeEntityMapper.toRecipeEntity(getRecipeById(id));

        String title = updateRecipeDto.getTitle();
        boolean titleAlreadyExists = recipeRepository.findByTitle(title);
        if (titleAlreadyExists && !recipeEntity.getTitle().equals(title)) {
            throw new RecipeAlreadyExistsException(title);
        }

        recipeEntity.setTitle(title);
        recipeEntity.setInstructions(updateRecipeDto.getInstructions());
        recipeEntity.setPrepTimeMinutes(updateRecipeDto.getPrepTimeMinutes());

        recipeEntity = recipeRepository.save(recipeEntity);

        return recipeEntityMapper.toRecipe(recipeEntity);
    }

    @Override
    @Transactional
    public void deleteRecipeById(Long id) {
        recipeRepository.deleteById(id);
    }
}
