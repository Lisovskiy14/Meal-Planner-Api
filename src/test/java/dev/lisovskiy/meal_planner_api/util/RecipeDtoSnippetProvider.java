package dev.lisovskiy.meal_planner_api.util;

import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.List;
import java.util.stream.Stream;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class RecipeDtoSnippetProvider {

    public static List<FieldDescriptor> getRecipeListDtoFields() {
        Stream<FieldDescriptor> rootField = Stream.of(
                fieldWithPath("recipes")
                        .description("List of recipes")
        );

        Stream<FieldDescriptor> nestedFields = getRecipeSummaryDtoFieldsWithPrefix("recipes[]")
                .stream();

        return Stream.concat(rootField, nestedFields)
                .toList();
    }

    public static List<FieldDescriptor> getRecipeDtoFields() {
        return getRecipeDtoFieldsWithPrefix("");
    }

    public static List<FieldDescriptor> getRecipeSummaryDtoFields() {
        return getRecipeSummaryDtoFieldsWithPrefix("");
    }

    public static List<FieldDescriptor> getRecipeDtoFieldsWithPrefix(String prefix) {
        String p = PrefixValidator.validate(prefix);

        Stream<FieldDescriptor> rootFields = Stream.concat(
                getRecipeSummaryDtoFieldsWithPrefix(p).stream(),
                Stream.of(
                        fieldWithPath(p + "recipeIngredients")
                                .type(JsonFieldType.ARRAY)
                                .description("The list of ingredients for the recipe")
                )
        );

        Stream<FieldDescriptor> nestedFields = RecipeIngredientDtoSnippetProvider
                .getRecipeIngredientDtoFieldsWithPrefix(p + "recipeIngredients[]").stream();

        return Stream.concat(rootFields, nestedFields)
                .toList();
    }

    public static List<FieldDescriptor> getEmptyRecipeDtoFields() {
        return Stream.concat(
                getRecipeSummaryDtoFields().stream(),
                Stream.of(
                        fieldWithPath("recipeIngredients")
                                .type(JsonFieldType.ARRAY)
                                .description("The list of ingredients for the recipe")
                )
        ).toList();
    }

    public static List<FieldDescriptor> getRecipeSummaryDtoFieldsWithPrefix(String prefix) {
        String p = PrefixValidator.validate(prefix);

        return List.of(
                fieldWithPath(p + "id")
                        .description("The recipe ID"),
                fieldWithPath(p + "title")
                        .description("The title of recipe"),
                fieldWithPath(p + "instructions")
                        .description("Prepare instructions of recipe"),
                fieldWithPath(p + "prepTimeMinutes")
                        .description("Preparation time in minutes")
        );
    }

    public static List<FieldDescriptor> getEmptyCreateRecipeDtoFields() {
        return List.of(
                fieldWithPath("title")
                        .description("The title of new recipe"),
                fieldWithPath("instructions")
                        .description("Prepare instructions of new recipe"),
                fieldWithPath("prepTimeMinutes")
                        .description("Preparation time in minutes"),
                fieldWithPath("recipeIngredients")
                        .description("The list of ingredients for the recipe")
        );
    }

    public static List<FieldDescriptor> getCreateRecipeDtoFields() {
        Stream<FieldDescriptor> rootFields = getEmptyCreateRecipeDtoFields().stream();

        Stream<FieldDescriptor> nestedFields = RecipeIngredientDtoSnippetProvider
                .getCreateRecipeIngredientDtoFieldsWithPrefix("recipeIngredients[]").stream();

        return Stream.concat(rootFields, nestedFields)
                .toList();
    }

    public static List<FieldDescriptor> getUpdateRecipeDtoFields() {
        return List.of(
                fieldWithPath("title")
                        .description("New title of recipe"),
                fieldWithPath("instructions")
                        .description("New prepare instructions of recipe"),
                fieldWithPath("prepTimeMinutes")
                        .description("New preparation time in minutes")
        );
    }


}
