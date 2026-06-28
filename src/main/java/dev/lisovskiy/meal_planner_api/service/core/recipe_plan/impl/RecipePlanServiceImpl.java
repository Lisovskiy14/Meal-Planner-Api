package dev.lisovskiy.meal_planner_api.service.core.recipe_plan.impl;

import dev.lisovskiy.meal_planner_api.domain.RecipePlan;
import dev.lisovskiy.meal_planner_api.dto.recipe_plan.CreateRecipePlanDto;
import dev.lisovskiy.meal_planner_api.dto.recipe_plan.UpdateRecipePlanDto;
import dev.lisovskiy.meal_planner_api.repository.RecipePlanRepository;
import dev.lisovskiy.meal_planner_api.repository.entity.MealPlanEntity;
import dev.lisovskiy.meal_planner_api.repository.entity.RecipeEntity;
import dev.lisovskiy.meal_planner_api.repository.entity.RecipePlanEntity;
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
        MealPlanEntity mealPlanEntity = mealPlanServiceCommunicator.getMealPlanEntityById(mealPlanId);
        return recipePlanRepository.findAllByMealPlan(mealPlanEntity).stream()
                .map(recipePlanEntityMapper::toRecipePlan)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RecipePlan getRecipePlanById(Long recipePlanId) {
        RecipePlanEntity recipePlanEntity = getRecipePlanEntityById(recipePlanId);
        return recipePlanEntityMapper.toRecipePlan(recipePlanEntity);
    }

    @Override
    @Transactional
    public RecipePlan createRecipePlan(Long mealPlanId, CreateRecipePlanDto createRecipePlanDto) {
        MealPlanEntity mealPlanEntity = mealPlanServiceCommunicator.getMealPlanEntityById(mealPlanId);
        RecipeEntity recipeEntity = recipeServiceCommunicator.getRecipeEntityById(createRecipePlanDto.getRecipeId());

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
        RecipePlanEntity recipePlanEntity = getRecipePlanEntityById(recipePlanId);

        MealPlanEntity mealPlanEntity = mealPlanServiceCommunicator.getMealPlanEntityById(
                updateRecipePlanDto.getMealPlanId()
        );
        RecipeEntity recipeEntity = recipeServiceCommunicator.getRecipeEntityById(
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

    private RecipePlanEntity getRecipePlanEntityById(Long recipePlanId) {
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
