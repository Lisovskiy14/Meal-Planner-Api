package dev.lisovskiy.meal_planner_api.web.exception;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class ParamsValidationDetails {
    String field;
    String message;
}
