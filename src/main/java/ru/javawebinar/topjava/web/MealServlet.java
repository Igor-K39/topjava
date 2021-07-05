package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.storage.MapMealStorage;
import ru.javawebinar.topjava.storage.MealStorage;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

@WebServlet(value = "/meals")
public class MealServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(MealServlet.class);
    MealStorage storage;

    @Override
    public void init() throws ServletException {
        super.init();
        storage = new MapMealStorage();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameterMap().containsKey("action") ? request.getParameter("action") : " ";
        switch (action) {
            case "create":
                forwardToCreate(request, response);
                break;
            case "edit":
                forwardToEdit(request, response);
                break;
            case "delete":
                delete(request, response);
                break;
            default:
                forwardToShowAll(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        save(request, response);
    }

    private void forwardToCreate(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("title", "Create a meal");
        request.setAttribute("meal", new Meal(0, LocalDate.now().atStartOfDay(), "", 0));
        log.debug("Forward to create form");
        request.getRequestDispatcher("mealEditForm.jsp").forward(request, response);
    }

    private void delete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.debug("getting id of the meal to delete");
        int id = Integer.parseInt(getParameterNotNull(request, "id"));
        if (id == -1) {
            throw new IllegalArgumentException("id must be owned by an existing meal");
        }
        storage.delete(id);
        log.debug("redirect to /meals");
        response.sendRedirect("meals");
    }

    private void forwardToEdit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("getting id of the meal to edit");
        int id = Integer.parseInt(getParameterNotNull(request, "id"));
        Meal meal = id != 0 ?
                storage.get(id) :
                new Meal(0, LocalDateTime.now(), "", 0);
        request.setAttribute("meal", meal);
        request.setAttribute("title", "Edit meal");

        log.debug("Forward to edit form");
        request.getRequestDispatcher("mealEditForm.jsp").forward(request, response);
    }

    private void save(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id = Integer.parseInt(getParameterNotNull(request, "id"));
        int calories = Integer.parseInt(getParameterNotNull(request, "calories"));
        String dateTime = getParameterNotNull(request, "dateTime");
        String description = getParameterNotNull(request, "description");

        Meal meal = new Meal(id, LocalDateTime.parse(dateTime), description, calories);
        log.debug(meal.isNew() ? "Create a new meal" : "Update an existing meal");
        storage.save(meal);
        log.debug("Redirect to meals");
        response.sendRedirect("meals");
    }

    private void forwardToShowAll(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Meal> meals = storage.getAll();
        List<MealTo> mealTos = MealsUtil.filteredByStreams(meals, LocalTime.MIN, LocalTime.MAX, MealsUtil.CALORIES_PER_DAY_MAX);
        request.setAttribute("meals", mealTos);
        log.debug("Forward to meals list");
        request.getRequestDispatcher("meals.jsp").forward(request, response);
    }

    private String getParameterNotNull(HttpServletRequest request, String name) {
        String parameter = request.getParameter(name);
        Objects.requireNonNull(parameter, name + " must not be null");
        return parameter;
    }
}
