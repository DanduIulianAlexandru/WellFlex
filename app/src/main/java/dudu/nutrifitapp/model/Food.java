package dudu.nutrifitapp.model;

public class Food {
    public int calories;
    public double carbohydrates;
    public String category;
    public double fat;
    public String name;
    public double proteins;
    public double sugars;

    public Food() { }

    public Food(int calories, double carbohydrates, String category, double fat, String name, double proteins, double sugars) {
        this.calories = calories;
        this.carbohydrates = carbohydrates;
        this.category = category;
        this.fat = fat;
        this.name = name;
        this.proteins = proteins;
        this.sugars = sugars;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public double getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(double carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getFat() {
        return fat;
    }

    public void setFat(double fat) {
        this.fat = fat;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getProteins() {
        return proteins;
    }

    public void setProteins(double proteins) {
        this.proteins = proteins;
    }

    public double getSugars() {
        return sugars;
    }

    public void setSugars(double sugars) {
        this.sugars = sugars;
    }
}
