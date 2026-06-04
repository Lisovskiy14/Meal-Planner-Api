package dev.lisovskiy.meal_planner_api.repository;

import dev.lisovskiy.meal_planner_api.repository.entity.RecipePlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipePlanRepository extends JpaRepository<RecipePlanEntity, Long> {
}
