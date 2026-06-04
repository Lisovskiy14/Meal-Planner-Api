package dev.lisovskiy.meal_planner_api.repository;

import dev.lisovskiy.meal_planner_api.repository.entity.MealPlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MealPlanRepository extends JpaRepository<MealPlanEntity, Long> {
}
