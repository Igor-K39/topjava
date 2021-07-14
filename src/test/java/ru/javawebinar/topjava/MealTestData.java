package ru.javawebinar.topjava;

import org.assertj.core.api.Assertions;
import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

public class MealTestData {
    // id 100_002 according to resources/db/populateDB.sql
    public static final Meal adminLunch = new Meal(100_002,
            LocalDateTime.of(2015, 6, 1, 14, 0), "Админ ланч", 510);

    // id 100_003 according to resources/db/populateDB.sql
    public static final Meal adminDinner = new Meal(100_003,
            LocalDateTime.of(2015, 6, 1, 21, 0), "Админ ужин", 1500);

    // id 100_004 according to resources/db/populateDB.sql
    public static final Meal userBreakfast = new Meal(100_004,
            LocalDateTime.of(2020, 1, 30, 10, 0), "Завтрак", 500);

    // id 100_005 according to resources/db/populateDB.sql
    public static final Meal userLunch = new Meal(100_005,
            LocalDateTime.of(2020, 1, 30, 13, 0), "Обед", 1000);

    // id 100_006 according to resources/db/populateDB.sql
    public static final Meal userDinner = new Meal(100_006,
            LocalDateTime.of(2020, 1, 30, 20, 0), "Ужин", 500);


    public static Meal getNew() {
        return new Meal(
                LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).plusHours(12), "Новая еда", 2000);
    }

    public static Meal getUpdated(Meal meal) {
        return new Meal(meal.getDateTime().plusHours(1),
                "updated " + meal.getDescription(),
                meal.getCalories() * 2);
    }

    public static void assertMatch(Meal actual, Meal expected) {
        Assertions.assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    public static void assertMatch(Iterable<Meal> actual, Iterable<Meal> expected) {
        Assertions.assertThat(actual).usingDefaultElementComparator().isEqualTo(expected);
    }

    public static void assertMatch(Iterable<Meal> actual, Meal... expected) {
        assertMatch(actual, Arrays.asList(expected));
    }
}
