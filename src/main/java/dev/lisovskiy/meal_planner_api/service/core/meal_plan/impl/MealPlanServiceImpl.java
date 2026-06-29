package dev.lisovskiy.meal_planner_api.service.core.meal_plan.impl;

import dev.lisovskiy.meal_planner_api.domain.MealPlan;
import dev.lisovskiy.meal_planner_api.dto.meal_plan.CreateMealPlanDto;
import dev.lisovskiy.meal_planner_api.dto.meal_plan.UpdateMealPlanDto;
import dev.lisovskiy.meal_planner_api.repository.MealPlanRepository;
import dev.lisovskiy.meal_planner_api.repository.entity.MealPlanEntity;
import dev.lisovskiy.meal_planner_api.service.core.meal_plan.MealPlanService;
import dev.lisovskiy.meal_planner_api.service.core.meal_plan.MealPlanServiceCommunicator;
import dev.lisovskiy.meal_planner_api.service.exception.not_found.impl.MealPlanNotFoundException;
import dev.lisovskiy.meal_planner_api.service.mapper.MealPlanEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MealPlanServiceImpl implements MealPlanService, MealPlanServiceCommunicator {

    private final MealPlanRepository mealPlanRepository;
    private final MealPlanEntityMapper mealPlanEntityMapper;

    @Override
    @Transactional(readOnly = true)
    public List<MealPlan> getAllMealPlans() {
        return mealPlanRepository.findAll().stream()
                .map(mealPlanEntityMapper::toMealPlan)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MealPlan getMealPlanById(Long id) {
        MealPlanEntity mealPlanEntity = getMealPlanEntityById(id);
        return mealPlanEntityMapper.toMealPlan(mealPlanEntity);
    }

    @Override
    @Transactional
    public MealPlan createMealPlan(CreateMealPlanDto createMealPlanDto) {
        DayOfWeek dayOfWeek = DayOfWeek.valueOf(createMealPlanDto.getDayOfWeek());

        MealPlanEntity mealPlanEntity = MealPlanEntity.builder()
                .description(createMealPlanDto.getDescription())
                .dayOfWeek(dayOfWeek)
                .build();

        mealPlanEntity = mealPlanRepository.save(mealPlanEntity);
        return mealPlanEntityMapper.toMealPlan(mealPlanEntity);
    }

    @Override
    @Transactional
    public MealPlan updateMealPlanById(Long id, UpdateMealPlanDto updateMealPlanDto) {
        MealPlanEntity mealPlanEntity = getMealPlanEntityById(id);

        DayOfWeek dayOfWeek = DayOfWeek.valueOf(updateMealPlanDto.getDayOfWeek());

        mealPlanEntity.setDayOfWeek(dayOfWeek);
        mealPlanEntity.setDescription(updateMealPlanDto.getDescription());

        mealPlanEntity = mealPlanRepository.save(mealPlanEntity);
        return mealPlanEntityMapper.toMealPlan(mealPlanEntity);
    }

    @Override
    @Transactional
    public void deleteMealPlanById(Long id) {
        mealPlanRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public MealPlanEntity getMealPlanEntityById(Long id) {
        return mealPlanRepository.findById(id)
                .orElseThrow(() -> new MealPlanNotFoundException(id));
    }
}
