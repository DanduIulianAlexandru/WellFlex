package dudu.nutrifitapp.model;

import java.util.List;
import java.util.Map;

public class DailyLog {
    public Map<String, List<String>> meals; // breakfast, lunch, dinner, snack

    public DailyLog() { }

    // Getters and setters...

    public DailyLog(Map<String, List<String>> meals) {
        this.meals = meals;
    }

    public Map<String, List<String>> getMeals() {
        return meals;
    }

    public void setMeals(Map<String, List<String>> meals) {
        this.meals = meals;
    }
}

