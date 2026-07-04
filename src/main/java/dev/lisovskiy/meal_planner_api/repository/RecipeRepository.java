package dev.lisovskiy.meal_planner_api.repository;

import dev.lisovskiy.meal_planner_api.repository.entity.impl.RecipeEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeRepository extends JpaRepository<RecipeEntity, Long> {

    @EntityGraph(attributePaths = {"recipeIngredients", "recipeIngredients.ingredient"})
    Optional<RecipeEntity> findWithIngredientsById(Long id);

    boolean existsByTitle(String title);
}
