package ru.javawebinar.topjava.web;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.StringUtils;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.meal.MealRestController;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Objects;

public class MealServlet extends HttpServlet {
    private static final ConfigurableApplicationContext appCtx = new ClassPathXmlApplicationContext("spring/spring-app.xml");

    private final MealRestController mealRestController = appCtx.getBean(MealRestController.class);

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        MealsUtil.meals.forEach(mealRestController::create);
    }

    @Override
    public void destroy() {
        super.destroy();
        appCtx.close();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        
        Meal meal = new Meal(
                LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories"))
        );

        if (StringUtils.hasLength(request.getParameter("id"))) {
            mealRestController.update(meal, getId(request));
        } else {
            mealRestController.create(meal);
        }
        response.sendRedirect("meals");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        switch (action == null ? "all" : action) {
            case "delete":
                delete(request, response);
                break;
            case "create":
            case "update":
                forwardMealForm(request, response, action);
                break;
            case "filter":
                filter(request, response);
                break;
            case "all":
            default:
                request.setAttribute("meals", mealRestController.getAll());
                request.getRequestDispatcher("meals.jsp").forward(request, response);
                break;
        }
    }

    private void forwardMealForm(HttpServletRequest request, HttpServletResponse response, String action) throws ServletException, IOException {
        final Meal meal = "create".equals(action)
                ? new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000)
                : mealRestController.get(getId(request));

        request.setAttribute("meal", meal);
        request.getRequestDispatcher("mealForm.jsp").forward(request, response);
    }

    private void delete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id = getId(request);
        mealRestController.delete(id);
        response.sendRedirect("meals");
    }

    private void filter(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("startDate", request.getParameter("startDate"));
        request.setAttribute("endDate", request.getParameter("endDate"));
        request.setAttribute("startTime", request.getParameter("startTime"));
        request.setAttribute("endTime", request.getParameter("endTime"));

        LocalDate startDate = LocalDate.parse(getParameterOrDefault(request, "startDate", LocalDate.MIN.toString()));
        LocalDate endDate = LocalDate.parse(getParameterOrDefault(request, "endDate", LocalDate.MAX.toString()));

        LocalTime startTime = LocalTime.parse(getParameterOrDefault(request, "startTime", LocalTime.MIN.toString()));
        LocalTime endTime = LocalTime.parse(getParameterOrDefault(request, "endTime", LocalTime.MAX.toString()));

        Collection<MealTo> meals = mealRestController.getAllFilteredByDateTime(startDate, endDate, startTime, endTime);
        request.setAttribute("meals", meals);
        request.getRequestDispatcher("meals.jsp").forward(request, response);
    }

    private int getId(HttpServletRequest request) {
        String paramId = Objects.requireNonNull(request.getParameter("id"));
        return Integer.parseInt(paramId);
    }

    private String getParameterOrDefault(HttpServletRequest request, String name, String defaultValue) {
        String parameter = request.getParameterMap().containsKey(name) ? request.getParameter(name) : defaultValue;
        return parameter.isEmpty() ? defaultValue : parameter;
    }
}
