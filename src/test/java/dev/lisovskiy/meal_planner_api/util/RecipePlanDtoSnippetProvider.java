package dev.lisovskiy.meal_planner_api.util;

import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.List;
import java.util.stream.Stream;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class RecipePlanDtoSnippetProvider {

    public static List<FieldDescriptor> getRecipePlanDtoFields() {
        return getRecipePlanDtoFieldsWithPrefix("");
    }

    public static List<FieldDescriptor> getRecipePlanDtoFieldsWithPrefix(String prefix) {
        String p = PrefixValidator.validate(prefix);

        Stream<FieldDescriptor> rootFields = Stream.of(
                fieldWithPath(p + "id")
                        .description("The recipe plan ID."),
                fieldWithPath(p + "description")
                        .description("The recipe plan description."),
                fieldWithPath(p + "time")
                        .description("Time to eat of recipe plan."),
                fieldWithPath(p + "recipe")
                        .type(JsonFieldType.OBJECT)
                        .description("Target recipe of recipe plan.")
        );

        Stream<FieldDescriptor> nestedFields = RecipeDtoSnippetProvider
                .getEmptyRecipeDtoFieldsWithPrefix(p + "recipe").stream();

        return Stream.concat(rootFields, nestedFields).toList();
    }

    public static List<FieldDescriptor> getRecipePlanListDtoFields() {
        Stream<FieldDescriptor> rootFields = Stream.of(
                fieldWithPath("recipePlans")
                        .type(JsonFieldType.ARRAY)
                        .description("List of recipe plans.")
        );

        Stream<FieldDescriptor> nestedFields = getRecipePlanDtoFieldsWithPrefix("recipePlans[]")
                .stream();

        return Stream.concat(rootFields, nestedFields).toList();
    }

    public static List<FieldDescriptor> getUpdateRecipePlanDtoFields() {
        return List.of(
                fieldWithPath("mealPlanId")
                        .description("New MealPlan ID."),
                fieldWithPath("recipeId")
                        .description("New Recipe ID."),
                fieldWithPath("description")
                        .description("New description."),
                fieldWithPath("time")
                        .description("New time.")
        );
    }
}
