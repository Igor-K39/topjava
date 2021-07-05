<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html">
    <meta charset="UTF-8">
    <link href="style/editMeal.css" rel="stylesheet">
    <title>${requestScope.title}</title>
</head>
<body>
<a href="meals"><h3>Back to meals</h3></a>
<hr>
<form action="meals" method="post" accept-charset="UTF-8">
    <jsp:useBean id="meal" scope="request" type="ru.javawebinar.topjava.model.Meal"/>
    <table>
        <caption>${requestScope.title}</caption>
        <tr>
            <td><label for="dateTime">DateTime: </label></td>
            <td><input id="dateTime" name="dateTime" type="datetime-local" value="${meal.dateTime}"></td>
        </tr>
        <tr>
            <td><label for="description">Description: </label></td>
            <td><input id="description" name="description" size="22" type="text" value="${meal.description}"></td>
        </tr>
        <tr>
            <td><label for="calories">Calories: </label></td>
            <td><input id="calories" name="calories" type="number" value="${meal.calories}"></td>
        </tr>
        <tr>
            <td class="buttons" colspan="2">
                <input type="submit" name="submit" value="Submit">
                <input type="reset" value="Reset">
            </td>
        </tr>
    </table>
    <input type="hidden" name="id" value="${meal.id}">
</form>
</body>
</html>
