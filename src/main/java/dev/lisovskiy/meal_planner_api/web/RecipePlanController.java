package dev.lisovskiy.meal_planner_api.web;

import dev.lisovskiy.meal_planner_api.domain.RecipePlan;
import dev.lisovskiy.meal_planner_api.dto.recipe_plan.RecipePlanDto;
import dev.lisovskiy.meal_planner_api.dto.recipe_plan.UpdateRecipePlanDto;
import dev.lisovskiy.meal_planner_api.service.core.recipe_plan.RecipePlanService;
import dev.lisovskiy.meal_planner_api.web.mapper.RecipePlanWebMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/recipe-plans")
@RequiredArgsConstructor
public class RecipePlanController {

    private final RecipePlanService recipePlanService;
    private final RecipePlanWebMapper recipePlanWebMapper;

    @GetMapping("/{id}")
    public ResponseEntity<RecipePlanDto> getRecipePlanById(@PathVariable Long id) {
        RecipePlan recipePlan = recipePlanService.getRecipePlanById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(recipePlanWebMapper.toRecipePlanDto(recipePlan));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecipePlanDto> updateRecipePlanById(
            @PathVariable Long id,
            @RequestBody @Valid UpdateRecipePlanDto updateRecipePlanDto
    ) {
        RecipePlan recipePlan = recipePlanService.updateRecipePlanById(id, updateRecipePlanDto);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(recipePlanWebMapper.toRecipePlanDto(recipePlan));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RecipePlanDto> deleteRecipePlanById(@PathVariable Long id) {
        recipePlanService.deleteRecipePlanById(id);
        return ResponseEntity.noContent().build();
    }
}
