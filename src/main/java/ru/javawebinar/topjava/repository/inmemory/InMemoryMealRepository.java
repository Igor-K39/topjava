package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private static final Comparator<Meal> mealComparator = Comparator
            .comparing(Meal::getDate)
            .thenComparing(Meal::getTime).reversed();

    private final Map<Integer, InMemoryBaseRepository<Meal>> repository = new ConcurrentHashMap<>();

    @Override
    public Meal save(int userId, Meal meal) {
        InMemoryBaseRepository<Meal> userMeals = repository.computeIfAbsent(userId, key -> new InMemoryBaseRepository<>());
        return userMeals.save(meal);
    }

    @Override
    public boolean delete(int userId, int id) {
        return repository.getOrDefault(userId, new InMemoryBaseRepository<>()).delete(id);
    }

    @Override
    public Meal get(int userId, int id) {
        return repository.getOrDefault(userId, new InMemoryBaseRepository<>()).get(id);
    }

    @Override
    public Collection<Meal> getAll(int userId) {
        return getAllFilteredByPredicate(userId, meal -> true);
    }

    @Override
    public Collection<Meal> getAllFilteredByPredicate(int userId, Predicate<Meal> predicate) {
        return repository.getOrDefault(userId, new InMemoryBaseRepository<>()).getAll().stream()
                .filter(predicate)
                .sorted(mealComparator)
                .collect(Collectors.toList());
    }
}

