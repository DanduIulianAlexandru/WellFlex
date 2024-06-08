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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import dudu.nutrifitapp.databinding.FragmentNutritionBinding;
import dudu.nutrifitapp.model.Meal;

public class NutritionFragment extends Fragment {

    private static final int REQUEST_CODE_ADD_FOOD = 1;
    private static final int REQUEST_CODE_ADD_CUSTOM_FOOD = 2;
    private String selectedMealType;
    private FragmentNutritionBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private int maxCalories;
    private double maxCarbs;
    private double maxProtein;
    private double maxFat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNutritionBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("EEEE, dd/MM", Locale.getDefault());

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            loadUserData(currentUser.getUid());
        }

        setupAddFoodButtons();
        setupDateNavigation();
        setupGeneratePieChartButton();
        return view;
    }

    private void setupAddFoodButtons() {
        binding.buttonAddBreakfast.setOnClickListener(v -> openSearchFoodActivity("breakfast"));
        binding.buttonAddLunch.setOnClickListener(v -> openSearchFoodActivity("lunch"));
        binding.buttonAddDinner.setOnClickListener(v -> openSearchFoodActivity("dinner"));
        binding.buttonAddSnack.setOnClickListener(v -> openSearchFoodActivity("snack"));
    }

    private void setupDateNavigation() {
        binding.textViewDate.setText(dateFormat.format(calendar.getTime()));
        binding.buttonPreviousDay.setOnClickListener(v -> changeDate(-1));
        binding.buttonNextDay.setOnClickListener(v -> changeDate(1));
    }

    private void changeDate(int amount) {
        calendar.add(Calendar.DAY_OF_MONTH, amount);
        binding.textViewDate.setText(dateFormat.format(calendar.getTime()));
        loadMealsForDate(calendar.getTimeInMillis());
    }

    private void openSearchFoodActivity(String mealType) {
        selectedMealType = mealType;
        Intent intent = new Intent(getActivity(), SearchFoodActivity.class);
        startActivityForResult(intent, REQUEST_CODE_ADD_FOOD);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REQUEST_CODE_ADD_FOOD || requestCode == REQUEST_CODE_ADD_CUSTOM_FOOD) && resultCode == getActivity().RESULT_OK && data != null) {
            String foodId = data.getStringExtra("foodId");
            String foodName = data.getStringExtra("foodName");
            double carbs = data.getDoubleExtra("carbs", 0);
            double protein = data.getDoubleExtra("protein", 0);
            double fat = data.getDoubleExtra("fat", 0);
            int calories = data.getIntExtra("calories", 0);
            addCustomFoodToMeal(foodId, foodName, carbs, protein, fat, calories);
        }
    }

    private void addCustomFoodToMeal(String foodId, String foodName, double carbs, double protein, double fat, int calories) {
        // Update UI
        updateMealUI(foodName, carbs, protein, fat, calories);

        // Store meal in database
        storeMeal(foodId, foodName, carbs, protein, fat, calories);
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

                    // Update UI
                    updateMealUI(foodName, carbs, protein, fat, calories);

                    // Store meal in database
                    storeMeal(foodId, foodName, carbs, protein, fat, calories);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
            }
        });
    }

    private void updateMealUI(String foodName, double carbs, double protein, double fat, int calories) {
        TextView loggedFoodTextView = null, carbsTextView = null, proteinTextView = null, fatTextView = null, caloriesTextView = null;
        switch (selectedMealType) {
            case "breakfast":
                loggedFoodTextView = binding.textViewLoggedBreakfast;
                carbsTextView = binding.textViewBreakfastCarbs;
                proteinTextView = binding.textViewBreakfastProtein;
                fatTextView = binding.textViewBreakfastFat;
                caloriesTextView = binding.textViewBreakfastCalories;
                break;
            case "lunch":
                loggedFoodTextView = binding.textViewLoggedLunch;
                carbsTextView = binding.textViewLunchCarbs;
                proteinTextView = binding.textViewLunchProtein;
                fatTextView = binding.textViewLunchFat;
                caloriesTextView = binding.textViewLunchCalories;
                break;
            case "dinner":
                loggedFoodTextView = binding.textViewLoggedDinner;
                carbsTextView = binding.textViewDinnerCarbs;
                proteinTextView = binding.textViewDinnerProtein;
                fatTextView = binding.textViewDinnerFat;
                caloriesTextView = binding.textViewDinnerCalories;
                break;
            case "snack":
                loggedFoodTextView = binding.textViewLoggedSnacks;
                carbsTextView = binding.textViewSnacksCarbs;
                proteinTextView = binding.textViewSnacksProtein;
                fatTextView = binding.textViewSnacksFat;
                caloriesTextView = binding.textViewSnacksCalories;
                break;
        }

        if (loggedFoodTextView != null) {
            String currentText = loggedFoodTextView.getText().toString();
            if (currentText.equals("Logged food: None")) {
                loggedFoodTextView.setText(foodName);
            } else {
                loggedFoodTextView.setText(currentText + "\n" + foodName);
            }

            double currentCarbs = extractNumericValue(carbsTextView.getText().toString());
            double currentProtein = extractNumericValue(proteinTextView.getText().toString());
            double currentFat = extractNumericValue(fatTextView.getText().toString());
            int currentCalories = Integer.parseInt(caloriesTextView.getText().toString().split(" ")[0]);

            carbsTextView.setText(String.format("%.1f g", roundToSingleDecimal(currentCarbs + carbs)));
            proteinTextView.setText(String.format("%.1f g", roundToSingleDecimal(currentProtein + protein)));
            fatTextView.setText(String.format("%.1f g", roundToSingleDecimal(currentFat + fat)));
            caloriesTextView.setText(String.format("%d kcal", currentCalories + calories));
        }

        updateProgressBar(calories, carbs, protein, fat);
    }

    private void storeMeal(String foodId, String foodName, double carbs, double protein, double fat, int calories) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());
            DatabaseReference mealRef = mDatabase.child("DailyLogs").child(date).child(userId).child(selectedMealType).push();
            mealRef.setValue(new Meal(foodId, foodName, carbs, protein, fat, calories));
        }
    }

    private void loadMealsForDate(long dateInMillis) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(dateInMillis);
            DatabaseReference mealsRef = mDatabase.child("DailyLogs").child(date).child(userId);

            mealsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    clearMeals();
                    for (DataSnapshot mealTypeSnapshot : snapshot.getChildren()) {
                        String mealType = mealTypeSnapshot.getKey();
                        ArrayList<String> foodNames = new ArrayList<>();
                        double totalCarbs = 0, totalProtein = 0, totalFat = 0;
                        int totalCalories = 0;
                        for (DataSnapshot mealSnapshot : mealTypeSnapshot.getChildren()) {
                            String foodName = mealSnapshot.child("foodName").getValue(String.class);
                            double carbs = mealSnapshot.child("carbs").getValue(Double.class);
                            double protein = mealSnapshot.child("protein").getValue(Double.class);
                            double fat = mealSnapshot.child("fat").getValue(Double.class);
                            int calories = mealSnapshot.child("calories").getValue(Integer.class);

                            foodNames.add(foodName);
                            totalCarbs += carbs;
                            totalProtein += protein;
                            totalFat += fat;
                            totalCalories += calories;
                        }

                        selectedMealType = mealType;
                        updateMealUI(String.join("\n", foodNames), totalCarbs, totalProtein, totalFat, totalCalories);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle possible errors
                }
            });
        }
    }

    private void clearMeals() {
        binding.textViewLoggedBreakfast.setText("Logged food: None");
        binding.textViewBreakfastCarbs.setText("0 / " + maxCarbs);
        binding.textViewBreakfastProtein.setText("0 / " + maxProtein);
        binding.textViewBreakfastFat.setText("0 / " + maxFat);
        binding.textViewBreakfastCalories.setText("0 / " + maxCalories);

        binding.textViewLoggedLunch.setText("Logged food: None");
        binding.textViewLunchCarbs.setText("0 / " + maxCarbs);
        binding.textViewLunchProtein.setText("0 / " + maxProtein);
        binding.textViewLunchFat.setText("0 / " + maxFat);
        binding.textViewLunchCalories.setText("0 / " + maxCalories);

        binding.textViewLoggedDinner.setText("Logged food: None");
        binding.textViewDinnerCarbs.setText("0 / " + maxCarbs);
        binding.textViewDinnerProtein.setText("0 / " + maxProtein);
        binding.textViewDinnerFat.setText("0 / " + maxFat);
        binding.textViewDinnerCalories.setText("0 / " + maxCalories);

        binding.textViewLoggedSnacks.setText("Logged food: None");
        binding.textViewSnacksCarbs.setText("0 / " + maxCarbs);
        binding.textViewSnacksProtein.setText("0 / " + maxProtein);
        binding.textViewSnacksFat.setText("0 / " + maxFat);
        binding.textViewSnacksCalories.setText("0 / " + maxCalories);

        binding.progressBar.setProgress(0);
        binding.textViewCalorieIntake.setText("0 / " + maxCalories + " kcal");
        binding.textViewCarbs.setText("0 / " + maxCarbs + " g");
        binding.textViewProtein.setText("0 / " + maxProtein + " g");
        binding.textViewFat.setText("0 / " + maxFat + " g");
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

                maxCalories = Integer.parseInt(calories);
                maxCarbs = Double.parseDouble(carbs);
                maxFat = Double.parseDouble(fat);
                maxProtein = Double.parseDouble(protein);

                binding.textViewCalorieIntake.setText("0 / " + maxCalories + " kcal");
                binding.textViewCarbs.setText("0 / " + maxCarbs + " g");
                binding.textViewFat.setText("0 / " + maxFat + " g");
                binding.textViewProtein.setText("0 / " + maxProtein + " g");
                binding.progressBar.setMax(maxCalories);

                // Load meals for today
                loadMealsForDate(calendar.getTimeInMillis());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
            }
        });
    }

    private void setupGeneratePieChartButton() {
        binding.buttonGeneratePieChart.setOnClickListener(v -> {
            double totalCarbs = extractNumericValue(binding.textViewCarbs.getText().toString());
            double totalProtein = extractNumericValue(binding.textViewProtein.getText().toString());
            double totalFat = extractNumericValue(binding.textViewFat.getText().toString());

            Intent intent = new Intent(getActivity(), NutritionStatisticsActivity.class);
            intent.putExtra("carbs", totalCarbs);
            intent.putExtra("protein", totalProtein);
            intent.putExtra("fat", totalFat);
            startActivity(intent);
        });
    }
}
