package dev.lisovskiy.meal_planner_api.repository;

import dev.lisovskiy.meal_planner_api.repository.entity.impl.MealPlanEntity;
import dev.lisovskiy.meal_planner_api.repository.entity.impl.RecipePlanEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecipePlanRepository extends JpaRepository<RecipePlanEntity, Long> {

    @EntityGraph(attributePaths = {"recipe"})
    List<RecipePlanEntity> findAllWithRecipeByMealPlan(MealPlanEntity mealPlan);

    @EntityGraph(attributePaths = {"recipe"})
    Optional<RecipePlanEntity> findWithRecipeById(Long id);

    boolean existsByMealPlan_IdAndTime(Long mealPlanId, LocalTime time);
}
