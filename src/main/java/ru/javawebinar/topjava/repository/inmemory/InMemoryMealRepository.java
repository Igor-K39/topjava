package ru.javawebinar.topjava.repository.inmemory;

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
    private static final Comparator<Meal> mealComparator = Comparator
            .comparing(Meal::getDate)
            .thenComparing(Meal::getTime).reversed();

    private final Map<Integer, Map<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public Meal save(int userId, Meal meal) {
        Map<Integer, Meal> userMeals = repository.computeIfAbsent(userId, ConcurrentHashMap::new);
        if (meal.isNew()) {
            int id = counter.incrementAndGet();
            meal.setId(id);
            return userMeals.computeIfAbsent(id, key -> meal);
        }
        return userMeals.computeIfPresent(meal.getId(), (key, value) -> meal);
    }

    @Override
    public boolean delete(int userId, int id) {
        return repository.getOrDefault(userId, Collections.emptyMap()).remove(id) != null;
    }

    @Override
    public Meal get(int userId, int id) {
        return repository.getOrDefault(userId, Collections.emptyMap()).get(id);
    }

    @Override
    public Collection<Meal> getAll(int userId) {
        return getAllFilteredByPredicate(userId, meal -> true);
    }

    @Override
    public Collection<Meal> getAllFilteredByPredicate(int userId, Predicate<Meal> predicate) {
        return repository.getOrDefault(userId, Collections.emptyMap()).values().stream()
                .filter(predicate)
                .sorted(mealComparator)
                .collect(Collectors.toList());
    }
}

