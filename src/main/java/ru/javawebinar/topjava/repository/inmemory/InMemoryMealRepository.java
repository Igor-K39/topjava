package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static ru.javawebinar.topjava.web.SecurityUtil.authUserId;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private static final Comparator<Meal> mealComparator = Comparator.comparing(Meal::getDate).reversed();

    private final Map<Integer, Map<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.meals.forEach(meal -> save(authUserId(), meal));
    }

    @Override
    public Meal save(int userId, Meal meal) {
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
    public boolean delete(int userId, int mealId) {
        return repository.getOrDefault(userId, Collections.emptyMap()).remove(mealId) != null;
    }

    @Override
    public Meal get(int userId, int mealId) {
        return repository.getOrDefault(userId, Collections.emptyMap()).get(mealId);
    }

    @Override
    public Collection<Meal> getAll(int userId) {
        return repository.getOrDefault(userId, Collections.emptyMap())
                .values()
                .stream()
                .sorted(mealComparator)
                .collect(Collectors.toList());
    }
}

