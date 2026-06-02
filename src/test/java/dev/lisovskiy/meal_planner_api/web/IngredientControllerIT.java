package dev.lisovskiy.meal_planner_api.web;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.epages.restdocs.apispec.SimpleType;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.lisovskiy.meal_planner_api.AbstractIT;
import dev.lisovskiy.meal_planner_api.dto.ingredient.UpdateIngredientDto;
import dev.lisovskiy.meal_planner_api.service.mapper.IngredientEntityMapper;
import dev.lisovskiy.meal_planner_api.util.GlobalDtoSnippetsProvider;
import dev.lisovskiy.meal_planner_api.util.GlobalExtractor;
import dev.lisovskiy.meal_planner_api.util.IngredientDtoSnippetsProvider;
import dev.lisovskiy.meal_planner_api.dto.ingredient.CreateIngredientDto;
import dev.lisovskiy.meal_planner_api.dto.ingredient.IngredientDto;
import dev.lisovskiy.meal_planner_api.dto.ingredient.IngredientListDto;
import dev.lisovskiy.meal_planner_api.repository.IngredientRepository;
import dev.lisovskiy.meal_planner_api.repository.entity.IngredientEntity;
import dev.lisovskiy.meal_planner_api.web.mapper.IngredientWebMapper;
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

@DisplayName("IngredientController IT")
public class IngredientControllerIT extends AbstractIT {

    private final String SCHEMA_TAG = "Ingredients";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private IngredientEntityMapper ingredientEntityMapper;

    @Autowired
    private IngredientWebMapper ingredientWebMapper;

    @AfterEach
    public void cleanUp() {
        ingredientRepository.deleteAll();
    }

    @Test
    @SneakyThrows
    @DisplayName("Get All Ingredients - Should Return a List")
    public void getAllIngredients_shouldReturnList() {
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

        ingredientRepository.saveAll(ingredientEntities);

        // Act
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/ingredients")
                        .accept(APPLICATION_JSON_VALUE));

        MvcResult mvcResult = resultActions.andReturn();

        // Assert
        resultActions.andExpect(status().isOk());

        IngredientListDto actualResult = GlobalExtractor.getObjectFromMvcResult(
                mvcResult, IngredientListDto.class, objectMapper
        );

        assertThat(actualResult)
                .isNotNull()
                .hasFieldOrProperty("ingredients");

        assertThat(actualResult.getIngredients())
                .isNotEmpty()
                .hasSize(ingredientEntities.size());

        // Documentation
        FieldDescriptor ingredientsField = fieldWithPath("ingredients")
                .type(JsonFieldType.ARRAY)
                .description("List of ingredients");

        List<FieldDescriptor> ingredientDtoListFields = Stream.concat(
                Stream.of(ingredientsField),
                IngredientDtoSnippetsProvider.getIngredientDtoFieldsWithPrefix("ingredients[]").stream()
        ).toList();

        resultActions.andDo(document("get-all-ingredients-ok",
                resource(
                        ResourceSnippetParameters.builder()
                                .tag(SCHEMA_TAG)
                                .summary("Get all ingredients")
                                .description("Gets all existing ingredients.")
                                .responseSchema(Schema.schema("IngredientListDto"))
                                .responseFields(
                                        ingredientDtoListFields
                                )
                                .build()
                )
        ));
    }

    @Test
    @SneakyThrows
    @DisplayName("Get Ingredient By Id - Should Return Ingredient")
    public void getIngredientById_shouldReturnIngredient() {
        // Arrange
        String name = "Ingredient 1";
        String description = "Description of Ingredient 1";

        IngredientEntity ingredientEntity = IngredientEntity.builder()
                .name(name)
                .description(description)
                .build();

        ingredientEntity = ingredientRepository.save(ingredientEntity);

        Long id = ingredientEntity.getId();

        IngredientDto expectedResult = new IngredientDto(
                ingredientEntity.getId(),
                name,
                description
        );

        // Act
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/ingredients/{id}", id)
                        .accept(APPLICATION_JSON_VALUE));

        MvcResult mvcResult = resultActions.andReturn();

        // Assert
        resultActions.andExpect(status().isOk());

        IngredientDto actualResult = GlobalExtractor.getObjectFromMvcResult(
                mvcResult, IngredientDto.class, objectMapper
        );

        assertThat(actualResult)
                .isNotNull()
                .isEqualTo(expectedResult);

        // Documentation
        resultActions.andDo(document("get-ingredient-by-id-ok",
                resource(
                        ResourceSnippetParameters.builder()
                                .tag(SCHEMA_TAG)
                                .summary("Get ingredient by ID")
                                .description("Gets an ingredient by its ID if exists.")
                                .pathParameters(
                                        parameterWithName("id")
                                                .type(SimpleType.NUMBER)
                                                .description("Identifier of the ingredient.")
                                )
                                .responseSchema(Schema.schema("IngredientDto"))
                                .responseFields(
                                        IngredientDtoSnippetsProvider.getIngredientDtoFields()
                                )
                                .build()
                )
        ));
    }

    @Test
    @SneakyThrows
    @DisplayName("Get Ingredient By Id - Should Return 404 Not Found")
    public void getIngredientById_shouldReturn404NotFound() {
        // Arrange
        Long id = 1L;

        // Act
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/ingredients/{id}", id)
                        .accept(APPLICATION_PROBLEM_JSON));

        // Assert
        resultActions.andExpect(status().isNotFound());

        assertThat(ingredientRepository.existsById(id))
                .isFalse();

        // Documentation
        resultActions.andDo(document("get-ingredient-by-id-not-found",
                resource(
                        ResourceSnippetParameters.builder()
                                .tag(SCHEMA_TAG)
                                .summary("Get ingredient by ID")
                                .description("Gets an ingredient by its ID if exists.")
                                .responseSchema(Schema.schema("ProblemDetail"))
                                .responseFields(
                                        GlobalDtoSnippetsProvider.getProblemDetailFields()
                                )
                                .build()
                )
        ));
    }

    @Test
    @SneakyThrows
    @DisplayName("Create Ingredient - Should Save And Return Ingredient")
    public void createIngredient_shouldSaveAndReturnIngredient() {
        // Arrange
        String name = "Ingredient 1";
        String description = "Description of ingredient 1";

        CreateIngredientDto createIngredientDto = new CreateIngredientDto(
                name,
                description
        );

        // Act
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/ingredients")
                        .contentType(APPLICATION_JSON_VALUE)
                        .accept(APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(createIngredientDto)));

        MvcResult mvcResult = resultActions.andReturn();

        // Assert
        resultActions.andExpect(status().isCreated());

        IngredientDto actualResult = GlobalExtractor.getObjectFromMvcResult(
                mvcResult, IngredientDto.class, objectMapper
        );

        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isNotNull();

        IngredientDto expectedResult = new IngredientDto(
                actualResult.getId(),
                name,
                description
        );

        assertThat(actualResult)
                .isEqualTo(expectedResult);

        assertThat(ingredientRepository.existsById(actualResult.getId()))
            .isTrue();

        // Documentation
        resultActions.andDo(document("create-ingredient-created",
                resource(
                        ResourceSnippetParameters.builder()
                                .tag(SCHEMA_TAG)
                                .summary("Create a new ingredient")
                                .description("Saves a new ingredient to the database and returns the created entity.")
                                .requestSchema(Schema.schema("CreateIngredientDto"))
                                .responseSchema(Schema.schema("IngredientDto"))
                                .requestFields(
                                        IngredientDtoSnippetsProvider.getCreateIngredientDtoFields()
                                )
                                .responseFields(
                                    IngredientDtoSnippetsProvider.getIngredientDtoFields()
                                )
                                .build()
                )
        ));
    }

    @Test
    @SneakyThrows
    @DisplayName("Create Ingredient - Should Return 400 Bad Request")
    public void createIngredient_shouldReturn400BadRequest() {
        // Arrange
        String name = "n".repeat(101);
        String description = "s".repeat(1001);

        CreateIngredientDto createIngredientDto = new CreateIngredientDto(
                name,
                description
        );

        String expectedResultTitle = "Validation Error";
        String expectedPropertyExisting = "errors";

        // Act
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/ingredients")
                        .contentType(APPLICATION_JSON_VALUE)
                        .accept(APPLICATION_PROBLEM_JSON)
                        .content(objectMapper.writeValueAsString(createIngredientDto)));

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

        // Documentation
        resultActions.andDo(document("create-ingredient-bad-request",
                resource(
                        ResourceSnippetParameters.builder()
                                .tag(SCHEMA_TAG)
                                .summary("Update ingredient by ID")
                                .description("Updates and returns an ingredient if exists.")
                                .requestSchema(Schema.schema("CreateIngredientDto"))
                                .responseSchema(Schema.schema("ProblemDetail"))
                                .responseFields(
                                        GlobalDtoSnippetsProvider.getProblemDetailFieldsWithErrorsProperty()
                                )
                                .build()
                )
        ));
    }

    @Test
    @SneakyThrows
    @DisplayName("Create Ingredient - Should Return 409 Conflict")
    public void createIngredient_shouldReturn409Conflict() {
        // Arrange
        String name = "Ingredient 1";

        IngredientEntity alreadyExistingIngredient = IngredientEntity.builder()
                .name(name)
                .description("Some description")
                .build();

        ingredientRepository.save(alreadyExistingIngredient);

        CreateIngredientDto createIngredientDto = new CreateIngredientDto(
                name,
                "Some description"
        );

        // Act
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/ingredients")
                        .contentType(APPLICATION_JSON_VALUE)
                        .accept(APPLICATION_PROBLEM_JSON)
                        .content(objectMapper.writeValueAsString(createIngredientDto)));

        // Assert
        resultActions.andExpect(status().isConflict());

        assertThat(ingredientRepository.count())
                .isEqualTo(1);

        // Documentation
        resultActions.andDo(document("create-ingredient-conflict",
                resource(
                        ResourceSnippetParameters.builder()
                                .tag(SCHEMA_TAG)
                                .summary("Create a new ingredient")
                                .description("Returns 409 Conflict if an ingredient with the same name already exists.")
                                .requestSchema(Schema.schema("CreateIngredientDto"))
                                .responseSchema(Schema.schema("ProblemDetail"))
                                .responseFields(
                                        GlobalDtoSnippetsProvider.getProblemDetailFields()
                                )
                                .build()
                )
        ));
    }

    @Test
    @SneakyThrows
    @DisplayName("Update Ingredient By Id - Should Update and Return")
    public void updateIngredient_shouldUpdateAndReturn() {
        // Arrange
        IngredientEntity ingredientEntity = IngredientEntity.builder()
                .name("Ingredient 1")
                .description("Description of ingredient 1")
                .build();

        ingredientEntity = ingredientRepository.save(ingredientEntity);

        Long id = ingredientEntity.getId();
        String updatedName = "Updated Ingredient 1";
        String updatedDescription = "Updated Description of ingredient 1";

        UpdateIngredientDto updateIngredientDto = new UpdateIngredientDto(
                updatedName,
                updatedDescription
        );

        IngredientDto expectedResult = new IngredientDto(
                id,
                updatedName,
                updatedDescription
        );

        // Act
        ResultActions resultActions = mockMvc.perform(
                put("/api/v1/ingredients/{id}", id)
                        .contentType(APPLICATION_JSON_VALUE)
                        .accept(APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(updateIngredientDto)));

        MvcResult mvcResult = resultActions.andReturn();

        // Assert
        resultActions.andExpect(status().isOk());

        IngredientDto actualResult = GlobalExtractor.getObjectFromMvcResult(
                mvcResult, IngredientDto.class, objectMapper
        );

        assertThat(actualResult)
                .isNotNull()
                .isEqualTo(expectedResult);

        ingredientEntity = ingredientRepository.findById(id).get();
        IngredientDto ingredientInDatabase = ingredientWebMapper.toIngredientDto(
                ingredientEntityMapper.toIngredient(ingredientEntity)
        );

        assertThat(actualResult)
                .isEqualTo(ingredientInDatabase);

        // Documentation
        resultActions.andDo(document("update-ingredient-by-id-ok",
                resource(
                        ResourceSnippetParameters.builder()
                                .tag(SCHEMA_TAG)
                                .summary("Update ingredient by ID")
                                .description("Updates and returns an ingredient if exists.")
                                .pathParameters(
                                        parameterWithName("id")
                                                .type(SimpleType.NUMBER)
                                                .description("Identifier of the ingredient to be updated.")
                                )
                                .requestSchema(Schema.schema("UpdateIngredientDto"))
                                .responseSchema(Schema.schema("IngredientDto"))
                                .requestFields(
                                        IngredientDtoSnippetsProvider.getUpdateIngredientDtoFields()
                                )
                                .responseFields(
                                        IngredientDtoSnippetsProvider.getIngredientDtoFields()
                                )
                                .build()
                )
        ));
    }

    @Test
    @SneakyThrows
    @DisplayName("Update Ingredient By Id - Should Return 400 Bad Request")
    public void updateIngredient_shouldReturn400BadRequest() {
        // Arrange
        Long id = 1L;

        UpdateIngredientDto updateIngredientDto = new UpdateIngredientDto(
                "i",
                "desc"
        );

        String expectedResultTitle = "Validation Error";
        String expectedPropertyExisting = "errors";

        // Act
        ResultActions resultActions = mockMvc.perform(
                put("/api/v1/ingredients/{id}", id)
                        .contentType(APPLICATION_JSON_VALUE)
                        .accept(APPLICATION_PROBLEM_JSON)
                        .content(objectMapper.writeValueAsString(updateIngredientDto)));

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

        // Documentation
        resultActions.andDo(document("update-ingredient-by-id-bad-request",
                resource(
                        ResourceSnippetParameters.builder()
                                .tag(SCHEMA_TAG)
                                .summary("Update ingredient by ID")
                                .description("Updates and returns an ingredient if exists.")
                                .requestSchema(Schema.schema("UpdateIngredientDto"))
                                .responseSchema(Schema.schema("ProblemDetail"))
                                .responseFields(
                                        GlobalDtoSnippetsProvider.getProblemDetailFieldsWithErrorsProperty()
                                )
                                .build()
                )
        ));
    }

    @Test
    @SneakyThrows
    @DisplayName("Update Ingredient By Id - Should Return 404 Not Found")
    public void updateIngredient_shouldReturn404NotFound() {
        // Arrange

        Long id = 1L;

        UpdateIngredientDto updateIngredientDto = new UpdateIngredientDto(
                "Some Ingredient name",
                "Some Ingredient description"
        );

        // Act
        ResultActions resultActions = mockMvc.perform(
                put("/api/v1/ingredients/{id}", id)
                        .contentType(APPLICATION_JSON_VALUE)
                        .accept(APPLICATION_PROBLEM_JSON)
                        .content(objectMapper.writeValueAsString(updateIngredientDto)));

        // Assert
        resultActions.andExpect(status().isNotFound());

        assertThat(ingredientRepository.existsById(id))
                .isFalse();

        // Documentation
        resultActions.andDo(document("update-ingredient-by-id-not-found",
                resource(
                        ResourceSnippetParameters.builder()
                                .tag(SCHEMA_TAG)
                                .summary("Update ingredient by ID")
                                .description("Updates and returns an ingredient if exists.")
                                .requestSchema(Schema.schema("UpdateIngredientDto"))
                                .responseSchema(Schema.schema("ProblemDetail"))
                                .responseFields(
                                        GlobalDtoSnippetsProvider.getProblemDetailFields()
                                )
                                .build()
                )
        ));
    }

    @Test
    @SneakyThrows
    @DisplayName("Update Ingredient By Id - Should Return 409 Conflict")
    public void updateIngredient_shouldReturn409Conflict() {
        // Arrange
        String updatedName = "Ingredient 2";

        IngredientEntity ingredientEntity = IngredientEntity.builder()
                .name("Ingredient 1")
                .description("Description of ingredient 1")
                .build();

        IngredientEntity anotherIngredientEntity = IngredientEntity.builder()
                .name("Ingredient 2")
                .description("Description of ingredient 2")
                .build();

        ingredientEntity = ingredientRepository.save(ingredientEntity);
        ingredientRepository.save(anotherIngredientEntity);

        Long id = ingredientEntity.getId();

        UpdateIngredientDto updateIngredientDto = new UpdateIngredientDto(
                updatedName,
                "Updated description of Ingredient 1"
        );

        // Act
        ResultActions resultActions = mockMvc.perform(
                put("/api/v1/ingredients/{id}", id)
                        .contentType(APPLICATION_JSON_VALUE)
                        .accept(APPLICATION_PROBLEM_JSON)
                        .content(objectMapper.writeValueAsString(updateIngredientDto)));

        // Assert
        resultActions.andExpect(status().isConflict());

        assertThat(ingredientRepository.existsByName(updatedName))
                .isTrue();

        ingredientEntity = ingredientRepository.findById(id).get();

        assertThat(ingredientEntity.getName())
                .isNotEqualTo(updatedName);

        // Documentation
        resultActions.andDo(document("update-ingredient-by-id-conflict",
                resource(
                        ResourceSnippetParameters.builder()
                                .tag(SCHEMA_TAG)
                                .summary("Update ingredient by ID")
                                .description("Updates and returns an ingredient if exists.")
                                .requestSchema(Schema.schema("UpdateIngredientDto"))
                                .responseSchema(Schema.schema("ProblemDetail"))
                                .responseFields(
                                        GlobalDtoSnippetsProvider.getProblemDetailFields()
                                )
                                .build()
                )
        ));
    }

    @Test
    @SneakyThrows
    @DisplayName("Delete Ingredient By Id - Should Delete And Return 204 No Content")
    public void deleteIngredientById_shouldDeleteAndReturn204NoContent() {
        // Arrange
        IngredientEntity existingIngredient = IngredientEntity.builder()
                .name("Ingredient 1")
                .description("Some description")
                .build();

        existingIngredient = ingredientRepository.save(existingIngredient);

        Long id = existingIngredient.getId();

        // Act
        ResultActions resultActions = mockMvc.perform(
                delete("/api/v1/ingredients/{id}", id)
                        .accept(APPLICATION_JSON_VALUE));

        // Assert
        resultActions.andExpect(status().isNoContent());

        assertThat(ingredientRepository.existsById(id))
                .isFalse();

        // Documentation
        resultActions.andDo(document("delete-ingredient-no-content",
                resource(
                        ResourceSnippetParameters.builder()
                                .tag(SCHEMA_TAG)
                                .summary("Delete an ingredient")
                                .description("Deletes an ingredient by its ID if exists.")
                                .pathParameters(
                                        parameterWithName("id")
                                                .type(SimpleType.NUMBER)
                                                .description("Identifier of the ingredient to be deleted.")
                                )
                                .build()
                )
        ));
    }
}
