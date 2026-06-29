package dev.lisovskiy.meal_planner_api.service.core.ingredient.impl;

import dev.lisovskiy.meal_planner_api.domain.Ingredient;
import dev.lisovskiy.meal_planner_api.dto.ingredient.CreateIngredientDto;
import dev.lisovskiy.meal_planner_api.dto.ingredient.UpdateIngredientDto;
import dev.lisovskiy.meal_planner_api.repository.IngredientRepository;
import dev.lisovskiy.meal_planner_api.repository.entity.IngredientEntity;
import dev.lisovskiy.meal_planner_api.service.exception.conflict.impl.IngredientAlreadyExistsException;
import dev.lisovskiy.meal_planner_api.service.exception.not_found.impl.IngredientNotFoundException;
import dev.lisovskiy.meal_planner_api.service.core.ingredient.IngredientService;
import dev.lisovskiy.meal_planner_api.service.core.ingredient.IngredientServiceCommunicator;
import dev.lisovskiy.meal_planner_api.service.mapper.IngredientEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IngredientServiceImpl implements IngredientService, IngredientServiceCommunicator {

    private final IngredientRepository ingredientRepository;
    private final IngredientEntityMapper ingredientEntityMapper;

    @Override
    @Transactional(readOnly = true)
    public List<Ingredient> getAllIngredients() {
        return ingredientRepository.findAll().stream()
                .map(ingredientEntityMapper::toIngredient)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Ingredient getIngredientById(Long id) {
        IngredientEntity ingredientEntity = getIngredientEntityById(id);
        return ingredientEntityMapper.toIngredient(ingredientEntity);
    }

    @Override
    @Transactional
    public Ingredient createIngredient(CreateIngredientDto createIngredientDto) {
        boolean nameAlreadyExists = ingredientRepository.existsByName(createIngredientDto.getName());
        if (nameAlreadyExists) {
            throw new IngredientAlreadyExistsException(createIngredientDto.getName());
        }

        IngredientEntity entity = IngredientEntity.builder()
                .name(createIngredientDto.getName())
                .description(createIngredientDto.getDescription())
                .build();

        entity = ingredientRepository.save(entity);

        return ingredientEntityMapper.toIngredient(entity);
    }

    @Override
    @Transactional
    public Ingredient updateIngredient(Long id, UpdateIngredientDto updateIngredientDto) {
        IngredientEntity existingEntity = ingredientEntityMapper
                .toIngredientEntity(getIngredientById(id));

        String updatedName = updateIngredientDto.getName();
        if (!existingEntity.getName().equals(updatedName) &&
                ingredientRepository.existsByName(updatedName)) {
            throw new IngredientAlreadyExistsException(updatedName);
        }

        existingEntity.setName(updatedName);
        existingEntity.setDescription(updateIngredientDto.getDescription());

        existingEntity = ingredientRepository.save(existingEntity);

        return ingredientEntityMapper.toIngredient(existingEntity);
    }

    @Override
    @Transactional
    public void deleteIngredientById(Long id) {
        ingredientRepository.deleteById(id);
    }

    @Override
    @Transactional
    public IngredientEntity getIngredientEntityById(Long id) {
        return ingredientRepository.findById(id)
                .orElseThrow(() -> new IngredientNotFoundException(id));
    }
}
