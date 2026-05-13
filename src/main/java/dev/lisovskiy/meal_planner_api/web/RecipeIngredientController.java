package dev.lisovskiy.meal_planner_api.web;

import dev.lisovskiy.meal_planner_api.domain.RecipeIngredient;
import dev.lisovskiy.meal_planner_api.dto.recipe_ingredient.CreateRecipeIngredientDto;
import dev.lisovskiy.meal_planner_api.dto.recipe_ingredient.RecipeIngredientDto;
import dev.lisovskiy.meal_planner_api.dto.recipe_ingredient.RecipeIngredientListDto;
import dev.lisovskiy.meal_planner_api.dto.recipe_ingredient.UpdateRecipeIngredientDto;
import dev.lisovskiy.meal_planner_api.service.RecipeIngredientService;
import dev.lisovskiy.meal_planner_api.web.mapper.RecipeIngredientWebMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/recipe-ingredients")
@RequiredArgsConstructor
public class RecipeIngredientController {

    private final RecipeIngredientService recipeIngredientService;
    private final RecipeIngredientWebMapper recipeIngredientWebMapper;

    @GetMapping
    public ResponseEntity<RecipeIngredientListDto> getAllRecipeIngredients() {
        List<RecipeIngredient> recipeIngredients = recipeIngredientService.getAllRecipeIngredients();
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new RecipeIngredientListDto(
                        recipeIngredients.stream()
                                .map(recipeIngredientWebMapper::toRecipeIngredientDto)
                                .toList()
                ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipeIngredientDto> getRecipeIngredientById(@PathVariable Long id) {
        RecipeIngredient recipeIngredient = recipeIngredientService.getRecipeIngredientById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(recipeIngredientWebMapper.toRecipeIngredientDto(recipeIngredient));
    }

    @PostMapping
    public ResponseEntity<RecipeIngredientDto> createRecipeIngredient(
            @RequestBody @Validated CreateRecipeIngredientDto createRecipeIngredientDto
    ) {
        RecipeIngredient recipeIngredient = recipeIngredientService
                .createRecipeIngredient(createRecipeIngredientDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(recipeIngredientWebMapper.toRecipeIngredientDto(recipeIngredient));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecipeIngredientDto> updateRecipeIngredientById(
            @PathVariable Long id,
            @RequestBody @Validated UpdateRecipeIngredientDto updateRecipeIngredientDto
    ) {
        RecipeIngredient recipeIngredient = recipeIngredientService
                .updateRecipeIngredient(id, updateRecipeIngredientDto);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(recipeIngredientWebMapper.toRecipeIngredientDto(recipeIngredient));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RecipeIngredientDto> deleteRecipeIngredientById(@PathVariable Long id) {
        recipeIngredientService.deleteRecipeIngredientById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }
}
