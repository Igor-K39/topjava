package ru.javawebinar.topjava.storage;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.MealsUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MapMealStorage implements MealStorage {
    AtomicInteger counter = new AtomicInteger(0);
    Map<Integer, Meal> storage = new ConcurrentHashMap<>();

    {
        MealsUtil.getMeals().forEach(this::save);
    }

    @Override
    public Meal get(int id) {
        return storage.get(id);
    }

    @Override
    public Meal save(Meal meal) {
        if (meal.isNew()) {
            int id = counter.incrementAndGet();
            return storage.computeIfAbsent(id, theMeal -> new Meal(id, meal));
        }
        return storage.replace(meal.getId(), meal);
    }

    @Override
    public boolean delete(int id) {
        return storage.remove(id) != null;
    }

    @Override
    public List<Meal> getAll() {
        return new ArrayList<>(storage.values());
    }
}
