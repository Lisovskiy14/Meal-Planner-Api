package dev.lisovskiy.meal_planner_api.service.impl;

import dev.lisovskiy.meal_planner_api.domain.Ingredient;
import dev.lisovskiy.meal_planner_api.domain.Recipe;
import dev.lisovskiy.meal_planner_api.domain.RecipeIngredient;
import dev.lisovskiy.meal_planner_api.dto.recipe_ingredient.CreateRecipeIngredientDto;
import dev.lisovskiy.meal_planner_api.dto.recipe_ingredient.UpdateRecipeIngredientDto;
import dev.lisovskiy.meal_planner_api.repository.RecipeIngredientRepository;
import dev.lisovskiy.meal_planner_api.repository.entity.RecipeIngredientEntity;
import dev.lisovskiy.meal_planner_api.service.IngredientService;
import dev.lisovskiy.meal_planner_api.service.RecipeIngredientService;
import dev.lisovskiy.meal_planner_api.service.RecipeService;
import dev.lisovskiy.meal_planner_api.service.exception.not_found.impl.RecipeIngredientNotFoundException;
import dev.lisovskiy.meal_planner_api.service.mapper.IngredientEntityMapper;
import dev.lisovskiy.meal_planner_api.service.mapper.RecipeEntityMapper;
import dev.lisovskiy.meal_planner_api.service.mapper.RecipeIngredientEntityMapper;
import dev.lisovskiy.meal_planner_api.util.IngredientUnitMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecipeIngredientServiceImpl implements RecipeIngredientService {

    private final RecipeIngredientRepository recipeIngredientRepository;
    private final RecipeIngredientEntityMapper recipeIngredientEntityMapper;

    private final RecipeService recipeService;
    private final RecipeEntityMapper recipeEntityMapper;

    private final IngredientService ingredientService;
    private final IngredientEntityMapper ingredientEntityMapper;

    @Override
    @Transactional(readOnly = true)
    public List<RecipeIngredient> getAllRecipeIngredients() {
        return recipeIngredientRepository.findAll().stream()
                .map(recipeIngredientEntityMapper::toRecipeIngredient)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RecipeIngredient getRecipeIngredientById(Long id) {
        RecipeIngredientEntity recipeIngredientEntity = recipeIngredientRepository.findById(id)
                .orElseThrow(() -> new RecipeIngredientNotFoundException(id));
        return recipeIngredientEntityMapper.toRecipeIngredient(recipeIngredientEntity);
    }

    @Override
    @Transactional
    public List<RecipeIngredient> createRecipeIngredients(List<CreateRecipeIngredientDto> createRecipeIngredientDtoList) {

        List<RecipeIngredientEntity> recipeIngredientEntities = new ArrayList<>();

        for (CreateRecipeIngredientDto createRecipeIngredientDto : createRecipeIngredientDtoList) {
            Recipe recipe = recipeService.getRecipeById(createRecipeIngredientDto.getRecipeId());
            Ingredient ingredient = ingredientService.getIngredientById(createRecipeIngredientDto.getIngredientId());

            RecipeIngredientEntity recipeIngredientEntity = RecipeIngredientEntity.builder()
                    .recipe(recipeEntityMapper.toRecipeEntity(recipe))
                    .ingredient(ingredientEntityMapper.toIngredientEntity(ingredient))
                    .unit(IngredientUnitMapper.fromString(createRecipeIngredientDto.getUnit()))
                    .quantity(createRecipeIngredientDto.getQuantity())
                    .build();

            recipeIngredientEntities.add(recipeIngredientEntity);
        }

        recipeIngredientEntities = recipeIngredientRepository.saveAll(recipeIngredientEntities);

        return recipeIngredientEntities.stream()
                .map(recipeIngredientEntityMapper::toRecipeIngredient)
                .toList();
    }

    @Override
    @Transactional
    public RecipeIngredient updateRecipeIngredient(Long id, UpdateRecipeIngredientDto updateRecipeIngredientDto) {
        RecipeIngredient recipeIngredient = getRecipeIngredientById(id);
        RecipeIngredientEntity recipeIngredientEntity = recipeIngredientEntityMapper
                .toRecipeIngredientEntity(recipeIngredient);

        Ingredient ingredient = ingredientService.getIngredientById(updateRecipeIngredientDto.getIngredientId());

        recipeIngredientEntity.setIngredient(ingredientEntityMapper.toIngredientEntity(ingredient));
        recipeIngredientEntity.setUnit(IngredientUnitMapper
                .fromString(updateRecipeIngredientDto.getUnit()));
        recipeIngredientEntity.setQuantity(updateRecipeIngredientDto.getQuantity());

        recipeIngredientEntity = recipeIngredientRepository.save(recipeIngredientEntity);

        return recipeIngredientEntityMapper.toRecipeIngredient(recipeIngredientEntity);
    }

    @Override
    @Transactional
    public void deleteRecipeIngredientById(Long id) {
        recipeIngredientRepository.deleteById(id);
    }
}
