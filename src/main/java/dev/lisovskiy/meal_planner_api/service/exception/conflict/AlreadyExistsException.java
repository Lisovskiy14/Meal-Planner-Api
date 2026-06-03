package dev.lisovskiy.meal_planner_api.service.exception.conflict;

public class AlreadyExistsException extends RuntimeException {
    public AlreadyExistsException(String message) {
        super(message);
    }
}
