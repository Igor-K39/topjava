package ru.javawebinar.topjava.web;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalTime;

import static ru.javawebinar.topjava.util.MealsUtil.*;

@WebServlet(value = "/meals")
public class MealServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("userMeal", filteredByStreams(getMeals(), LocalTime.MIN, LocalTime.MAX, CALORIES_PER_DAY_MAX));
        request.getRequestDispatcher("/meals.jsp").forward(request, response);
    }
}
