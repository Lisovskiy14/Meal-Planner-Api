package dev.lisovskiy.meal_planner_api.web;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.lisovskiy.meal_planner_api.AbstractIT;
import dev.lisovskiy.meal_planner_api.common.IngredientUnit;
import dev.lisovskiy.meal_planner_api.dto.recipe.CreateRecipeDto;
import dev.lisovskiy.meal_planner_api.dto.recipe.RecipeDto;
import dev.lisovskiy.meal_planner_api.dto.recipe.RecipeListDto;
import dev.lisovskiy.meal_planner_api.dto.recipe.UpdateRecipeDto;
import dev.lisovskiy.meal_planner_api.dto.recipe_ingredient.CreateRecipeIngredientDto;
import dev.lisovskiy.meal_planner_api.repository.IngredientRepository;
import dev.lisovskiy.meal_planner_api.repository.RecipeIngredientRepository;
import dev.lisovskiy.meal_planner_api.repository.RecipeRepository;
import dev.lisovskiy.meal_planner_api.repository.entity.IngredientEntity;
import dev.lisovskiy.meal_planner_api.repository.entity.RecipeEntity;
import dev.lisovskiy.meal_planner_api.repository.entity.RecipeIngredientEntity;
import dev.lisovskiy.meal_planner_api.repository.entity.RecipeIngredientId;
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
import org.springframework.http.ProblemDetail;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
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

    @Test
    @SneakyThrows
    @DisplayName("CreateRecipe - Should Create and Return Empty Recipe")
    public void createRecipe_shouldCreateAndReturnEmptyRecipe() {
        // Arrange
        String title = "Recipe 1";
        String instructions = "Instructions of Recipe 1";
        int prepTimeMinutes = 1;

        CreateRecipeDto createRecipeDto = new CreateRecipeDto(
                title, instructions, prepTimeMinutes, null
        );

        // Act
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/recipes")
                        .contentType(APPLICATION_JSON_VALUE)
                        .accept(APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(createRecipeDto)));

        MvcResult mvcResult = resultActions.andReturn();

        // Assert
        resultActions.andExpect(status().isCreated());

        RecipeDto actualResult = GlobalExtractor.getObjectFromMvcResult(
                mvcResult, RecipeDto.class, objectMapper
        );

        RecipeDto expectedResult = new RecipeDto(
                actualResult.getId(), title, instructions, prepTimeMinutes, new ArrayList<>()
        );

        assertThat(actualResult)
                .isEqualTo(expectedResult);

        assertThat(recipeRepository.existsById(actualResult.getId()))
                .isTrue();

        // Document
        resultActions.andDo(document("create-recipe-empty",
                resource(
                        ResourceSnippetParameters.builder()
                                .tag(SCHEMA_TAG)
                                .summary("Create Recipe")
                                .description("Creates Recipe and returns it.")
                                .requestSchema(Schema.schema("CreateRecipeDto"))
                                .responseSchema(Schema.schema("RecipeDto"))
                                .requestFields(
                                        RecipeDtoSnippetProvider.getEmptyCreateRecipeDtoFields()
                                )
                                .responseFields(
                                        RecipeDtoSnippetProvider.getEmptyRecipeDtoFields()
                                )
                                .build()
                )
        ));
    }

    @Test
    @SneakyThrows
    @DisplayName("CreateRecipe - Should Create and Return Full Recipe")
    public void createRecipe_shouldCreateAndReturnFullRecipe() {
        // Arrange
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

        List<CreateRecipeIngredientDto> createRecipeIngredientDtoList = List.of(
                new CreateRecipeIngredientDto(
                        ingredientEntities.getFirst().getId(),
                        IngredientUnit.GRAMS.toString(),
                        45.0
                ),
                new CreateRecipeIngredientDto(
                        ingredientEntities.get(1).getId(),
                        IngredientUnit.QUANTITY.toString(),
                        4.0
                )
        );

        String title = "Recipe 1";
        String instructions = "Instructions of Recipe 1";
        int prepTimeMinutes = 1;

        CreateRecipeDto createRecipeDto = new CreateRecipeDto(
                title,
                instructions,
                prepTimeMinutes,
                createRecipeIngredientDtoList
        );

        // Act
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/recipes")
                        .contentType(APPLICATION_JSON_VALUE)
                        .accept(APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(createRecipeDto)));

        MvcResult mvcResult = resultActions.andReturn();

        // Assert
        resultActions.andExpect(status().isCreated());

        RecipeDto actualResult = GlobalExtractor.getObjectFromMvcResult(
                mvcResult, RecipeDto.class, objectMapper
        );

        assertThat(actualResult.getId())
                .isNotNull();
        assertThat(actualResult.getTitle())
                .isEqualTo(title);
        assertThat(actualResult.getInstructions())
                .isEqualTo(instructions);
        assertThat(actualResult.getPrepTimeMinutes())
                .isEqualTo(prepTimeMinutes);
        assertThat(actualResult.getRecipeIngredients())
                .isNotEmpty()
                .hasSize(createRecipeIngredientDtoList.size());

        assertThat(recipeRepository.existsById(actualResult.getId()))
                .isTrue();

        List<RecipeIngredientId> createdRecipeIngredientIds = actualResult.getRecipeIngredients().stream()
                .map(recipeIngredientDto -> new RecipeIngredientId(
                        actualResult.getId(),
                        recipeIngredientDto.getIngredient().getId()
                ))
                .toList();

        for (RecipeIngredientId createdRecipeIngredientId : createdRecipeIngredientIds) {
            assertThat(recipeIngredientRepository.existsById(createdRecipeIngredientId))
                    .isTrue();
        }

        // Document
        resultActions.andDo(document("create-recipe-full",
                resource(
                        ResourceSnippetParameters.builder()
                                .tag(SCHEMA_TAG)
                                .summary("Create Recipe")
                                .description("Creates Recipe and returns it.")
                                .requestSchema(Schema.schema("CreateRecipeDto"))
                                .responseSchema(Schema.schema("RecipeDto"))
                                .requestFields(
                                        RecipeDtoSnippetProvider.getCreateRecipeDtoFields()
                                )
                                .responseFields(
                                        RecipeDtoSnippetProvider.getRecipeDtoFields()
                                )
                                .build()
                )
        ));
    }

    @Test
    @SneakyThrows
    @DisplayName("CreateRecipe - Should Return 400 Bad Request")
    public void createRecipe_shouldReturn400BadRequest() {
        // Arrange
        String title = "R";
        String instructions = "instr";
        int prepTimeMinutes = 0;

        CreateRecipeDto createRecipeDto = new CreateRecipeDto(
                title, instructions, prepTimeMinutes, null
        );

        String expectedResultTitle = "Validation Error";
        String expectedPropertyExisting = "errors";

        // Act
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/recipes")
                        .contentType(APPLICATION_JSON_VALUE)
                        .accept(APPLICATION_PROBLEM_JSON)
                        .content(objectMapper.writeValueAsString(createRecipeDto)));

        MvcResult mvcResult = resultActions.andReturn();

        // Assert
        resultActions.andExpect(status().isBadRequest());

        ProblemDetail actualResult = GlobalExtractor.getObjectFromMvcResult(
                mvcResult, ProblemDetail.class, objectMapper
        );

        assertThat(actualResult.getTitle())
                .isEqualTo(expectedResultTitle);
        assertThat(actualResult.getProperties())
                .hasFieldOrProperty(expectedPropertyExisting);

        // Document
        resultActions.andDo(document("create-recipe-bad-request",
                resource(
                        ResourceSnippetParameters.builder()
                                .tag(SCHEMA_TAG)
                                .summary("Create Recipe")
                                .description("Creates Recipe and returns it.")
                                .requestSchema(Schema.schema("CreateRecipeDto"))
                                .responseSchema(Schema.schema("ProblemDetail"))
                                .build()
                )
        ));
    }

    @Test
    @SneakyThrows
    @DisplayName("CreateRecipe - Should Return 404 Not Found")
    public void createRecipe_shouldReturn404NotFound() {
        // Arrange
        List<CreateRecipeIngredientDto> createRecipeIngredientDtoList = List.of(
                new CreateRecipeIngredientDto(
                        1L,
                        IngredientUnit.GRAMS.toString(),
                        45.0
                )
        );

        String title = "Recipe 1";
        String instructions = "Instructions of Recipe 1";
        int prepTimeMinutes = 1;

        CreateRecipeDto createRecipeDto = new CreateRecipeDto(
                title,
                instructions,
                prepTimeMinutes,
                createRecipeIngredientDtoList
        );

        // Act
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/recipes")
                        .contentType(APPLICATION_JSON_VALUE)
                        .accept(APPLICATION_PROBLEM_JSON)
                        .content(objectMapper.writeValueAsString(createRecipeDto)));

        // Assert
        resultActions.andExpect(status().isNotFound());

        // Document
        resultActions.andDo(document("create-recipe-not-found",
                resource(
                        ResourceSnippetParameters.builder()
                                .tag(SCHEMA_TAG)
                                .summary("Create Recipe")
                                .description("Creates Recipe and returns it.")
                                .requestSchema(Schema.schema("CreateRecipeDto"))
                                .responseSchema(Schema.schema("ProblemDetail"))
                                .build()
                )
        ));
    }

    @Test
    @SneakyThrows
    @DisplayName("CreateRecipe - Should Return 409 Conflict")
    public void createRecipe_shouldReturn409Conflict() {
        // Arrange
        String sameTitle = "Recipe 1";

        RecipeEntity existingRecipeEntity = RecipeEntity.builder()
                .title(sameTitle)
                .instructions("Some instructions")
                .prepTimeMinutes(3)
                .build();
        recipeRepository.save(existingRecipeEntity);

        String instructions = "Instructions of Recipe 1";
        int prepTimeMinutes = 1;

        CreateRecipeDto createRecipeDto = new CreateRecipeDto(
                sameTitle,
                instructions,
                prepTimeMinutes,
                null
        );

        // Act
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/recipes")
                        .contentType(APPLICATION_JSON_VALUE)
                        .accept(APPLICATION_PROBLEM_JSON)
                        .content(objectMapper.writeValueAsString(createRecipeDto)));

        // Assert
        resultActions.andExpect(status().isConflict());

        assertThat(recipeRepository.count())
                .isEqualTo(1);

        // Document
        resultActions.andDo(document("create-recipe-conflict",
                resource(
                        ResourceSnippetParameters.builder()
                                .tag(SCHEMA_TAG)
                                .summary("Create Recipe")
                                .description("Creates Recipe and returns it.")
                                .requestSchema(Schema.schema("CreateRecipeDto"))
                                .responseSchema(Schema.schema("ProblemDetail"))
                                .build()
                )
        ));
    }

    @Test
    @SneakyThrows
    @DisplayName("UpdateRecipeById - Should Update and Return Recipe")
    public void updateRecipeById_shouldUpdateAndReturnRecipe() {
        // Arrange
        RecipeEntity recipeEntity = RecipeEntity.builder()
                .title("Recipe 1")
                .instructions("Instructions of Recipe 1")
                .prepTimeMinutes(3)
                .build();
        recipeEntity = recipeRepository.save(recipeEntity);

        Long recipeId = recipeEntity.getId();
        String updatedTitle = "Updated Recipe 1";
        String updatedInstructions = "Updated Instructions of Recipe 1";
        int updatedPrepTimeMinutes = 5;

        UpdateRecipeDto updateRecipeDto = new UpdateRecipeDto(
                updatedTitle,
                updatedInstructions,
                updatedPrepTimeMinutes
        );

        // Act
        ResultActions resultActions = mockMvc.perform(
                put("/api/v1/recipes/{id}", recipeId)
                        .contentType(APPLICATION_JSON_VALUE)
                        .accept(APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(updateRecipeDto)));

        MvcResult mvcResult = resultActions.andReturn();

        // Assert
        resultActions.andExpect(status().isOk());

        RecipeDto actualResult = GlobalExtractor.getObjectFromMvcResult(
                mvcResult, RecipeDto.class, objectMapper
        );

        RecipeDto expectedResult = new RecipeDto(
                actualResult.getId(),
                updatedTitle,
                updatedInstructions,
                updatedPrepTimeMinutes,
                new ArrayList<>()
        );

        assertThat(actualResult)
                .isEqualTo(expectedResult);

        RecipeEntity recipeInDb = recipeRepository.findById(recipeId).get();

        assertThat(recipeInDb.getTitle())
                .isEqualTo(updatedTitle);
        assertThat(recipeInDb.getInstructions())
            .isEqualTo(updatedInstructions);
        assertThat(recipeInDb.getPrepTimeMinutes())
                .isEqualTo(updatedPrepTimeMinutes);

        // Document
        resultActions.andDo(document("update-recipe-by-id",
                resource(
                        ResourceSnippetParameters.builder()
                                .tag(SCHEMA_TAG)
                                .summary("Update Recipe By Id")
                                .description("Updates Recipe if it exists and returns it.")
                                .pathParameters(
                                        parameterWithName("id")
                                                .description("Recipe Id")
                                )
                                .requestSchema(Schema.schema("UpdateRecipeDto"))
                                .responseSchema(Schema.schema("RecipeDto"))
                                .requestFields(
                                    RecipeDtoSnippetProvider.getUpdateRecipeDtoFields()
                                )
                                .responseFields(
                                        RecipeDtoSnippetProvider.getEmptyRecipeDtoFields()
                                )
                                .build()
                )
        ));
    }

    @Test
    @SneakyThrows
    @DisplayName("UpdateRecipeById - Should Return 400 Bad Request")
    public void updateRecipeById_shouldReturn400BadRequest() {
        // Arrange
        Long recipeId = 1L;
        String updatedTitle = "r";
        String updatedInstructions = "instr";
        int updatedPrepTimeMinutes = 0;

        UpdateRecipeDto updateRecipeDto = new UpdateRecipeDto(
                updatedTitle,
                updatedInstructions,
                updatedPrepTimeMinutes
        );

        String expectedResultTitle = "Validation Error";
        String expectedPropertyExisting = "errors";

        // Act
        ResultActions resultActions = mockMvc.perform(
                put("/api/v1/recipes/{id}", recipeId)
                        .contentType(APPLICATION_JSON_VALUE)
                        .accept(APPLICATION_PROBLEM_JSON)
                        .content(objectMapper.writeValueAsString(updateRecipeDto)));

        MvcResult mvcResult = resultActions.andReturn();

        // Assert
        resultActions.andExpect(status().isBadRequest());

        ProblemDetail actualResult = GlobalExtractor.getObjectFromMvcResult(
                mvcResult, ProblemDetail.class, objectMapper
        );

        assertThat(actualResult.getTitle())
                .isEqualTo(expectedResultTitle);
        assertThat(actualResult.getProperties())
                .hasFieldOrProperty(expectedPropertyExisting);

        // Document
        resultActions.andDo(document("update-recipe-by-id-bad-request",
                resource(
                        ResourceSnippetParameters.builder()
                                .tag(SCHEMA_TAG)
                                .summary("Update Recipe By Id")
                                .description("Updates Recipe if it exists and returns it.")
                                .pathParameters(
                                        parameterWithName("id")
                                                .description("Recipe Id")
                                )
                                .requestSchema(Schema.schema("UpdateRecipeDto"))
                                .responseSchema(Schema.schema("ProblemDetail"))
                                .build()
                )
        ));
    }

    @Test
    @SneakyThrows
    @DisplayName("UpdateRecipeById - Should Return 404 Not Found")
    public void updateRecipeById_shouldReturn404NotFound() {
        // Arrange
        Long recipeId = 1L;
        String updatedTitle = "Updated Recipe 1";
        String updatedInstructions = "Updated Instructions of Recipe 1";
        int updatedPrepTimeMinutes = 5;

        UpdateRecipeDto updateRecipeDto = new UpdateRecipeDto(
                updatedTitle,
                updatedInstructions,
                updatedPrepTimeMinutes
        );

        // Act
        ResultActions resultActions = mockMvc.perform(
                put("/api/v1/recipes/{id}", recipeId)
                        .contentType(APPLICATION_JSON_VALUE)
                        .accept(APPLICATION_PROBLEM_JSON)
                        .content(objectMapper.writeValueAsString(updateRecipeDto)));

        // Assert
        resultActions.andExpect(status().isNotFound());

        // Document
        resultActions.andDo(document("update-recipe-by-id-not-found",
                resource(
                        ResourceSnippetParameters.builder()
                                .tag(SCHEMA_TAG)
                                .summary("Update Recipe By Id")
                                .description("Updates Recipe if it exists and returns it.")
                                .pathParameters(
                                        parameterWithName("id")
                                                .description("Recipe Id")
                                )
                                .requestSchema(Schema.schema("UpdateRecipeDto"))
                                .responseSchema(Schema.schema("ProblemDetail"))
                                .build()
                )
        ));
    }

    @Test
    @SneakyThrows
    @DisplayName("UpdateRecipeById - Should Return 409 Conflict")
    public void updateRecipeById_shouldReturn409Conflict() {
        // Arrange
        String sameTitle = "Recipe 1";

        RecipeEntity anotherRecipeEntity = RecipeEntity.builder()
                .title(sameTitle)
                .instructions("Instructions of Recipe 1")
                .prepTimeMinutes(3)
                .build();
        recipeRepository.save(anotherRecipeEntity);

        RecipeEntity recipeEntity = RecipeEntity.builder()
                .title("Recipe 2")
                .instructions("Instructions of Recipe 2")
                .prepTimeMinutes(3)
                .build();
        recipeEntity = recipeRepository.save(recipeEntity);

        Long recipeId = recipeEntity.getId();
        String updatedInstructions = "Updated Instructions of Recipe 2";
        int updatedPrepTimeMinutes = 5;

        UpdateRecipeDto updateRecipeDto = new UpdateRecipeDto(
                sameTitle,
                updatedInstructions,
                updatedPrepTimeMinutes
        );

        // Act
        ResultActions resultActions = mockMvc.perform(
                put("/api/v1/recipes/{id}", recipeId)
                        .contentType(APPLICATION_JSON_VALUE)
                        .accept(APPLICATION_PROBLEM_JSON)
                        .content(objectMapper.writeValueAsString(updateRecipeDto)));

        // Assert
        resultActions.andExpect(status().isConflict());

        recipeEntity = recipeRepository.findById(recipeId).get();

        assertThat(recipeEntity.getTitle())
                .isNotEqualTo(sameTitle);
        assertThat(recipeEntity.getInstructions())
                .isNotEqualTo(updatedInstructions);
        assertThat(recipeEntity.getPrepTimeMinutes())
                .isNotEqualTo(updatedPrepTimeMinutes);

        // Document
        resultActions.andDo(document("update-recipe-by-id-conflict",
                resource(
                        ResourceSnippetParameters.builder()
                                .tag(SCHEMA_TAG)
                                .summary("Update Recipe By Id")
                                .description("Updates Recipe if it exists and returns it.")
                                .pathParameters(
                                        parameterWithName("id")
                                                .description("Recipe Id")
                                )
                                .requestSchema(Schema.schema("UpdateRecipeDto"))
                                .responseSchema(Schema.schema("ProblemDetail"))
                                .build()
                )
        ));
    }
}
