package ru.javawebinar.topjava.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.util.List;

import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.*;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    static {
        // Only for postgres driver logging
        // It uses java.util.logging and logged via jul-to-slf4j bridge
        SLF4JBridgeHandler.install();
    }

    @Autowired
    MealService service;

    @Test
    public void create() {
        Meal mealNew = MealTestData.getNew();
        Meal mealCreated = service.create(mealNew, USER_ID);
        int idNew = mealCreated.getId();
        mealNew.setId(idNew);
        assertMatch(mealCreated, mealNew);
        assertMatch(mealNew, service.get(idNew, USER_ID));
    }

    @Test(expected = DataAccessException.class)
    public void createDuplicateDateTime() {
        Meal meal = service.get(userBreakfast.getId(), USER_ID);
        meal.setId(null);
        service.create(meal, USER_ID);
    }

    @Test
    public void delete() {
        service.delete(userBreakfast.getId(), USER_ID);
        Assert.assertThrows(NotFoundException.class, () -> service.get(userBreakfast.getId(), USER_ID));
    }

    @Test
    public void deleteNotFound() {
        Assert.assertThrows(NotFoundException.class, () -> service.delete(NOT_FOUND, USER_ID));
    }

    @Test
    public void get() {
        Meal meal = service.get(userBreakfast.getId(), USER_ID);
        assertMatch(meal, userBreakfast);
    }

    @Test
    public void getAll() {
        List<Meal> meals = service.getAll(ADMIN_ID);
        assertMatch(meals, adminDinner, adminLunch);
    }

    @Test
    public void getForeignMeal() {
        Assert.assertThrows(NotFoundException.class, () -> service.get(adminDinner.getId(), USER_ID));
    }

    @Test
    public void getNotFound() {
        Assert.assertThrows(NotFoundException.class, () -> service.get(NOT_FOUND, USER_ID));
    }

    @Test
    public void getBetweenInclusive() {
        List<Meal> meals = service.getBetweenInclusive(
                LocalDate.of(2020, 1, 30),
                LocalDate.of(2020, 1, 30), USER_ID);
        assertMatch(meals, userDinner, userLunch, userBreakfast);
    }

    @Test
    public void update() {
        Meal mealUpdated = MealTestData.getUpdated(userBreakfast);
        service.update(mealUpdated, USER_ID);
        assertMatch(service.get(mealUpdated.getId(), USER_ID), mealUpdated);
    }

    @Test
    public void updateNotFound() {
        Meal mealUpdated = MealTestData.getUpdated(userBreakfast);
        mealUpdated.setId(NOT_FOUND);
        Assert.assertThrows(NotFoundException.class, () -> service.update(mealUpdated, USER_ID));
    }
}