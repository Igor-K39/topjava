package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private static final Logger log = LoggerFactory.getLogger(InMemoryMealRepository.class);
    private static final Comparator<Meal> mealComparator = Comparator
            .comparing(Meal::getDate).reversed()
            .thenComparing(Meal::getTime).reversed();

    private final Map<Integer, Map<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public Meal save(int userId, Meal meal) {
        log.info("save {} for user id {}", meal, userId);
        if (meal.isNew()) {
            int id = counter.incrementAndGet();
            meal.setId(id);
            return repository.computeIfAbsent(userId, ConcurrentHashMap::new).computeIfAbsent(id, key -> meal);
        }
        return repository
                .computeIfAbsent(userId, ConcurrentHashMap::new)
                .computeIfPresent(meal.getId(), (key, value) -> meal);
    }

    @Override
    public boolean delete(int userId, int id) {
        log.info("get id {} for user id {}", id, userId);
        return repository.getOrDefault(userId, Collections.emptyMap()).remove(id) != null;
    }

    @Override
    public Meal get(int userId, int id) {
        log.info("get id {} for user id {}", id, userId);
        return repository.getOrDefault(userId, Collections.emptyMap()).get(id);
    }

    @Override
    public Collection<Meal> getAll(int userId) {
        log.info("getAll for user id {}", userId);
        return getAllFilteredByPredicate(userId, meal -> true);
    }

    @Override
    public Collection<Meal> getAllFilteredByPredicate(int userId, Predicate<Meal> predicate) {
        log.info("getAllFilteredByPredicate");
        return repository.getOrDefault(userId, Collections.emptyMap())
                .values()
                .stream()
                .filter(predicate)
                .sorted(mealComparator)
                .collect(Collectors.toList());
    }
}

