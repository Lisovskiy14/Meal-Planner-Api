package dev.lisovskiy.meal_planner_api.service.exception.not_found;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
