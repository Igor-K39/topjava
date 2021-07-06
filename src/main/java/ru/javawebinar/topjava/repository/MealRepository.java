package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;

import java.util.Collection;
import java.util.function.Predicate;

public interface MealRepository {
    // null if updated meal do not belong to userId
    Meal save(int userId, Meal meal);

    // false if meal do not belong to userId
    boolean delete(int userId, int mealId);

    // null if meal do not belong to userId
    Meal get(int userId, int mealId);

    // ORDERED dateTime desc
    Collection<Meal> getAll(int userId);

    Collection<Meal> getAllFilteredByPredicate(int userId, Predicate<Meal> predicate);
}
