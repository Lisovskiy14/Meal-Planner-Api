package dev.lisovskiy.meal_planner_api.service.core.recipe.impl;

import dev.lisovskiy.meal_planner_api.domain.Recipe;
import dev.lisovskiy.meal_planner_api.dto.recipe.CreateRecipeDto;
import dev.lisovskiy.meal_planner_api.dto.recipe.UpdateRecipeDto;
import dev.lisovskiy.meal_planner_api.repository.RecipeRepository;
import dev.lisovskiy.meal_planner_api.repository.entity.impl.RecipeEntity;
import dev.lisovskiy.meal_planner_api.service.core.recipe.RecipeServiceCommunicator;
import dev.lisovskiy.meal_planner_api.service.exception.conflict.impl.RecipeAlreadyExistsException;
import dev.lisovskiy.meal_planner_api.service.exception.not_found.impl.RecipeNotFoundException;
import dev.lisovskiy.meal_planner_api.service.mapper.RecipeEntityMapper;
import dev.lisovskiy.meal_planner_api.service.core.recipe.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService, RecipeServiceCommunicator {

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
        RecipeEntity recipeEntity = getRecipeEntityById(id);
        return recipeEntityMapper.toRecipe(recipeEntity);
    }

    @Override
    @Transactional
    public Recipe createRecipe(CreateRecipeDto createRecipeDto) {
        boolean titleAlreadyExists = recipeRepository.existsByTitle(createRecipeDto.getTitle());
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
        RecipeEntity recipeEntity = getRecipeEntityById(id);

        String title = updateRecipeDto.getTitle();
        boolean titleAlreadyExists = recipeRepository.existsByTitle(title);
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

    @Override
    @Transactional
    public RecipeEntity getRecipeEntityById(Long id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new RecipeNotFoundException(id));
    }
}
