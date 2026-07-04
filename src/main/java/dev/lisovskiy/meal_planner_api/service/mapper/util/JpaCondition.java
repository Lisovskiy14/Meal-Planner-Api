package dev.lisovskiy.meal_planner_api.service.mapper.util;

import dev.lisovskiy.meal_planner_api.repository.entity.BaseEntity;
import jakarta.persistence.Persistence;
import org.mapstruct.Condition;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class JpaCondition {

    @Condition
    public boolean isCollectionInitialized(Collection<?> collection) {
        if (collection == null) return false;
        return Persistence.getPersistenceUtil().isLoaded(collection);
    }

    @Condition
    public boolean isEntityInitialized(BaseEntity entity) {
        if (entity == null) return false;
        return Persistence.getPersistenceUtil().isLoaded(entity);
    }
}
