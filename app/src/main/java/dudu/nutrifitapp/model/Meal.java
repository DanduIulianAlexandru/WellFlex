package dudu.nutrifitapp.model;

public class Meal {
    public String foodId;
    public String foodName;
    public double carbs;
    public double protein;
    public double fat;
    public int calories;

    public Meal(String foodId, String foodName, double carbs, double protein, double fat, int calories) {
        this.foodId = foodId;
        this.foodName = foodName;
        this.carbs = carbs;
        this.protein = protein;
        this.fat = fat;
        this.calories = calories;
    }
}