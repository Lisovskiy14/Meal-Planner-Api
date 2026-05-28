package dev.lisovskiy.meal_planner_api.web;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.epages.restdocs.apispec.SimpleType;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.lisovskiy.meal_planner_api.AbstractIT;
import dev.lisovskiy.meal_planner_api.common.IngredientUnit;
import dev.lisovskiy.meal_planner_api.dto.recipe_ingredient.RecipeIngredientListDto;
import dev.lisovskiy.meal_planner_api.repository.IngredientRepository;
import dev.lisovskiy.meal_planner_api.repository.RecipeIngredientRepository;
import dev.lisovskiy.meal_planner_api.repository.RecipeRepository;
import dev.lisovskiy.meal_planner_api.repository.entity.IngredientEntity;
import dev.lisovskiy.meal_planner_api.repository.entity.RecipeEntity;
import dev.lisovskiy.meal_planner_api.repository.entity.RecipeIngredientEntity;
import dev.lisovskiy.meal_planner_api.repository.entity.RecipeIngredientId;
import dev.lisovskiy.meal_planner_api.util.GlobalExtractor;
import dev.lisovskiy.meal_planner_api.util.RecipeIngredientDtoSnippetProvider;
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
}
