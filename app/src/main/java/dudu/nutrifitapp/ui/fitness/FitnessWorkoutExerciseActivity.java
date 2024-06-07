package dudu.nutrifitapp.ui.fitness;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import dudu.nutrifitapp.databinding.FitnessWorkoutExerciseBinding;
import dudu.nutrifitapp.model.Exercise;

public class FitnessWorkoutExerciseActivity extends AppCompatActivity {

    public static final String EXTRA_EXERCISES = "dudu.nutrifitapp.EXERCISES";
    public static final String EXTRA_CURRENT_EXERCISE_INDEX = "dudu.nutrifitapp.CURRENT_EXERCISE_INDEX";
    public static final String EXTRA_IMAGE_RES_ID = "dudu.nutrifitapp.IMAGE_RES_ID";
    public static final String EXTRA_WORKOUT_NAME = "dudu.nutrifitapp.WORKOUT_NAME";
    public static final String EXTRA_WORKOUT_LEVEL = "dudu.nutrifitapp.WORKOUT_LEVEL";

    private FitnessWorkoutExerciseBinding binding;
    private List<Exercise> exercises;
    private int currentExerciseIndex;
    private CountDownTimer timer;
    private boolean isPaused = false;
    private long timeRemaining;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FitnessWorkoutExerciseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        exercises = getIntent().getParcelableArrayListExtra(EXTRA_EXERCISES);
        currentExerciseIndex = getIntent().getIntExtra(EXTRA_CURRENT_EXERCISE_INDEX, 0);

        binding.buttonBack.setOnClickListener(v -> onBackPressed());

        binding.buttonPause.setOnClickListener(v -> {
            if (isPaused) {
                resumeTimer();
            } else {
                pauseTimer();
            }
        });

        binding.buttonSkip.setOnClickListener(v -> skipExercise());

        startExercise();
    }

    private void startExercise() {
        if (timer != null) {
            timer.cancel();
        }

        Exercise currentExercise = exercises.get(currentExerciseIndex);
        binding.textCurrentExercise.setText(currentExercise.getName());
        binding.lottieAnimationView.setAnimation(currentExercise.getAnimationResId());
        binding.lottieAnimationView.playAnimation();

        timeRemaining = currentExercise.getDuration() * 60 * 1000;
        startTimer(timeRemaining);
    }

    private void startTimer(long duration) {
        timer = new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeRemaining = millisUntilFinished;
                updateTimerText();
            }

            @Override
            public void onFinish() {
                if (currentExerciseIndex < exercises.size() - 1) {
                    currentExerciseIndex++;
                    startExercise();
                } else {
                    finishWorkout();
                }
            }
        }.start();
    }

    private void updateTimerText() {
        int minutes = (int) (timeRemaining / 1000) / 60;
        int seconds = (int) (timeRemaining / 1000) % 60;
        binding.textTimer.setText(String.format("%02d:%02d", minutes, seconds));
    }

    private void pauseTimer() {
        timer.cancel();
        binding.lottieAnimationView.pauseAnimation();
        binding.buttonPause.setText("Resume");
        isPaused = true;
    }

    private void resumeTimer() {
        startTimer(timeRemaining);
        binding.lottieAnimationView.resumeAnimation();
        binding.buttonPause.setText("Pause");
        isPaused = false;
    }

    private void skipExercise() {
        if (timer != null) {
            timer.cancel();
        }

        if (currentExerciseIndex < exercises.size() - 1) {
            currentExerciseIndex++;
            startExercise();
        } else {
            finishWorkout();
        }
    }

    private void finishWorkout() {
        Intent intent = new Intent(this, FitnessCompletedWorkoutActivity.class);
        intent.putExtra("WORKOUT_IMAGE_RES_ID", getIntent().getIntExtra(EXTRA_IMAGE_RES_ID, 0));
        intent.putExtra("WORKOUT_TITLE", getIntent().getStringExtra(EXTRA_WORKOUT_NAME));
        intent.putExtra("WORKOUT_LEVEL", getIntent().getStringExtra(EXTRA_WORKOUT_LEVEL));
        intent.putExtra("TOTAL_EXERCISES", exercises.size());
        intent.putExtra("TOTAL_TIME", getTotalTime(exercises));
        startActivity(intent);
        finish();
    }

    private int getTotalTime(List<Exercise> exercises) {
        int totalTime = 0;
        for (Exercise exercise : exercises) {
            totalTime += exercise.getDuration();
        }
        return totalTime;
    }
}
