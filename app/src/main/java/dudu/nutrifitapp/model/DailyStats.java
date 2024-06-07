package dudu.nutrifitapp.model;

public class DailyStats {
    public double caloriesBurnt;
    public int workoutsCompleted;
    public int timeSpent;

    public DailyStats(double caloriesBurnt, int workoutsCompleted, int timeSpent) {
        this.caloriesBurnt = caloriesBurnt;
        this.workoutsCompleted = workoutsCompleted;
        this.timeSpent = timeSpent;
    }
}