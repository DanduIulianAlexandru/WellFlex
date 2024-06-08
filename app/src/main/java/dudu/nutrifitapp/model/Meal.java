package dudu.nutrifitapp.model;

public class Meal {
    public String foodId;
    public String foodName;
    public double carbs;
    public double protein;
    public double fat;
    public int calories;
    public String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Meal(String foodId, String foodName, double carbs, double protein, double fat, int calories, String description) {
        this.foodId = foodId;
        this.foodName = foodName;
        this.carbs = carbs;
        this.protein = protein;
        this.fat = fat;
        this.calories = calories;
        this.description = description;
    }

    public Meal(String foodId, String foodName, double carbs, double protein, double fat, int calories) {
        this.foodId = foodId;
        this.foodName = foodName;
        this.carbs = carbs;
        this.protein = protein;
        this.fat = fat;
        this.calories = calories;
    }
    public Meal() {
    }
    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public double getCarbs() {
        return carbs;
    }

    public void setCarbs(double carbs) {
        this.carbs = carbs;
    }

    public double getProtein() {
        return protein;
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }

    public double getFat() {
        return fat;
    }

    public void setFat(double fat) {
        this.fat = fat;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }
}