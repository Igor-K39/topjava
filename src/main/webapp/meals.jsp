<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<html>
<head>
    <link href="style/meals.css" rel="stylesheet">
    <meta http-equiv="Content-Type" content="text/html">
    <meta charset="UTF-8">
    <title>User meals</title>
</head>

<body>
<table>
    <caption>User meal list</caption>
    <tr>
        <th>Date</th>
        <th>Description</th>
        <th>Calories</th>
    </tr>
    <jsp:useBean id="userMeal" scope="request" type="java.util.List"/>
    <c:forEach var="meal" items="${userMeal}">
        <jsp:useBean id="meal" type="ru.javawebinar.topjava.model.MealTo"/>
        <fmt:parseDate value="${meal.dateTime}" pattern="yyyy-MM-dd'T'HH:mm" var="dateTime"/>
        <tr class="${meal.excess ? "red" : "green"}">
            <td><fmt:formatDate value="${dateTime}" pattern="yyyy-MM-dd HH:mm"/></td>
            <td>${meal.description}</td>
            <td>${meal.calories}</td>
        </tr>
    </c:forEach>
</table>

</body>
</html>
