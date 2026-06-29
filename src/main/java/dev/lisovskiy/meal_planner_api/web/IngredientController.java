package dev.lisovskiy.meal_planner_api.web;

import dev.lisovskiy.meal_planner_api.domain.Ingredient;
import dev.lisovskiy.meal_planner_api.dto.ingredient.CreateIngredientDto;
import dev.lisovskiy.meal_planner_api.dto.ingredient.IngredientDto;
import dev.lisovskiy.meal_planner_api.dto.ingredient.IngredientListDto;
import dev.lisovskiy.meal_planner_api.dto.ingredient.UpdateIngredientDto;
import dev.lisovskiy.meal_planner_api.dto.validation.ValidLongId;
import dev.lisovskiy.meal_planner_api.service.core.ingredient.IngredientService;
import dev.lisovskiy.meal_planner_api.web.mapper.IngredientWebMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ingredients")
@RequiredArgsConstructor
public class IngredientController {

    private final IngredientService ingredientService;
    private final IngredientWebMapper ingredientWebMapper;

    @GetMapping
    public ResponseEntity<IngredientListDto> getAllIngredients() {
        List<Ingredient> ingredients = ingredientService.getAllIngredients();
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new IngredientListDto(
                        ingredients.stream()
                                .map(ingredientWebMapper::toIngredientDto)
                                .toList()
                ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<IngredientDto> getIngredientById(@PathVariable @ValidLongId Long id) {
        Ingredient ingredient = ingredientService.getIngredientById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ingredientWebMapper.toIngredientDto(ingredient));
    }

    @PostMapping
    public ResponseEntity<IngredientDto> createIngredient(
            @RequestBody @Validated CreateIngredientDto createIngredientDto
    ) {
        Ingredient createdIngredient = ingredientService.createIngredient(createIngredientDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ingredientWebMapper.toIngredientDto(createdIngredient));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IngredientDto> updateIngredientById(
            @PathVariable @ValidLongId Long id,
            @RequestBody @Validated UpdateIngredientDto updateIngredientDto
    ) {
        Ingredient updatedIngredient = ingredientService.updateIngredient(id, updateIngredientDto);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ingredientWebMapper.toIngredientDto(updatedIngredient));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIngredientById(@PathVariable @ValidLongId Long id) {
        ingredientService.deleteIngredientById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }
}
