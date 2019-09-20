package ru.javawebinar.topjava.util;

import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion;
import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExceed;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

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


        List<UserMealWithExceed> viaArray = getFilteredWithExceeded(mealList, LocalTime.of(7, 0), LocalTime.of(14, 0), 500);
        List<UserMealWithExceed> viaStream = getFilteredWithExceededViaStream(mealList, LocalTime.of(7, 0), LocalTime.of(14, 0), 500);

        System.out.println(viaArray.equals(viaStream));
    }

    public static List<UserMealWithExceed> getFilteredWithExceeded(List<UserMeal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, List<UserMeal>> processedMeals = new HashMap<>();
        Map<LocalDate, Integer> processedCaloriesPerDate = new HashMap<>();

        for (UserMeal meal : mealList) {
            if (meal.getTime().isAfter(startTime) && meal.getTime().isBefore(endTime)) {
                processedCaloriesPerDate.merge(meal.getDate(), meal.getCalories(), Integer::sum);
                int caloriesPerDate = processedCaloriesPerDate.getOrDefault(meal.getDate(), 0);


                List<UserMeal> listMealsWithExceededPerDate = processedMeals.getOrDefault(meal.getDate(), new ArrayList<>());
                listMealsWithExceededPerDate.add(meal);
                processedMeals.put(meal.getDate(), listMealsWithExceededPerDate);
            }
        }

        List<UserMealWithExceed> resultArray = new ArrayList<>();
        for (LocalDate mealsGroupKey : processedMeals.keySet()) {
            boolean isExceeded = processedCaloriesPerDate.get(mealsGroupKey) > caloriesPerDay;
            for (UserMeal meal : processedMeals.get(mealsGroupKey)) {
                resultArray.add(new UserMealWithExceed(meal, isExceeded));
            }
        }
        return resultArray;
    }

    public static List<UserMealWithExceed> getFilteredWithExceededViaStream(List<UserMeal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
            List<UserMeal> filteredMealList = mealList.stream()
                    .filter(meal -> meal.getTime().isBefore(endTime) && meal.getTime()
                    .isAfter(startTime)).collect(Collectors.toList());
            Map<LocalDate, Integer> caloriesPerDare = filteredMealList.stream()
                    .collect( Collectors.groupingBy(UserMeal::getDate, Collectors.summingInt(UserMeal::getCalories)));

            return filteredMealList.stream().map(meal -> new UserMealWithExceed(meal, caloriesPerDare.get(meal.getDate()) > caloriesPerDay)).collect(Collectors.toList());
    }
}
