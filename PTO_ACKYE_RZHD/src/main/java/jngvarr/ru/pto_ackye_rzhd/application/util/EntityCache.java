package jngvarr.ru.pto_ackye_rzhd.application.util;

import jngvarr.ru.pto_ackye_rzhd.domain.value.EntityType;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

@Component
public class EntityCache {
    private final Map<EntityType, Map<String, ?>> entityCache = new EnumMap<>(EntityType.class);

    public EntityCache() {
        for (
                EntityType type : EntityType.values()) {
            entityCache.put(type, new HashMap<>());
        }
    }
    @SuppressWarnings("unchecked") // глушит предупреждение компилятора о небезопасном приведении типов
    public Map<String, T> get(EntityType type) {
        return (Map<String, T>) entityCache.get(type);
    }

    public void put(EntityType type, String key, T value) {
        get(type).put(key, value);
    }
}