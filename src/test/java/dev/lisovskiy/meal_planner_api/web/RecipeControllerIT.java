package dev.lisovskiy.meal_planner_api.web;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.lisovskiy.meal_planner_api.AbstractIT;
import dev.lisovskiy.meal_planner_api.common.IngredientUnit;
import dev.lisovskiy.meal_planner_api.dto.recipe.RecipeDto;
import dev.lisovskiy.meal_planner_api.dto.recipe.RecipeListDto;
import dev.lisovskiy.meal_planner_api.repository.IngredientRepository;
import dev.lisovskiy.meal_planner_api.repository.RecipeIngredientRepository;
import dev.lisovskiy.meal_planner_api.repository.RecipeRepository;
import dev.lisovskiy.meal_planner_api.repository.entity.IngredientEntity;
import dev.lisovskiy.meal_planner_api.repository.entity.RecipeEntity;
import dev.lisovskiy.meal_planner_api.repository.entity.RecipeIngredientEntity;
import dev.lisovskiy.meal_planner_api.service.mapper.RecipeEntityMapper;
import dev.lisovskiy.meal_planner_api.util.GlobalDtoSnippetsProvider;
import dev.lisovskiy.meal_planner_api.util.GlobalExtractor;
import dev.lisovskiy.meal_planner_api.util.RecipeDtoSnippetProvider;
import dev.lisovskiy.meal_planner_api.web.mapper.RecipeWebMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("RecipeController IT")
public class RecipeControllerIT extends AbstractIT {

    private final String SCHEMA_TAG = "Recipes";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private RecipeIngredientRepository recipeIngredientRepository;

    @Autowired
    private RecipeEntityMapper recipeEntityMapper;

    @Autowired
    private RecipeWebMapper recipeWebMapper;

    @AfterEach
    public void cleanUp() {
        recipeIngredientRepository.deleteAll();
        recipeRepository.deleteAll();
        ingredientRepository.deleteAll();
    }

    @Test
    @SneakyThrows
    @DisplayName("GetAllRecipes - Should Return List")
    public void getAllRecipes_shouldReturnList() {
        // Arrange
        List<RecipeEntity> recipeEntities = List.of(
                RecipeEntity.builder()
                        .title("Recipe 1")
                        .instructions("Instruction of Recipe 1")
                        .prepTimeMinutes(1)
                        .build(),
                RecipeEntity.builder()
                        .title("Recipe 2")
                        .instructions("Instruction of Recipe 2")
                        .prepTimeMinutes(3)
                        .build()
        );
        recipeEntities = recipeRepository.saveAll(recipeEntities);

        List<IngredientEntity> ingredientEntities = List.of(
                IngredientEntity.builder()
                        .name("Ingredient 1")
                        .description("Description of Ingredient 1")
                        .build(),
                IngredientEntity.builder()
                        .name("Ingredient 2")
                        .description("Description of Ingredient 2")
                        .build()
        );
        ingredientEntities = ingredientRepository.saveAll(ingredientEntities);

        List<RecipeIngredientEntity> recipeIngredientEntities = List.of(
                RecipeIngredientEntity.builder()
                        .recipe(recipeEntities.getFirst())
                        .ingredient(ingredientEntities.getFirst())
                        .unit(IngredientUnit.GRAMS)
                        .quantity(50.0)
                        .build(),
                RecipeIngredientEntity.builder()
                        .recipe(recipeEntities.get(1))
                        .ingredient(ingredientEntities.get(1))
                        .unit(IngredientUnit.MILLILITERS)
                        .quantity(120.0)
                        .build()
        );
        recipeIngredientRepository.saveAll(recipeIngredientEntities);

        recipeEntities.getFirst().setRecipeIngredients(List.of(
                recipeIngredientEntities.getFirst()
        ));
        recipeEntities.get(1).setRecipeIngredients(List.of(
                recipeIngredientEntities.get(1)
        ));

        List<RecipeDto> expectedList = recipeEntities.stream()
                .map(recipeEntityMapper::toRecipe)
                .map(recipeWebMapper::toRecipeDto)
                .toList();

        // Act
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/recipes")
                        .accept(APPLICATION_JSON_VALUE));

        MvcResult mvcResult = resultActions.andReturn();

        // Assert
        resultActions.andExpect(status().isOk());

        RecipeListDto actualResult = GlobalExtractor.getObjectFromMvcResult(
                mvcResult, RecipeListDto.class, objectMapper
        );

        assertThat(actualResult.getRecipes())
                .isEqualTo(expectedList);

        // Document
        resultActions.andDo(document("get-all-recipes",
                resource(
                        ResourceSnippetParameters.builder()
                                .tag(SCHEMA_TAG)
                                .summary("Get All Recipes")
                                .description("Gets all existing recipes from database.")
                                .responseSchema(Schema.schema("RecipeListDto"))
                                .responseFields(
                                        RecipeDtoSnippetProvider.getRecipeListDtoFields()
                                )
                                .build()
                )
        ));
    }

    @Test
    @SneakyThrows
    @DisplayName("GetRecipeById - Should Return Recipe")
    public void getRecipeById_shouldReturnRecipe() {
        // Arrange
        RecipeEntity recipeEntity = RecipeEntity.builder()
                .title("Recipe 1")
                .instructions("Instruction of Recipe 1")
                .prepTimeMinutes(1)
                .build();
        recipeEntity = recipeRepository.save(recipeEntity);

        List<IngredientEntity> ingredientEntities = List.of(
                IngredientEntity.builder()
                        .name("Ingredient 1")
                        .description("Description of Ingredient 1")
                        .build(),
                IngredientEntity.builder()
                        .name("Ingredient 2")
                        .description("Description of Ingredient 2")
                        .build()
        );
        ingredientEntities = ingredientRepository.saveAll(ingredientEntities);

        List<RecipeIngredientEntity> recipeIngredientEntities = List.of(
                RecipeIngredientEntity.builder()
                        .recipe(recipeEntity)
                        .ingredient(ingredientEntities.getFirst())
                        .unit(IngredientUnit.GRAMS)
                        .quantity(50.0)
                        .build(),
                RecipeIngredientEntity.builder()
                        .recipe(recipeEntity)
                        .ingredient(ingredientEntities.get(1))
                        .unit(IngredientUnit.MILLILITERS)
                        .quantity(120.0)
                        .build()
        );
        recipeIngredientRepository.saveAll(recipeIngredientEntities);

        recipeEntity.setRecipeIngredients(recipeIngredientEntities);

        RecipeDto expectedResult = recipeWebMapper.toRecipeDto(recipeEntityMapper.toRecipe(
                recipeEntity
        ));

        Long recipeId = recipeEntity.getId();

        // Act
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/recipes/{id}", recipeId)
                        .accept(APPLICATION_JSON_VALUE));

        MvcResult mvcResult = resultActions.andReturn();

        // Assert
        resultActions.andExpect(status().isOk());

        RecipeDto actualResult = GlobalExtractor.getObjectFromMvcResult(
                mvcResult, RecipeDto.class, objectMapper
        );

        assertThat(actualResult)
                .isEqualTo(expectedResult);

        // Document
        resultActions.andDo(document("get-recipe-by-id",
                resource(
                        ResourceSnippetParameters.builder()
                                .tag(SCHEMA_TAG)
                                .summary("Get Recipe By Id")
                                .description("Gets recipe by ID if exists.")
                                .pathParameters(
                                        parameterWithName("id")
                                                .description("The recipe ID")
                                )
                                .responseSchema(Schema.schema("RecipeDto"))
                                .responseFields(
                                        RecipeDtoSnippetProvider.getRecipeDtoFields()
                                )
                                .build()
                )
        ));
    }

    @Test
    @SneakyThrows
    @DisplayName("GetRecipeById - Should Return 404 Not Found")
    public void getRecipeById_shouldReturn404NotFound() {
        // Arrange
        Long recipeId = 1L;

        // Act
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/recipes/{id}", recipeId)
                        .accept(APPLICATION_PROBLEM_JSON));

        // Assert
        resultActions.andExpect(status().isNotFound());

        assertThat(recipeRepository.existsById(recipeId))
                .isFalse();

        // Document
        resultActions.andDo(document("get-recipe-by-id-not-found",
                resource(
                        ResourceSnippetParameters.builder()
                                .tag(SCHEMA_TAG)
                                .summary("Get Recipe By Id")
                                .description("Gets recipe by ID if exists.")
                                .pathParameters(
                                        parameterWithName("id")
                                                .description("The recipe ID")
                                )
                                .responseSchema(Schema.schema("ProblemDetail"))
                                .build()
                )
        ));
    }
}
