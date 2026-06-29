package dev.lisovskiy.meal_planner_api.service.core.recipe_ingredient.impl;

import dev.lisovskiy.meal_planner_api.domain.RecipeIngredient;
import dev.lisovskiy.meal_planner_api.dto.recipe_ingredient.CreateRecipeIngredientDto;
import dev.lisovskiy.meal_planner_api.dto.recipe_ingredient.UpdateRecipeIngredientDto;
import dev.lisovskiy.meal_planner_api.repository.RecipeIngredientRepository;
import dev.lisovskiy.meal_planner_api.repository.entity.IngredientEntity;
import dev.lisovskiy.meal_planner_api.repository.entity.RecipeEntity;
import dev.lisovskiy.meal_planner_api.repository.entity.RecipeIngredientEntity;
import dev.lisovskiy.meal_planner_api.repository.entity.RecipeIngredientId;
import dev.lisovskiy.meal_planner_api.service.core.ingredient.IngredientServiceCommunicator;
import dev.lisovskiy.meal_planner_api.service.core.recipe.RecipeService;
import dev.lisovskiy.meal_planner_api.service.core.recipe.RecipeServiceCommunicator;
import dev.lisovskiy.meal_planner_api.service.exception.not_found.impl.RecipeIngredientNotFoundException;
import dev.lisovskiy.meal_planner_api.service.mapper.IngredientEntityMapper;
import dev.lisovskiy.meal_planner_api.service.mapper.RecipeEntityMapper;
import dev.lisovskiy.meal_planner_api.service.mapper.RecipeIngredientEntityMapper;
import dev.lisovskiy.meal_planner_api.service.core.recipe_ingredient.RecipeIngredientService;
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

    private final RecipeServiceCommunicator recipeServiceCommunicator;
    private final RecipeEntityMapper recipeEntityMapper;

    private final IngredientServiceCommunicator ingredientServiceCommunicator;
    private final IngredientEntityMapper ingredientEntityMapper;

    @Override
    @Transactional(readOnly = true)
    public List<RecipeIngredient> getAllRecipeIngredients(Long recipeId) {
        recipeServiceCommunicator.getRecipeEntityById(recipeId);
        return recipeIngredientRepository.findAllByRecipe_Id(recipeId).stream()
                .map(recipeIngredientEntityMapper::toRecipeIngredient)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RecipeIngredient getRecipeIngredientById(Long recipeId, Long ingredientId) {
        RecipeIngredientEntity recipeIngredientEntity = getRecipeIngredientEntityById(recipeId, ingredientId);
        return recipeIngredientEntityMapper.toRecipeIngredient(recipeIngredientEntity);
    }

    @Override
    @Transactional
    public List<RecipeIngredient> createRecipeIngredients(
            Long recipeId,
            List<CreateRecipeIngredientDto> createRecipeIngredientDtoList
    ) {
        List<RecipeIngredientEntity> recipeIngredientEntities = new ArrayList<>();
        RecipeEntity recipeEntity = recipeServiceCommunicator.getRecipeEntityById(recipeId);

        for (CreateRecipeIngredientDto createRecipeIngredientDto : createRecipeIngredientDtoList) {

            IngredientEntity ingredientEntity = ingredientServiceCommunicator.getIngredientEntityById(
                    createRecipeIngredientDto.getIngredientId()
            );

            RecipeIngredientEntity recipeIngredientEntity = RecipeIngredientEntity.builder()
                    .recipe(recipeEntity)
                    .ingredient(ingredientEntity)
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
    public RecipeIngredient updateRecipeIngredientById(
            Long recipeId, Long ingredientId,
            UpdateRecipeIngredientDto updateRecipeIngredientDto
    ) {
        RecipeIngredientEntity recipeIngredientEntity = getRecipeIngredientEntityById(recipeId, ingredientId);

        Long newIngredientId = updateRecipeIngredientDto.getIngredientId();
        if (!ingredientId.equals(newIngredientId)) {
            IngredientEntity newIngredientEntity = ingredientServiceCommunicator
                    .getIngredientEntityById(updateRecipeIngredientDto.getIngredientId());

            recipeIngredientRepository.delete(recipeIngredientEntity);

            recipeIngredientEntity = RecipeIngredientEntity.builder()
                    .recipe(recipeIngredientEntity.getRecipe())
                    .ingredient(newIngredientEntity)
                    .build();
        }

        recipeIngredientEntity.setUnit(IngredientUnitMapper
                .fromString(updateRecipeIngredientDto.getUnit()));
        recipeIngredientEntity.setQuantity(updateRecipeIngredientDto.getQuantity());

        recipeIngredientEntity = recipeIngredientRepository.save(recipeIngredientEntity);

        return recipeIngredientEntityMapper.toRecipeIngredient(recipeIngredientEntity);
    }

    @Override
    @Transactional
    public void deleteRecipeIngredientById(Long recipeId, Long ingredientId) {
        recipeIngredientRepository.deleteById(
                new RecipeIngredientId(recipeId, ingredientId));
    }

    private RecipeIngredientEntity getRecipeIngredientEntityById(Long recipeId, Long ingredientId) {
        recipeServiceCommunicator.getRecipeEntityById(recipeId);
        ingredientServiceCommunicator.getIngredientEntityById(ingredientId);

        RecipeIngredientId recipeIngredientId = new RecipeIngredientId(recipeId, ingredientId);

        return recipeIngredientRepository.findById(recipeIngredientId)
                .orElseThrow(() ->
                        new RecipeIngredientNotFoundException(
                                recipeIngredientId.getRecipeId(),
                                recipeIngredientId.getIngredientId()
                        )
                );
    }
}
