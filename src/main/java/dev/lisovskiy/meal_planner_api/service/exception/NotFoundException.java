package dev.lisovskiy.meal_planner_api.service.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
