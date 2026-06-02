package dev.lisovskiy.meal_planner_api.web;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.epages.restdocs.apispec.SimpleType;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.lisovskiy.meal_planner_api.AbstractIT;
import dev.lisovskiy.meal_planner_api.common.IngredientUnit;
import dev.lisovskiy.meal_planner_api.dto.recipe_ingredient.CreateRecipeIngredientDto;
import dev.lisovskiy.meal_planner_api.dto.recipe_ingredient.CreateRecipeIngredientListDto;
import dev.lisovskiy.meal_planner_api.dto.recipe_ingredient.RecipeIngredientDto;
import dev.lisovskiy.meal_planner_api.dto.recipe_ingredient.RecipeIngredientListDto;
import dev.lisovskiy.meal_planner_api.repository.IngredientRepository;
import dev.lisovskiy.meal_planner_api.repository.RecipeIngredientRepository;
import dev.lisovskiy.meal_planner_api.repository.RecipeRepository;
import dev.lisovskiy.meal_planner_api.repository.entity.IngredientEntity;
import dev.lisovskiy.meal_planner_api.repository.entity.RecipeEntity;
import dev.lisovskiy.meal_planner_api.repository.entity.RecipeIngredientEntity;
import dev.lisovskiy.meal_planner_api.repository.entity.RecipeIngredientId;
import dev.lisovskiy.meal_planner_api.service.mapper.RecipeIngredientEntityMapper;
import dev.lisovskiy.meal_planner_api.util.GlobalExtractor;
import dev.lisovskiy.meal_planner_api.util.RecipeIngredientDtoSnippetProvider;
import dev.lisovskiy.meal_planner_api.web.mapper.RecipeIngredientWebMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ProblemDetail;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.stream.Stream;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("RecipeIngredientController IT")
public class RecipeIngredientControllerIT extends AbstractIT {

    private final String SCHEMA_TAG = "RecipeIngredients";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RecipeIngredientRepository recipeIngredientRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private RecipeIngredientEntityMapper recipeIngredientEntityMapper;

    @Autowired
    private RecipeIngredientWebMapper recipeIngredientWebMapper;

    @AfterEach
    public void cleanUp() {
        recipeIngredientRepository.deleteAll();
        recipeRepository.deleteAll();
        ingredientRepository.deleteAll();
    }

    @Test
    @SneakyThrows
    @DisplayName("GetAllRecipeIngredients - Should Return List")
    public void getAllRecipeIngredients_shouldReturnList() {
        // Arrange
        List<IngredientEntity> ingredientEntities = List.of(
                IngredientEntity.builder()
                        .name("Ingredient 1")
                        .description("Description of Ingredient 1")
                        .build(),
                IngredientEntity.builder()
                        .name("Ingredient 2")
                        .description("Description of Ingredient 2")
                        .build(),
                IngredientEntity.builder()
                        .name("Ingredient 3")
                        .description("Description of Ingredient 3")
                        .build()
        );
        ingredientEntities = ingredientRepository.saveAll(ingredientEntities);

        RecipeEntity recipeEntity = RecipeEntity.builder()
                .title("Recipe Title")
                .instructions("Recipe Instructions")
                .prepTimeMinutes(1)
                .build();
        recipeEntity = recipeRepository.save(recipeEntity);

        List<RecipeIngredientEntity> recipeIngredientEntities = List.of(
                RecipeIngredientEntity.builder()
                        .recipe(recipeEntity)
                        .ingredient(ingredientEntities.get(0))
                        .unit(IngredientUnit.GRAMS)
                        .quantity(50.0)
                        .build(),
                RecipeIngredientEntity.builder()
                        .recipe(recipeEntity)
                        .ingredient(ingredientEntities.get(1))
                        .unit(IngredientUnit.GRAMS)
                        .quantity(100.0)
                        .build(),
                RecipeIngredientEntity.builder()
                        .recipe(recipeEntity)
                        .ingredient(ingredientEntities.get(2))
                        .unit(IngredientUnit.GRAMS)
                        .quantity(70.0)
                        .build()
        );
        recipeIngredientEntities = recipeIngredientRepository.saveAll(recipeIngredientEntities);

        // Act
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/recipes/{recipeId}/ingredients", recipeEntity.getId())
                        .accept(APPLICATION_JSON_VALUE));

        MvcResult mvcResult = resultActions.andReturn();

        // Assert
        resultActions.andExpect(status().isOk());

        RecipeIngredientListDto actualResult = GlobalExtractor.getObjectFromMvcResult(
                mvcResult, RecipeIngredientListDto.class, objectMapper
        );

        assertThat(actualResult)
                .isNotNull()
                .hasNoNullFieldsOrProperties();

        assertThat(actualResult.getRecipeIngredients())
                .isNotEmpty()
                .hasSize(recipeIngredientEntities.size());

        // Document
        Stream<FieldDescriptor> rootField = Stream.of(
                fieldWithPath("recipeIngredients")
                        .type(JsonFieldType.ARRAY)
                        .description("List of ingredients of recipe.")
        );

        Stream<FieldDescriptor> nestedRecipeIngredientListFields = RecipeIngredientDtoSnippetProvider
                .getRecipeIngredientDtoFieldsWithPrefix("recipeIngredients[]").stream();

        resultActions.andDo(document("get-all-recipe-ingredients",
                resource(
                        ResourceSnippetParameters.builder()
                                .tag(SCHEMA_TAG)
                                .summary("Get all recipe ingredients")
                                .description("Get all recipe ingredients by recipe ID.")
                                .pathParameters(
                                        parameterWithName("recipeId")
                                                .type(SimpleType.NUMBER)
                                                .description("Identifier of the target recipe.")
                                )
                                .responseSchema(Schema.schema("RecipeIngredientListDto"))
                                .responseFields(
                                        Stream.concat(rootField, nestedRecipeIngredientListFields)
                                                .toList()
                                )
                                .build()
                )
        ));
    }

    @Test
    @SneakyThrows
    @DisplayName("GetAllRecipeIngredients - Should Return 404 Not Found")
    public void getAllRecipeIngredients_shouldReturn404NotFound() {
        // Arrange
        Long recipeId = 1L;

        // Act
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/recipes/{recipeId}/ingredients", recipeId)
                        .accept(APPLICATION_PROBLEM_JSON));

        // Assert
        resultActions.andExpect(status().isNotFound());

        assertThat(recipeRepository.existsById(recipeId))
                .isFalse();

        // Document
        resultActions.andDo(document("get-all-recipe-ingredients-not-found",
                resource(
                        ResourceSnippetParameters.builder()
                                .tag(SCHEMA_TAG)
                                .summary("Get all recipe ingredients")
                                .description("Get all recipe ingredients by recipe ID.")
                                .pathParameters(
                                        parameterWithName("recipeId")
                                                .type(SimpleType.NUMBER)
                                                .description("Identifier of the target recipe.")
                                )
                                .responseSchema(Schema.schema("ProblemDetail"))
                                .build()
                )
        ));
    }

    @Test
    @SneakyThrows
    @DisplayName("GetRecipeIngredientById - Should Return RecipeIngredient")
    public void getRecipeIngredientById_shouldReturnRecipeIngredient() {
        // Arrange
        IngredientEntity ingredientEntity = IngredientEntity.builder()
                .name("Ingredient 1")
                .description("Description of Ingredient 1")
                .build();
        ingredientEntity = ingredientRepository.save(ingredientEntity);

        RecipeEntity recipeEntity = RecipeEntity.builder()
                .title("Recipe 1")
                .instructions("Instructions of Recipe 1")
                .prepTimeMinutes(1)
                .build();
        recipeEntity = recipeRepository.save(recipeEntity);

        RecipeIngredientEntity recipeIngredientEntity = RecipeIngredientEntity.builder()
                .recipe(recipeEntity)
                .ingredient(ingredientEntity)
                .unit(IngredientUnit.GRAMS)
                .quantity(80.0)
                .build();
        recipeIngredientEntity = recipeIngredientRepository.save(recipeIngredientEntity);

        RecipeIngredientDto expectedResult = recipeIngredientWebMapper.toRecipeIngredientDto(
                recipeIngredientEntityMapper.toRecipeIngredient(recipeIngredientEntity)
        );

        // Act
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/recipes/{recipeId}/ingredients/{ingredientId}",
                        recipeEntity.getId(), ingredientEntity.getId())
                        .accept(APPLICATION_JSON_VALUE));

        MvcResult mvcResult = resultActions.andReturn();

        // Assert
        resultActions.andExpect(status().isOk());

        RecipeIngredientDto actualResult = GlobalExtractor.getObjectFromMvcResult(
                mvcResult, RecipeIngredientDto.class, objectMapper
        );

        assertThat(actualResult)
                .isNotNull()
                .isEqualTo(expectedResult);

        // Document
        resultActions.andDo(document("get-recipe-ingredient-by-id",
                resource(
                        ResourceSnippetParameters.builder()
                                .tag(SCHEMA_TAG)
                                .summary("Get recipe ingredient")
                                .description("Gets recipe ingredient by its ID, that is composed of both, recipe and ingredient ids.")
                                .pathParameters(
                                        parameterWithName("recipeId")
                                                .type(SimpleType.NUMBER)
                                                .description("Identifier of the target recipe."),
                                        parameterWithName("ingredientId")
                                                .type(SimpleType.NUMBER)
                                                .description("Identifier of the target ingredient.")
                                )
                                .responseSchema(Schema.schema("RecipeIngredientDto"))
                                .responseFields(
                                        RecipeIngredientDtoSnippetProvider.getRecipeIngredientDtoFields()
                                )
                                .build()
                )
        ));
    }

    @Test
    @SneakyThrows
    @DisplayName("GetRecipeIngredientById - Should Return 404 Not Found")
    public void getRecipeIngredientById_shouldReturn404NotFound() {
        // Arrange
        IngredientEntity ingredientEntity = IngredientEntity.builder()
                .name("Ingredient 1")
                .description("Description of Ingredient 1")
                .build();
        ingredientEntity = ingredientRepository.save(ingredientEntity);

        RecipeEntity recipeEntity = RecipeEntity.builder()
                .title("Recipe 1")
                .instructions("Instructions of Recipe 1")
                .prepTimeMinutes(1)
                .build();
        recipeEntity = recipeRepository.save(recipeEntity);

        Long recipeId = recipeEntity.getId();
        Long ingredientId = ingredientEntity.getId();

        // Act
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/recipes/{recipeId}/ingredients/{ingredientId}",
                        recipeId, ingredientId)
                        .accept(APPLICATION_PROBLEM_JSON));

        MvcResult mvcResult = resultActions.andReturn();

        // Assert
        resultActions.andExpect(status().isNotFound());

        ProblemDetail actualResult = GlobalExtractor.getObjectFromMvcResult(
                mvcResult, ProblemDetail.class, objectMapper
        );

        assertThat(actualResult.getDetail())
                .matches(".*Ingredient.*not found.*in recipe.*");

        RecipeIngredientId recipeIngredientId = new RecipeIngredientId(recipeId, ingredientId);
        assertThat(recipeIngredientRepository.existsById(recipeIngredientId))
                .isFalse();

        // Document
        resultActions.andDo(document("get-recipe-ingredient-by-id-not-found",
                resource(
                        ResourceSnippetParameters.builder()
                                .tag(SCHEMA_TAG)
                                .summary("Get recipe ingredient")
                                .description("Gets recipe ingredient by its ID, that is composed of both, recipe and ingredient ids.")
                                .pathParameters(
                                        parameterWithName("recipeId")
                                                .type(SimpleType.NUMBER)
                                                .description("Identifier of the target recipe."),
                                        parameterWithName("ingredientId")
                                                .type(SimpleType.NUMBER)
                                                .description("Identifier of the target ingredient.")
                                )
                                .responseSchema(Schema.schema("ProblemDetail"))
                                .build()
                )
        ));
    }

    @Test
    @SneakyThrows
    @DisplayName("GetRecipeIngredientById - Should Return 404 Not Found on Recipe")
    public void getRecipeIngredientById_shouldReturn404NotFoundOnRecipe() {
        // Arrange
        IngredientEntity ingredientEntity = IngredientEntity.builder()
                .name("Ingredient 1")
                .description("Description of Ingredient 1")
                .build();
        ingredientEntity = ingredientRepository.save(ingredientEntity);

        Long recipeId = 1L;
        Long ingredientId = ingredientEntity.getId();

        // Act
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/recipes/{recipeId}/ingredients/{ingredientId}",
                        recipeId, ingredientId)
                        .accept(APPLICATION_PROBLEM_JSON));

        MvcResult mvcResult = resultActions.andReturn();

        // Assert
        resultActions.andExpect(status().isNotFound());

        ProblemDetail actualResult = GlobalExtractor.getObjectFromMvcResult(
                mvcResult, ProblemDetail.class, objectMapper
        );

        assertThat(actualResult.getDetail())
                .matches(".*Recipe.*not found.*");

        assertThat(recipeRepository.existsById(recipeId))
                .isFalse();

        // Document
        resultActions.andDo(document("get-recipe-ingredient-by-id-not-found-on-recipe",
                resource(
                        ResourceSnippetParameters.builder()
                                .tag(SCHEMA_TAG)
                                .summary("Get recipe ingredient")
                                .description("Gets recipe ingredient by its ID, that is composed of both, recipe and ingredient ids.")
                                .pathParameters(
                                        parameterWithName("recipeId")
                                                .type(SimpleType.NUMBER)
                                                .description("Identifier of the target recipe."),
                                        parameterWithName("ingredientId")
                                                .type(SimpleType.NUMBER)
                                                .description("Identifier of the target ingredient.")
                                )
                                .responseSchema(Schema.schema("ProblemDetail"))
                                .build()
                )
        ));
    }

    @Test
    @SneakyThrows
    @DisplayName("GetRecipeIngredientById - Should Return 404 Not Found on Ingredient")
    public void getRecipeIngredientById_shouldReturn404NotFoundOnIngredient() {
        // Arrange
        RecipeEntity recipeEntity = RecipeEntity.builder()
                .title("Recipe 1")
                .instructions("Instructions of Recipe 1")
                .prepTimeMinutes(1)
                .build();
        recipeEntity = recipeRepository.save(recipeEntity);

        Long recipeId = recipeEntity.getId();
        Long ingredientId = 1L;

        // Act
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/recipes/{recipeId}/ingredients/{ingredientId}",
                        recipeId, ingredientId)
                        .accept(APPLICATION_PROBLEM_JSON));

        MvcResult mvcResult = resultActions.andReturn();

        // Assert
        resultActions.andExpect(status().isNotFound());

        ProblemDetail actualResult = GlobalExtractor.getObjectFromMvcResult(
                mvcResult, ProblemDetail.class, objectMapper
        );

        assertThat(actualResult.getDetail())
                .matches(".*Ingredient.*not found.*");

        assertThat(ingredientRepository.existsById(ingredientId))
                .isFalse();

        // Document
        resultActions.andDo(document("get-recipe-ingredient-by-id-not-found-on-ingredient",
                resource(
                        ResourceSnippetParameters.builder()
                                .tag(SCHEMA_TAG)
                                .summary("Get recipe ingredient")
                                .description("Gets recipe ingredient by its ID, that is composed of both, recipe and ingredient ids.")
                                .pathParameters(
                                        parameterWithName("recipeId")
                                                .type(SimpleType.NUMBER)
                                                .description("Identifier of the target recipe."),
                                        parameterWithName("ingredientId")
                                                .type(SimpleType.NUMBER)
                                                .description("Identifier of the target ingredient.")
                                )
                                .responseSchema(Schema.schema("ProblemDetail"))
                                .build()
                )
        ));
    }

    @Test
    @SneakyThrows
    @DisplayName("CreateRecipeIngredients - Should Create and Return RecipeIngredients")
    public void createRecipeIngredients_shouldCreateAndReturnRecipeIngredients() {
        // Arrange
        RecipeEntity recipeEntity = RecipeEntity.builder()
                .title("Recipe 1")
                .instructions("Instructions of Recipe 1")
                .prepTimeMinutes(1)
                .build();
        recipeEntity = recipeRepository.save(recipeEntity);

        IngredientEntity ingredientEntity = IngredientEntity.builder()
                .name("Ingredient 1")
                .description("Description of Ingredient 1")
                .build();
        ingredientEntity = ingredientRepository.save(ingredientEntity);

        Long recipeId = recipeEntity.getId();
        Long ingredientId = ingredientEntity.getId();

        IngredientUnit unit = IngredientUnit.GRAMS;
        Double quantity = 43.0;

        CreateRecipeIngredientListDto createRecipeIngredientListDto = new CreateRecipeIngredientListDto(List.of(
                        new CreateRecipeIngredientDto(ingredientId, unit.toString(), quantity)
        ));

        // Act
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/recipes/{recipeId}/ingredients", recipeId)
                        .contentType(APPLICATION_JSON_VALUE)
                        .accept(APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(createRecipeIngredientListDto)));

        MvcResult mvcResult = resultActions.andReturn();

        // Assert
        resultActions.andExpect(status().isCreated());

        RecipeIngredientListDto actualResult = GlobalExtractor.getObjectFromMvcResult(
                mvcResult, RecipeIngredientListDto.class, objectMapper
        );

        assertThat(actualResult)
                .isNotNull();

        assertThat(actualResult.getRecipeIngredients())
                .isNotEmpty()
                .hasSize(1);

        RecipeIngredientDto returnedRecipeIngredientDto = actualResult.getRecipeIngredients().getFirst();

        assertThat(returnedRecipeIngredientDto.getIngredient().getId())
                .isEqualTo(ingredientId);
        assertThat(returnedRecipeIngredientDto.getUnit())
                .isEqualTo(unit);
        assertThat(returnedRecipeIngredientDto.getQuantity())
                .isEqualTo(quantity);

        RecipeIngredientId recipeIngredientId = new RecipeIngredientId(recipeId, ingredientId);
        assertThat(recipeIngredientRepository.existsById(recipeIngredientId))
                .isTrue();

        // Document
        resultActions.andDo(document("create-recipe-ingredient",
                resource(
                        ResourceSnippetParameters.builder()
                                .tag(SCHEMA_TAG)
                                .summary("Create recipe ingredient")
                                .description("Creates a relation between targeting Recipe and Ingredients.")
                                .pathParameters(
                                        parameterWithName("recipeId")
                                                .type(SimpleType.NUMBER)
                                                .description("Identifier of the target recipe.")
                                )
                                .requestSchema(Schema.schema("CreateRecipeIngredientListDto"))
                                .responseSchema(Schema.schema("RecipeIngredientListDto"))
                                .requestFields(
                                        RecipeIngredientDtoSnippetProvider.getCreateRecipeIngredientListDtoFields()
                                )
                                .build()
                )
        ));
    }

    @Test
    @SneakyThrows
    @DisplayName("CreateRecipeIngredients - Should Return 400 Bad Request")
    public void createRecipeIngredients_shouldReturn400BadRequest() {
        // Arrange
        String expectedResultTitle = "Validation Error";
        String expectedPropertyExisting = "errors";

        CreateRecipeIngredientListDto createRecipeIngredientListDto = new CreateRecipeIngredientListDto(List.of(
                        new CreateRecipeIngredientDto(-1L, "Not Valid Unit", 0.0)
        ));

        // Act
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/recipes/{recipeId}/ingredients", 0L)
                        .contentType(APPLICATION_JSON_VALUE)
                        .accept(APPLICATION_PROBLEM_JSON)
                        .content(objectMapper.writeValueAsString(createRecipeIngredientListDto)));

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
        resultActions.andDo(document("create-recipe-ingredient-bad-request",
                resource(
                        ResourceSnippetParameters.builder()
                                .tag(SCHEMA_TAG)
                                .summary("Create recipe ingredient")
                                .description("Creates a relation between targeting Recipe and Ingredients.")
                                .pathParameters(
                                        parameterWithName("recipeId")
                                                .type(SimpleType.NUMBER)
                                                .description("Identifier of the target recipe.")
                                )
                                .requestSchema(Schema.schema("CreateRecipeIngredientListDto"))
                                .responseSchema(Schema.schema("ProblemDetail"))
                                .build()
                )
        ));
    }

    @Test
    @SneakyThrows
    @DisplayName("CreateRecipeIngredients - Should Return 404 Not Found")
    public void createRecipeIngredients_shouldReturn404NotFound() {
        // Arrange
        CreateRecipeIngredientListDto createRecipeIngredientListDto = new CreateRecipeIngredientListDto(List.of(
                new CreateRecipeIngredientDto(1L, IngredientUnit.GRAMS.toString(), 32.0)
        ));

        // Act
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/recipes/{recipeId}/ingredients", 1L)
                        .contentType(APPLICATION_JSON_VALUE)
                        .accept(APPLICATION_PROBLEM_JSON)
                        .content(objectMapper.writeValueAsString(createRecipeIngredientListDto)));

        // Assert
        resultActions.andExpect(status().isNotFound());

        // Document
        resultActions.andDo(document("create-recipe-ingredient-not-found",
                resource(
                        ResourceSnippetParameters.builder()
                                .tag(SCHEMA_TAG)
                                .summary("Create recipe ingredient")
                                .description("Creates a relation between targeting Recipe and Ingredients.")
                                .pathParameters(
                                        parameterWithName("recipeId")
                                                .type(SimpleType.NUMBER)
                                                .description("Identifier of the target recipe.")
                                )
                                .requestSchema(Schema.schema("CreateRecipeIngredientListDto"))
                                .responseSchema(Schema.schema("ProblemDetail"))
                                .build()
                )
        ));
    }
}
