package dev.lisovskiy.meal_planner_api.repository;

import dev.lisovskiy.meal_planner_api.repository.entity.impl.MealPlanEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MealPlanRepository extends JpaRepository<MealPlanEntity, Long> {

    @EntityGraph(attributePaths = {"recipePlans"})
    Optional<MealPlanEntity> findWithRecipePlansById(Long id);
}
