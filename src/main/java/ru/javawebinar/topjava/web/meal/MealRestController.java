package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;

import java.util.Collection;

import static ru.javawebinar.topjava.util.MealsUtil.DEFAULT_CALORIES_PER_DAY;
import static ru.javawebinar.topjava.util.MealsUtil.getTos;
import static ru.javawebinar.topjava.web.SecurityUtil.authUserId;

@Controller
public class MealRestController {
    private static final Logger log = LoggerFactory.getLogger(MealRestController.class);

    private final MealService service;

    public MealRestController(MealService service) {
        this.service = service;
    }

    public Meal get(int id) {
        int userId = authUserId();
        log.info("get {} for user id {}", id, userId);
        return service.get(authUserId(), id);
    }

    public Collection<MealTo> getAll() {
        int userId = authUserId();
        log.info("getAll for user id {}", userId);
        return getTos(service.getAll(userId), DEFAULT_CALORIES_PER_DAY);
    }

    public Meal save(Meal meal) {
        int userId = authUserId();
        log.info("{} {} for user {}", meal.isNew() ? "create" : "update", meal, userId);
        return service.save(authUserId(), meal);
    }

    public void delete(int id) {
        int userId = authUserId();
        log.info("delete meal id = {} for user {}", id, userId);
        service.delete(userId, id);
    }
}