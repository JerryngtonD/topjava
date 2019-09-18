package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExceed;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> mealList = Arrays.asList(
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31, 20, 0), "Ужин", 510)
        );
        getFilteredWithExceeded(mealList, LocalTime.of(7, 0), LocalTime.of(14, 0), 500);
//        .toLocalDate();
//        .toLocalTime();
    }

    public static List<UserMealWithExceed> getFilteredWithExceeded(List<UserMeal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        // TODO return filtered list with correctly exceeded field
        String defaultDescription = "По-умолчанию";
        Map<LocalDate, List<UserMeal>> processedMeals = new HashMap<>();
        Map<LocalDate, Integer> processedCaloriesPerDate = new HashMap<>();

        for (UserMeal meal : mealList) {
            if (meal.getDateTime().toLocalTime().isAfter(startTime) && meal.getDateTime().toLocalTime().isBefore(endTime)) {
                processedCaloriesPerDate.merge(meal.getDateTime().toLocalDate(), meal.getCalories(), Integer::sum);
                int caloriesPerDate = processedCaloriesPerDate.getOrDefault(meal.getDateTime().toLocalDate(), 0);


                List<UserMeal> listMealsWithExceededPerDate = processedMeals.getOrDefault(meal.getDateTime().toLocalDate(), new ArrayList<>());
                listMealsWithExceededPerDate.add(meal);
                processedMeals.put(meal.getDateTime().toLocalDate(), listMealsWithExceededPerDate);
            }
        }

        List<UserMealWithExceed> resultArray = new ArrayList<>();
        for (LocalDate mealsGroupKey : processedMeals.keySet()) {
            boolean isExceeded = processedCaloriesPerDate.get(mealsGroupKey) > caloriesPerDay;
            for (UserMeal meal : processedMeals.get(mealsGroupKey)) {
                resultArray.add(new UserMealWithExceed(meal.getDateTime(), meal.getDescription(), meal.getCalories(), isExceeded));
            }
        }
        return resultArray;
    }
}
