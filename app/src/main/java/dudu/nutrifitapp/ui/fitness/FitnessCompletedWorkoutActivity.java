package dudu.nutrifitapp.ui.fitness;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import dudu.nutrifitapp.databinding.FitnessCompletedWorkoutBinding;
import dudu.nutrifitapp.model.DailyStats;

public class FitnessCompletedWorkoutActivity extends AppCompatActivity {

    private FitnessCompletedWorkoutBinding binding;
    private double userWeight;
    private double metValue;
    private int totalExercises;
    private int totalTime;
    private double caloriesBurnt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FitnessCompletedWorkoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int workoutImageResId = getIntent().getIntExtra("WORKOUT_IMAGE_RES_ID", 0);
        String workoutTitle = getIntent().getStringExtra("WORKOUT_TITLE");
        String workoutLevel = getIntent().getStringExtra("WORKOUT_LEVEL");
        totalExercises = getIntent().getIntExtra("TOTAL_EXERCISES", 0);
        totalTime = getIntent().getIntExtra("TOTAL_TIME", 0);
        metValue = getMetValue(workoutTitle);

        binding.imageWorkoutCompleted.setImageResource(workoutImageResId);
        binding.textCongratulations.setText("Congratulations, you've completed the workout!");
        binding.textWorkoutTitle.setText(workoutTitle);
        binding.textExercisesCount.setText(String.valueOf(totalExercises));
        binding.textWorkoutTime.setText(totalTime + " minutes");

        getUserWeightFromDatabase();
        binding.buttonNext.setOnClickListener(v -> {
            saveWorkoutStatistics();
            finish();
        });
    }

    private void getUserWeightFromDatabase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User").child(userId).child("nutritiveProfile").child("weight");

            databaseReference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult().exists()) {
                    userWeight = task.getResult().getValue(Double.class);
                    caloriesBurnt = calculateCaloriesBurnt();
                    binding.textCaloriesBurnt.setText(String.format("%.2f", caloriesBurnt));
                } else {
                    Toast.makeText(this, "Failed to get user weight", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private double calculateCaloriesBurnt() {
        return userWeight * metValue;  // Convert time to hours
    }

    private double getMetValue(String workoutTitle) {
        switch (workoutTitle) {
            case "Legs Beginner":
                return 0.8;
            case "Arms Beginner":
                return 0.6;
            case "Abs Beginner":
                return 0.4;
            case "Chest Beginner":
                return 0.8;
            case "Legs Intermediate":
                return 1.3;
            case "Arms Intermediate":
                return 0.9;
            case "Abs Intermediate":
                return 0.6;
            case "Chest Intermediate":
                return 1.4;
            case "Legs Advanced":
                return 1.4;
            case "Arms Advanced":
                return 1.4;
            case "Abs Advanced":
                return 0.6;
            case "Chest Advanced":
                return 1.6;
            default:
                return 1.0;
        }
    }

    private void saveWorkoutStatistics() {
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("DailyStats").child(date);

        databaseReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                Map<String, Object> updates = new HashMap<>();
                updates.put("caloriesBurnt", task.getResult().child("caloriesBurnt").getValue(Double.class) + caloriesBurnt);
                updates.put("workoutsCompleted", task.getResult().child("workoutsCompleted").getValue(Integer.class) + 1);
                updates.put("timeSpent", task.getResult().child("timeSpent").getValue(Integer.class) + totalTime);
                databaseReference.updateChildren(updates);
            } else {
                databaseReference.setValue(new DailyStats(caloriesBurnt, 1, totalTime));
            }
        });
    }
}
