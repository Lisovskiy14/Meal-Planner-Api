package dev.lisovskiy.meal_planner_api.repository;

import dev.lisovskiy.meal_planner_api.repository.entity.impl.MealPlanEntity;
import dev.lisovskiy.meal_planner_api.repository.entity.impl.RecipePlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface RecipePlanRepository extends JpaRepository<RecipePlanEntity, Long> {
    List<RecipePlanEntity> findAllByMealPlan(MealPlanEntity mealPlan);

    boolean existsByMealPlan_IdAndTime(Long mealPlanId, LocalTime time);
}
