package dev.lisovskiy.meal_planner_api.web;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.lisovskiy.meal_planner_api.AbstractIT;
import dev.lisovskiy.meal_planner_api.util.GlobalDtoSnippetsProvider;
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

        IngredientListDto actualResult = getObjectFromMvcResult(mvcResult, IngredientListDto.class);

        resultActions.andExpect(status().isOk());

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
    @DisplayName("Create Ingredient - Should Save And Return Ingredient")
    public void saveIngredient_shouldSaveAndReturnIngredient() {
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

        IngredientDto actualResult = getObjectFromMvcResult(mvcResult, IngredientDto.class);

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
                                        fieldWithPath("name").description("Name of new ingredient"),
                                        fieldWithPath("description").description("Description of new ingredient")
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
    @DisplayName("Create Ingredient - Should Return 409 Conflict")
    public void saveIngredient_shouldReturn409Conflict() {
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
                                        parameterWithName("id").description("Identifier of the ingredient to be deleted.")
                                )
                                .build()
                )
        ));
    }

    private <T> T getObjectFromMvcResult(MvcResult mvcResult, Class<T> clazz) throws Exception {
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        return objectMapper.readValue(jsonResponse, clazz);
    }
}
