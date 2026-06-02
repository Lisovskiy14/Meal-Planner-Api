package dev.lisovskiy.meal_planner_api.util;

import org.springframework.restdocs.payload.FieldDescriptor;

import java.util.List;
import java.util.stream.Stream;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class RecipeDtoSnippetProvider {

    public static List<FieldDescriptor> getRecipeListDtoFields() {
        Stream<FieldDescriptor> rootField = Stream.of(
                fieldWithPath("recipes")
                        .description("List of recipes")
        );

        Stream<FieldDescriptor> nestedFields = getRecipeDtoFieldsWithPrefix("recipes[]")
                .stream();

        return Stream.concat(rootField, nestedFields)
                .toList();
    }

    public static List<FieldDescriptor> getRecipeDtoFields() {
        return getRecipeDtoFieldsWithPrefix("");
    }

    public static List<FieldDescriptor> getRecipeDtoFieldsWithPrefix(String prefix) {
        String p = PrefixValidator.validate(prefix);

        Stream<FieldDescriptor> rootFields = Stream.of(
                fieldWithPath(p + "id")
                        .description("The recipe ID"),
                fieldWithPath(p + "title")
                        .description("The title of recipe"),
                fieldWithPath(p + "instructions")
                        .description("Prepare instructions of recipe"),
                fieldWithPath(p + "prepTimeMinutes")
                        .description("Preparation time in minutes"),
                fieldWithPath(p + "recipeIngredients")
                        .description("The list of ingredients for the recipe")
        );

        Stream<FieldDescriptor> nestedFields = RecipeIngredientDtoSnippetProvider
                .getRecipeIngredientDtoFieldsWithPrefix(p + "recipeIngredients[]").stream();

        return Stream.concat(rootFields, nestedFields)
                .toList();
    }
}
