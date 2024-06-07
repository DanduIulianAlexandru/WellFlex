package dudu.nutrifitapp.ui.fitness;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;
import java.util.List;

import dudu.nutrifitapp.R;
import dudu.nutrifitapp.databinding.FitnessWorkoutBinding;
import dudu.nutrifitapp.model.Exercise;

public class WorkoutActivity extends AppCompatActivity {

    public static final String EXTRA_IMAGE_RES_ID = "dudu.nutrifitapp.IMAGE_RES_ID";
    public static final String EXTRA_WORKOUT_NAME = "dudu.nutrifitapp.WORKOUT_NAME";
    public static final String EXTRA_WORKOUT_LEVEL = "dudu.nutrifitapp.WORKOUT_LEVEL";

    private FitnessWorkoutBinding binding;
    private List<Exercise> exercises;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FitnessWorkoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int imageResId = getIntent().getIntExtra(EXTRA_IMAGE_RES_ID, 0);
        String workoutName = getIntent().getStringExtra(EXTRA_WORKOUT_NAME);
        String workoutLevel = getIntent().getStringExtra(EXTRA_WORKOUT_LEVEL);

        binding.imageWorkout.setImageResource(imageResId);
        binding.textWorkoutName.setText(workoutName);

        // Set back button click listener
        binding.buttonBack.setOnClickListener(v -> onBackPressed());

        // Define workout plans
        exercises = getExercises(workoutName);
        int totalTime = getTotalTime(exercises);
        int totalExercises = getTotalExercises(exercises);

        binding.textWorkoutInfo.setText(String.format("%d Minutes - %d Exercises", totalTime, totalExercises));

        // Add exercise summaries dynamically
        addExerciseSummaries(exercises);

        // Set start button click listener
        binding.buttonStart.setOnClickListener(v -> startWorkout(imageResId, workoutName, workoutLevel));
    }

    private void startWorkout(int imageResId, String workoutName, String workoutLevel) {
        Intent intent = new Intent(this, FitnessWorkoutExerciseActivity.class);
        intent.putParcelableArrayListExtra(FitnessWorkoutExerciseActivity.EXTRA_EXERCISES, new ArrayList<>(exercises));
        intent.putExtra(FitnessWorkoutExerciseActivity.EXTRA_CURRENT_EXERCISE_INDEX, 0);
        intent.putExtra(FitnessWorkoutExerciseActivity.EXTRA_IMAGE_RES_ID, imageResId);
        intent.putExtra(FitnessWorkoutExerciseActivity.EXTRA_WORKOUT_NAME, workoutName);
        intent.putExtra(FitnessWorkoutExerciseActivity.EXTRA_WORKOUT_LEVEL, workoutLevel);
        startActivity(intent);
    }

    private List<Exercise> getExercises(String workoutName) {
        List<Exercise> exercises = new ArrayList<>();

        switch (workoutName) {
            case "Abs Beginner":
                exercises.add(new Exercise("Inchworm", 2, R.raw.inchworm));
                exercises.add(new Exercise("Rest", 1, R.raw.rest_summary));
                exercises.add(new Exercise("Reverse Crunches", 3, R.raw.reverse_crunches));
                exercises.add(new Exercise("Rest", 1, R.raw.rest_summary));
                exercises.add(new Exercise("Cobras", 2, R.raw.cobras));
                break;
            case "Arms Beginner":
                exercises.add(new Exercise("Wide Arm Push-ups", 2, R.raw.wide_arm_push_ups));
                exercises.add(new Exercise("Rest", 1, R.raw.rest_summary));
                exercises.add(new Exercise("Frog Press", 3, R.raw.frog_press));
                exercises.add(new Exercise("Rest", 1, R.raw.rest_summary));
                exercises.add(new Exercise("Press Up Position Toe Tap", 2, R.raw.press_up_position_toe_tap));
                break;
            case "Legs Beginner":
                exercises.add(new Exercise("Step Up on Chair", 3, R.raw.step_up_on_chair));
                exercises.add(new Exercise("Rest", 1, R.raw.rest_summary));
                exercises.add(new Exercise("Lunge", 3, R.raw.lunge));
                exercises.add(new Exercise("Rest", 1, R.raw.rest_summary));
                exercises.add(new Exercise("Squat Reach", 2, R.raw.squat_reach));
                break;
            case "Chest Beginner":
                exercises.add(new Exercise("Push-ups", 2, R.raw.push_ups));
                exercises.add(new Exercise("Rest", 1, R.raw.rest_summary));
                exercises.add(new Exercise("Wide Arm Push-ups", 3, R.raw.wide_arm_push_ups));
                exercises.add(new Exercise("Rest", 1, R.raw.rest_summary));
                exercises.add(new Exercise("T Plank Exercise", 2, R.raw.t_plank_exercise));
                break;
            case "Abs Intermediate":
                exercises.add(new Exercise("Seated Abs Circles", 3, R.raw.seated_abs_circles));
                exercises.add(new Exercise("Rest", 1, R.raw.rest_summary));
                exercises.add(new Exercise("Inchworm", 4, R.raw.inchworm));
                exercises.add(new Exercise("Rest", 1, R.raw.rest_summary));
                exercises.add(new Exercise("Reverse Crunches", 3, R.raw.reverse_crunches));
                break;
            case "Arms Intermediate":
                exercises.add(new Exercise("Staggered Push-ups", 3, R.raw.staggered_push_ups));
                exercises.add(new Exercise("Rest", 1, R.raw.rest_summary));
                exercises.add(new Exercise("Press Up Position Toe Tap", 4, R.raw.press_up_position_toe_tap));
                exercises.add(new Exercise("Rest", 1, R.raw.rest_summary));
                exercises.add(new Exercise("Frog Press", 3, R.raw.frog_press));
                break;
            case "Legs Intermediate":
                exercises.add(new Exercise("Jumping Squats", 3, R.raw.jumping_squats));
                exercises.add(new Exercise("Rest", 1, R.raw.rest_summary));
                exercises.add(new Exercise("Split Jumps", 4, R.raw.split_jumps));
                exercises.add(new Exercise("Rest", 1, R.raw.rest_summary));
                exercises.add(new Exercise("Step Up on Chair", 3, R.raw.step_up_on_chair));
                break;
            case "Chest Intermediate":
                exercises.add(new Exercise("Wide Arm Push-ups", 3, R.raw.wide_arm_push_ups));
                exercises.add(new Exercise("Rest", 1, R.raw.rest_summary));
                exercises.add(new Exercise("Burpee Jump", 4, R.raw.burpee_jump));
                exercises.add(new Exercise("Rest", 1, R.raw.rest_summary));
                exercises.add(new Exercise("Push-ups", 3, R.raw.push_ups));
                break;
            case "Abs Advanced":
                exercises.add(new Exercise("T Plank Exercise", 3, R.raw.t_plank_exercise));
                exercises.add(new Exercise("Rest", 1, R.raw.rest_summary));
                exercises.add(new Exercise("Seated Abs Circles", 4, R.raw.seated_abs_circles));
                exercises.add(new Exercise("Rest", 1, R.raw.rest_summary));
                exercises.add(new Exercise("Inchworm", 3, R.raw.inchworm));
                break;
            case "Arms Advanced":
                exercises.add(new Exercise("Staggered Push-ups", 4, R.raw.staggered_push_ups));
                exercises.add(new Exercise("Rest", 1, R.raw.rest_summary));
                exercises.add(new Exercise("Wide Arm Push-ups", 5, R.raw.wide_arm_push_ups));
                exercises.add(new Exercise("Rest", 1, R.raw.rest_summary));
                exercises.add(new Exercise("Frog Press", 4, R.raw.frog_press));
                break;
            case "Legs Advanced":
                exercises.add(new Exercise("Box Jump Exercise", 4, R.raw.box_jump_exercise));
                exercises.add(new Exercise("Rest", 1, R.raw.rest_summary));
                exercises.add(new Exercise("Jumping Squats", 5, R.raw.jumping_squats));
                exercises.add(new Exercise("Rest", 1, R.raw.rest_summary));
                exercises.add(new Exercise("Single Leg Hip Rotation", 4, R.raw.single_leg_hip_rotation));
                break;
            case "Chest Advanced":
                exercises.add(new Exercise("Burpee Jump", 4, R.raw.burpee_jump));
                exercises.add(new Exercise("Rest", 1, R.raw.rest_summary));
                exercises.add(new Exercise("Wide Arm Push-ups", 5, R.raw.wide_arm_push_ups));
                exercises.add(new Exercise("Rest", 1, R.raw.rest_summary));
                exercises.add(new Exercise("T Plank", 4, R.raw.t_plank));
                break;
        }

        return exercises;
    }

    private int getTotalTime(List<Exercise> exercises) {
        int totalTime = 0;
        for (Exercise exercise : exercises) {
            totalTime += exercise.getDuration();
        }
        return totalTime;
    }

    private int getTotalExercises(List<Exercise> exercises) {
        int totalExercises = 0;
        for (Exercise exercise : exercises) {
            if (!exercise.getName().equalsIgnoreCase("Rest")) {
                totalExercises++;
            }
        }
        return totalExercises;
    }

    private void addExerciseSummaries(List<Exercise> exercises) {
        for (Exercise exercise : exercises) {
            View exerciseView = LayoutInflater.from(this).inflate(R.layout.fitness_workout_summary, binding.summaryContainer, false);
            LottieAnimationView animationView = exerciseView.findViewById(R.id.lottieAnimationView);
            TextView textViewExercise = exerciseView.findViewById(R.id.textViewExercise);
            TextView textViewDuration = exerciseView.findViewById(R.id.textViewDuration);

            animationView.setAnimation(exercise.getAnimationResId());
            animationView.playAnimation();
            textViewExercise.setText(exercise.getName());
            textViewDuration.setText(String.format("%d minute%s", exercise.getDuration(), exercise.getDuration() > 1 ? "s" : ""));

            binding.summaryContainer.addView(exerciseView);
        }
    }
}
