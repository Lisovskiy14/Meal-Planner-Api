package dev.lisovskiy.meal_planner_api.web;

import dev.lisovskiy.meal_planner_api.domain.Recipe;
import dev.lisovskiy.meal_planner_api.dto.recipe.CreateRecipeDto;
import dev.lisovskiy.meal_planner_api.dto.recipe.RecipeDto;
import dev.lisovskiy.meal_planner_api.dto.recipe.RecipeListDto;
import dev.lisovskiy.meal_planner_api.dto.recipe.UpdateRecipeDto;
import dev.lisovskiy.meal_planner_api.dto.validation.ValidLongId;
import dev.lisovskiy.meal_planner_api.service.RecipeFacade;
import dev.lisovskiy.meal_planner_api.service.RecipeService;
import dev.lisovskiy.meal_planner_api.web.mapper.RecipeWebMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/v1/recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;
    private final RecipeFacade recipeFacade;
    private final RecipeWebMapper recipeWebMapper;

    @GetMapping
    public ResponseEntity<RecipeListDto> getAllRecipes() {
        List<Recipe> recipes = recipeService.getAllRecipes();
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new RecipeListDto(
                        recipes.stream()
                                .map(recipeWebMapper::toRecipeDto)
                                .toList()
                ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipeDto> getRecipeById(@PathVariable @ValidLongId Long id) {
        Recipe recipe = recipeService.getRecipeById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(recipeWebMapper.toRecipeDto(recipe));
    }

    @PostMapping
    public ResponseEntity<RecipeDto> createRecipe(
            @RequestBody @Validated CreateRecipeDto createRecipeDto
    ) {
        Recipe recipe = recipeFacade.createRecipeWithIngredients(createRecipeDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(recipeWebMapper.toRecipeDto(recipe));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecipeDto> updateRecipeById(
            @PathVariable @ValidLongId Long id,
            @RequestBody @Validated UpdateRecipeDto updateRecipeDto
    ) {
        Recipe recipe = recipeService.updateRecipeById(id, updateRecipeDto);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(recipeWebMapper.toRecipeDto(recipe));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipeById(@PathVariable @ValidLongId Long id) {
        recipeService.deleteRecipeById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }
}
