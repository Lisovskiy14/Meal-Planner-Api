package dev.lisovskiy.meal_planner_api.util;


import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.List;
import java.util.stream.Stream;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class MealPlanDtoSnippetProvider {

    public static List<FieldDescriptor> getMealPlanDtoFields() {
        return getMealPlanDtoFieldsWithPrefix("");
    }

    public static List<FieldDescriptor> getMealPlanDtoFieldsWithPrefix(String prefix) {
        String p = PrefixValidator.validate(prefix);

        return List.of(
                fieldWithPath(p + "id")
                        .description("Identifier of a Meal Plan."),
                fieldWithPath(p + "description")
                        .description("Description of a Meal Plan."),
                fieldWithPath(p + "recipePlans")
                        .type(JsonFieldType.ARRAY)
                        .description("List of recipe plans of a Meal Plan."),
                fieldWithPath(p + "dayOfWeek")
                        .description("Scheduled day of week of a Meal Plan.")
        );
    }

    public static List<FieldDescriptor> getMealPlanListDtoFields() {
        Stream<FieldDescriptor> rootFields = Stream.of(
                fieldWithPath("mealPlans")
                        .type(JsonFieldType.ARRAY)
                        .description("List of recipe plans.")
        );

        Stream<FieldDescriptor> nestedFields = getMealPlanDtoFieldsWithPrefix("mealPlans[]")
                .stream();

        return Stream.concat(rootFields, nestedFields).toList();
    }

    public static List<FieldDescriptor> getCreateOrUpdateMealPlanDtoFields() {
        return List.of(
                fieldWithPath("description")
                        .description("Description of a new MealPlan."),
                fieldWithPath("dayOfWeek")
                        .description("Scheduled day of week of a new MealPlan.")
        );
    }
}
