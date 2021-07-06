package ru.javawebinar.topjava.service;

import org.springframework.stereotype.Service;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.stream.Collectors;

import static ru.javawebinar.topjava.util.Util.isBetweenHalfOpen;
import static ru.javawebinar.topjava.util.Util.isBetweenInclusive;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNotFoundWithId;

@Service
public class MealService {

    private final MealRepository repository;

    public MealService(MealRepository repository) {
        this.repository = repository;
    }

    public Meal get(int userId, int id) {
        return checkNotFoundWithId(repository.get(userId, id), id);
    }

    public Collection<Meal> getAll(int userId) {
        return repository.getAll(userId);
    }

    public Collection<MealTo> getAllFilteredByDateTime(LocalDate fromDate, LocalDate toDate,
                                                   LocalTime fromTime, LocalTime toTime, int userId) {

        Collection<Meal> meals = repository.getAllFilteredByPredicate(userId, meal ->
                isBetweenInclusive(meal.getDate(), fromDate, toDate));
        
        Collection<MealTo> mealTos = MealsUtil.getTos(meals, SecurityUtil.authUserCaloriesPerDay());
        return mealTos
                .stream()
                .filter(meal -> isBetweenHalfOpen(meal.getDateTime().toLocalTime(), fromTime, toTime))
                .collect(Collectors.toList());
    }

    public Meal save(int userId, Meal meal) {
        return checkNotFoundWithId(repository.save(userId, meal), meal.getId());
    }

    public void delete(int userId, int id) {
        checkNotFoundWithId(repository.delete(userId, id), id);
    }
}