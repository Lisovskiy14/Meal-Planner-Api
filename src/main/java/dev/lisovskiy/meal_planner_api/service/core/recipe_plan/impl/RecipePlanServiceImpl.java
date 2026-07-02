package dev.lisovskiy.meal_planner_api.service.core.recipe_plan.impl;

import dev.lisovskiy.meal_planner_api.domain.RecipePlan;
import dev.lisovskiy.meal_planner_api.dto.recipe_plan.CreateRecipePlanDto;
import dev.lisovskiy.meal_planner_api.dto.recipe_plan.UpdateRecipePlanDto;
import dev.lisovskiy.meal_planner_api.repository.RecipePlanRepository;
import dev.lisovskiy.meal_planner_api.repository.entity.impl.MealPlanEntity;
import dev.lisovskiy.meal_planner_api.repository.entity.impl.RecipeEntity;
import dev.lisovskiy.meal_planner_api.repository.entity.impl.RecipePlanEntity;
import dev.lisovskiy.meal_planner_api.service.core.meal_plan.MealPlanServiceCommunicator;
import dev.lisovskiy.meal_planner_api.service.core.recipe.RecipeServiceCommunicator;
import dev.lisovskiy.meal_planner_api.service.core.recipe_plan.RecipePlanService;
import dev.lisovskiy.meal_planner_api.service.exception.conflict.impl.RecipePlanAlreadyExistsException;
import dev.lisovskiy.meal_planner_api.service.exception.not_found.impl.RecipePlanNotFoundException;
import dev.lisovskiy.meal_planner_api.service.mapper.RecipePlanEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecipePlanServiceImpl implements RecipePlanService {

    private final RecipePlanRepository recipePlanRepository;
    private final RecipePlanEntityMapper recipePlanEntityMapper;

    private final MealPlanServiceCommunicator mealPlanServiceCommunicator;
    private final RecipeServiceCommunicator recipeServiceCommunicator;

    @Override
    @Transactional(readOnly = true)
    public List<RecipePlan> getAllRecipePlansByMealPlanId(Long mealPlanId) {
        MealPlanEntity mealPlanEntity = mealPlanServiceCommunicator.getMealPlanEntitySummaryById(mealPlanId);
        return recipePlanRepository.findAllWithRecipeByMealPlan(mealPlanEntity).stream()
                .map(recipePlanEntityMapper::toRecipePlan)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RecipePlan getRecipePlanById(Long recipePlanId) {
        RecipePlanEntity recipePlanEntity = getRecipePlanEntityWithRecipeById(recipePlanId);
        return recipePlanEntityMapper.toRecipePlan(recipePlanEntity);
    }

    @Override
    @Transactional
    public RecipePlan createRecipePlan(Long mealPlanId, CreateRecipePlanDto createRecipePlanDto) {
        MealPlanEntity mealPlanEntity = mealPlanServiceCommunicator.getMealPlanEntitySummaryById(mealPlanId);
        RecipeEntity recipeEntity = recipeServiceCommunicator.getRecipeEntitySummaryById(createRecipePlanDto.getRecipeId());

        LocalTime time = LocalTime.parse(createRecipePlanDto.getTime());
        checkForConflict(time, mealPlanId);

        RecipePlanEntity recipePlanEntity = RecipePlanEntity.builder()
                .mealPlan(mealPlanEntity)
                .recipe(recipeEntity)
                .description(createRecipePlanDto.getDescription())
                .time(time)
                .build();

        recipePlanEntity = recipePlanRepository.save(recipePlanEntity);
        return recipePlanEntityMapper.toRecipePlan(recipePlanEntity);
    }

    @Override
    @Transactional
    public RecipePlan updateRecipePlanById(Long recipePlanId, UpdateRecipePlanDto updateRecipePlanDto) {
        RecipePlanEntity recipePlanEntity = getRecipePlanEntitySummaryById(recipePlanId);

        MealPlanEntity mealPlanEntity = mealPlanServiceCommunicator.getMealPlanEntitySummaryById(
                updateRecipePlanDto.getMealPlanId()
        );
        RecipeEntity recipeEntity = recipeServiceCommunicator.getRecipeEntitySummaryById(
                updateRecipePlanDto.getRecipeId()
        );

        LocalTime time = LocalTime.parse(updateRecipePlanDto.getTime());
        checkForConflict(time, mealPlanEntity.getId(), recipePlanEntity);

        recipePlanEntity.setMealPlan(mealPlanEntity);
        recipePlanEntity.setRecipe(recipeEntity);
        recipePlanEntity.setDescription(updateRecipePlanDto.getDescription());
        recipePlanEntity.setTime(time);

        recipePlanEntity = recipePlanRepository.save(recipePlanEntity);
        return recipePlanEntityMapper.toRecipePlan(recipePlanEntity);
    }

    @Override
    @Transactional
    public void deleteRecipePlanById(Long recipePlanId) {
        recipePlanRepository.deleteById(recipePlanId);
    }

    private RecipePlanEntity getRecipePlanEntityWithRecipeById(Long recipePlanId) {
        return recipePlanRepository.findWithRecipeById(recipePlanId)
                .orElseThrow(() -> new RecipePlanNotFoundException(recipePlanId));
    }

    private RecipePlanEntity getRecipePlanEntitySummaryById(Long recipePlanId) {
        return recipePlanRepository.findById(recipePlanId)
                .orElseThrow(() -> new RecipePlanNotFoundException(recipePlanId));
    }

    private void checkForConflict(LocalTime time, Long mealPlanId) {
        if (recipePlanRepository.existsByMealPlan_IdAndTime(mealPlanId, time)) {
            throw new RecipePlanAlreadyExistsException(time, mealPlanId);
        }
    }

    private void checkForConflict(LocalTime newTime, Long mealPlanId, RecipePlanEntity targetRecipePlanEntity) {
        if (targetRecipePlanEntity != null && targetRecipePlanEntity.getTime().equals(newTime)) {
            return;
        }

        if (recipePlanRepository.existsByMealPlan_IdAndTime(mealPlanId, newTime)) {
            throw new RecipePlanAlreadyExistsException(newTime, mealPlanId);
        }
    }
}
