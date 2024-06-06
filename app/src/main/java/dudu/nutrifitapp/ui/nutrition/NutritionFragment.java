package dudu.nutrifitapp.ui.nutrition;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import dudu.nutrifitapp.databinding.FragmentNutritionBinding;

public class NutritionFragment extends Fragment {

    private static final int REQUEST_CODE_ADD_FOOD = 1;
    private String selectedMealType;

    private FragmentNutritionBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNutritionBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            loadUserData(currentUser.getUid());
        }

        setupAddFoodButtons();

        return view;
    }

    private void setupAddFoodButtons() {
        binding.buttonAddBreakfast.setOnClickListener(v -> openSearchFoodActivity("breakfast"));
        binding.buttonAddLunch.setOnClickListener(v -> openSearchFoodActivity("lunch"));
        binding.buttonAddDinner.setOnClickListener(v -> openSearchFoodActivity("dinner"));
        binding.buttonAddSnack.setOnClickListener(v -> openSearchFoodActivity("snack"));
    }

    private void openSearchFoodActivity(String mealType) {
        selectedMealType = mealType;
        Intent intent = new Intent(getActivity(), SearchFoodActivity.class);
        startActivityForResult(intent, REQUEST_CODE_ADD_FOOD);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_FOOD && resultCode == getActivity().RESULT_OK && data != null) {
            String foodId = data.getStringExtra("foodId");
            addFoodToMeal(foodId);
        }
    }

    private void addFoodToMeal(String foodId) {
        DatabaseReference foodRef = mDatabase.child("Food").child(foodId);
        foodRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String foodName = snapshot.child("Name").getValue(String.class);
                    double carbs = snapshot.child("Carbohydrates").getValue(Double.class);
                    double protein = snapshot.child("Proteins").getValue(Double.class);
                    double fat = snapshot.child("Fat").getValue(Double.class);
                    int calories = snapshot.child("Calories").getValue(Integer.class);

                    switch (selectedMealType) {
                        case "breakfast":
                            updateMealUI(binding.textViewLoggedBreakfast, binding.textViewBreakfastCarbs, binding.textViewBreakfastProtein, binding.textViewBreakfastFat, binding.textViewBreakfastCalories, foodName, carbs, protein, fat, calories);
                            break;
                        case "lunch":
                            updateMealUI(binding.textViewLoggedLunch, binding.textViewLunchCarbs, binding.textViewLunchProtein, binding.textViewLunchFat, binding.textViewLunchCalories, foodName, carbs, protein, fat, calories);
                            break;
                        case "dinner":
                            updateMealUI(binding.textViewLoggedDinner, binding.textViewDinnerCarbs, binding.textViewDinnerProtein, binding.textViewDinnerFat, binding.textViewDinnerCalories, foodName, carbs, protein, fat, calories);
                            break;
                        case "snack":
                            updateMealUI(binding.textViewLoggedSnacks, binding.textViewSnacksCarbs, binding.textViewSnacksProtein, binding.textViewSnacksFat, binding.textViewSnacksCalories, foodName, carbs, protein, fat, calories);
                            break;
                    }

                    updateProgressBar(calories, carbs, protein, fat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
            }
        });
    }

    private void updateMealUI(TextView loggedFoodTextView, TextView carbsTextView, TextView proteinTextView, TextView fatTextView, TextView caloriesTextView, String foodName, double carbs, double protein, double fat, int calories) {
        loggedFoodTextView.setText(foodName);
        carbsTextView.setText(String.format("%.1f g", roundToSingleDecimal(carbs)));
        proteinTextView.setText(String.format("%.1f g", roundToSingleDecimal(protein)));
        fatTextView.setText(String.format("%.1f g", roundToSingleDecimal(fat)));
        caloriesTextView.setText(String.format("%d kcal", calories));
    }

    private void updateProgressBar(int calories, double carbs, double protein, double fat) {
        int currentProgressCalories = binding.progressBar.getProgress();
        binding.progressBar.setProgress(currentProgressCalories + calories);

        String currentCaloriesText = binding.textViewCalorieIntake.getText().toString();
        String[] caloriesParts = currentCaloriesText.split(" / ");
        int currentIntakeCalories = Integer.parseInt(caloriesParts[0].trim());
        binding.textViewCalorieIntake.setText((currentIntakeCalories + calories) + " / " + caloriesParts[1]);

        double currentIntakeCarbs = extractNumericValue(binding.textViewCarbs.getText().toString());
        String carbsMax = extractMaxValue(binding.textViewCarbs.getText().toString());
        binding.textViewCarbs.setText(String.format("%.1f g / %s", roundToSingleDecimal(currentIntakeCarbs + carbs), carbsMax));

        double currentIntakeProtein = extractNumericValue(binding.textViewProtein.getText().toString());
        String proteinMax = extractMaxValue(binding.textViewProtein.getText().toString());
        binding.textViewProtein.setText(String.format("%.1f g / %s", roundToSingleDecimal(currentIntakeProtein + protein), proteinMax));

        double currentIntakeFat = extractNumericValue(binding.textViewFat.getText().toString());
        String fatMax = extractMaxValue(binding.textViewFat.getText().toString());
        binding.textViewFat.setText(String.format("%.1f g / %s", roundToSingleDecimal(currentIntakeFat + fat), fatMax));
    }

    private double extractNumericValue(String text) {
        String[] parts = text.split(" ");
        try {
            return Double.parseDouble(parts[0].trim());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private String extractMaxValue(String text) {
        String[] parts = text.split(" / ");
        return parts[1].trim();
    }

    private double roundToSingleDecimal(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    private void loadUserData(String userId) {
        DatabaseReference userRef = mDatabase.child("User").child(userId);
        userRef.child("nutritiveProfile").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String calories = snapshot.child("Calories").getValue(String.class);
                String carbs = snapshot.child("Carbs").getValue(String.class);
                String fat = snapshot.child("Fat").getValue(String.class);
                String protein = snapshot.child("Protein").getValue(String.class);

                binding.textViewCalorieIntake.setText("0 / " + calories + " kcal");
                binding.textViewCarbs.setText("0 / " + carbs + " g");
                binding.textViewFat.setText("0 / " + fat + " g");
                binding.textViewProtein.setText("0 / " + protein + " g");
                binding.progressBar.setMax(Integer.parseInt(calories));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
            }
        });
    }
}
