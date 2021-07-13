package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.javawebinar.topjava.model.AbstractBaseEntity;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryBaseRepository<T extends AbstractBaseEntity> {
    private static final Logger logger = LoggerFactory.getLogger(InMemoryBaseRepository.class);
    private static final AtomicInteger counter = new AtomicInteger(0);
    private final Map<Integer, T> storage = new ConcurrentHashMap<>();

    public T save(T entity) {
        logger.info("save() - {}", entity);

        if (entity.isNew()) {
            int id = counter.incrementAndGet();
            entity.setId(id);
            storage.put(id, entity);
            return entity;
        }
        storage.replace(entity.getId(), entity);
        return entity;
    }

    public T get(int id) {
        logger.info("get({})", id);
        return storage.get(id);
    }

    public Collection<T> getAll() {
        logger.info("getAll()");
        return storage.values();
    }

    public boolean delete(int id) {
        logger.info("delete({})", id);
        return storage.remove(id) != null;
    }
}
