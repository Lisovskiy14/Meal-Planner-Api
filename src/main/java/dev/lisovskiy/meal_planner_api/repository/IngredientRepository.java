package dev.lisovskiy.meal_planner_api.repository;

import dev.lisovskiy.meal_planner_api.repository.entity.IngredientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientRepository extends JpaRepository<IngredientEntity, Long> {
    boolean existsByName(String name);
}
