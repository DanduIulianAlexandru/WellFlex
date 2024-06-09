package dudu.nutrifitapp.ui.fitness;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import dudu.nutrifitapp.R;
import dudu.nutrifitapp.databinding.FragmentFitnessBinding;

public class FitnessFragment extends Fragment {

    private FragmentFitnessBinding binding;
    private Calendar calendar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFitnessBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        calendar = Calendar.getInstance();
        updateDateAndLoadStats();

        binding.buttonPreviousDay.setOnClickListener(v -> {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            updateDateAndLoadStats();
        });

        binding.buttonNextDay.setOnClickListener(v -> {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            updateDateAndLoadStats();
        });

        // Set up buttons for difficulty levels
        binding.btnBeginner.setOnClickListener(v -> {
            showWorkouts("Beginner");
            updateButtonStyles(binding.btnBeginner);
        });
        binding.btnIntermediate.setOnClickListener(v -> {
            showWorkouts("Intermediate");
            updateButtonStyles(binding.btnIntermediate);
        });
        binding.btnAdvanced.setOnClickListener(v -> {
            showWorkouts("Advanced");
            updateButtonStyles(binding.btnAdvanced);
        });

        // Show beginner workouts by default and underline the Beginner button
        showWorkouts("Beginner");
        updateButtonStyles(binding.btnBeginner);

        return view;
    }

    private void updateDateAndLoadStats() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MM/dd", Locale.getDefault());
        String date = sdf.format(calendar.getTime());
        binding.textViewDate.setText(date);
        loadStatsForDate(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime()));
    }

    private void loadStatsForDate(String date) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("DailyStats").child(date);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (binding != null) {  // Check if the binding is still valid
                    if (snapshot.exists()) {
                        binding.tvCaloriesBurnt.setText(String.format(Locale.getDefault(), "%.1f🔥", snapshot.child("caloriesBurnt").getValue(Double.class)));
                        binding.tvWorkoutsCompleted.setText(String.valueOf(snapshot.child("workoutsCompleted").getValue(Integer.class)));
                        binding.tvTimeSpent.setText(snapshot.child("timeSpent").getValue(Integer.class) + " min");
                    } else {
                        binding.tvCaloriesBurnt.setText("0");
                        binding.tvWorkoutsCompleted.setText("0");
                        binding.tvTimeSpent.setText("0 min");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
            }
        });
    }

    private void showWorkouts(String level) {
        binding.workoutsContainer.removeAllViews();

        int[] workoutImages;
        String[] workoutNames;
        switch (level) {
            case "Intermediate":
                workoutImages = new int[]{R.drawable.abs_inter, R.drawable.arms_inter, R.drawable.chest_inter, R.drawable.legs_inter};
                workoutNames = new String[]{"Abs Intermediate", "Arms Intermediate", "Chest Intermediate", "Legs Intermediate"};
                break;
            case "Advanced":
                workoutImages = new int[]{R.drawable.abs_advanced, R.drawable.arms_advanced, R.drawable.chest_advanced, R.drawable.legs_advanced};
                workoutNames = new String[]{"Abs Advanced", "Arms Advanced", "Chest Advanced", "Legs Advanced"};
                break;
            case "Beginner":
            default:
                workoutImages = new int[]{R.drawable.abs_begginer, R.drawable.arms_begginer, R.drawable.chest_begginer, R.drawable.legs_begginer};
                workoutNames = new String[]{"Abs Beginner", "Arms Beginner", "Chest Beginner", "Legs Beginner"};
                break;
        }

        for (int i = 0; i < workoutImages.length; i++) {
            CardView cardView = new CardView(getContext());
            cardView.setRadius(16); // Adjust the corner radius as needed
            cardView.setCardElevation(8); // Add elevation to the card
            cardView.setCardBackgroundColor(getResources().getColor(R.color.background));

            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    400  // Adjust the height as needed to fit the screen nicely
            );
            cardParams.setMargins(0, 8, 0, 8);
            cardView.setLayoutParams(cardParams);

            ImageButton imageButton = new ImageButton(getContext());
            LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
            imageButton.setLayoutParams(imageParams);
            imageButton.setImageResource(workoutImages[i]);
            imageButton.setScaleType(ImageButton.ScaleType.CENTER_CROP);
            imageButton.setBackgroundColor(0);  // Make background transparent

            int imageResId = workoutImages[i];
            String workoutName = workoutNames[i];
            imageButton.setOnClickListener(v -> openWorkoutExecutionActivity(imageResId, workoutName));

            cardView.addView(imageButton);
            binding.workoutsContainer.addView(cardView);
        }
    }

    private void openWorkoutExecutionActivity(int imageResId, String workoutName) {
        Intent intent = new Intent(getContext(), WorkoutActivity.class);
        intent.putExtra(WorkoutActivity.EXTRA_IMAGE_RES_ID, imageResId);
        intent.putExtra(WorkoutActivity.EXTRA_WORKOUT_NAME, workoutName);
        startActivity(intent);
    }

    private void updateButtonStyles(Button selectedButton) {
        Button[] buttons = {binding.btnBeginner, binding.btnIntermediate, binding.btnAdvanced};
        for (Button button : buttons) {
            if (button == selectedButton) {
                button.setPaintFlags(button.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            } else {
                button.setPaintFlags(button.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
