package dudu.nutrifitapp.ui.options;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import dudu.nutrifitapp.R;

public class OptionsCalculateCaloriesMacro extends AppCompatActivity {
    private EditText ageField, heightField, weightField, objectiveField, sexField;
    private EditText caloriesField, proteinField, fatField, carbField;
    private Button fetchDataButton, calculateButton, saveDataButton;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    private Spinner activityLevelSpinner, goalSpinner;
    private String selectedActivityLevel, selectedGoal;
    private String activity;
    private String goal;

    int age;
    float height, weight, objective;
    String sex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.options_calculate_calories_macro);

        ageField = findViewById(R.id.ageField);
        heightField = findViewById(R.id.heightField);
        weightField = findViewById(R.id.weightField);
        objectiveField = findViewById(R.id.objectiveField);
        sexField = findViewById(R.id.sexField);

        caloriesField = findViewById(R.id.caloriesField);
        proteinField = findViewById(R.id.proteinField);
        fatField = findViewById(R.id.fatField);
        carbField = findViewById(R.id.carbField);

        activityLevelSpinner = findViewById(R.id.activityLevelSpinner);
        goalSpinner = findViewById(R.id.goalSpinner);
        fetchDataButton = findViewById(R.id.fetchDataButton);
        calculateButton = findViewById(R.id.calculateButton);
        saveDataButton = findViewById(R.id.saveDataButton);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("User").child(userId).child("nutritiveProfile");


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.activity_levels, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activityLevelSpinner.setAdapter(adapter);
        activityLevelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedActivityLevel = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedActivityLevel = "Moderate activity (3–5 exercise per week)";
            }
        });

        // Populate the goal spinner
        ArrayAdapter<CharSequence> goalAdapter = ArrayAdapter.createFromResource(this,
                R.array.goal_options, android.R.layout.simple_spinner_item);
        goalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        goalSpinner.setAdapter(goalAdapter);
        goalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGoal = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedGoal = "Lose Weight";
            }
        });
        fetchDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchDataFromDatabase();
            }
        });
        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculate();
            }
        });
        saveDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveResultsToDatabase();
            }
        });
    }

    private void fetchDataFromDatabase() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    age = snapshot.child("age").getValue(Integer.class);
                    height = snapshot.child("height").getValue(Float.class);
                    weight = snapshot.child("weight").getValue(Float.class);
                    objective = snapshot.child("objective").getValue(Float.class);
                    sex = snapshot.child("sex").getValue(String.class);

                    ageField.setText(String.format("Age: %s years old", String.valueOf(age)));
                    heightField.setText(String.format("Height: %s cm", String.valueOf(height)));
                    weightField.setText(String.format("Weight: %s kg", String.valueOf(weight)));
                    objectiveField.setText(String.format("Objective: %s kg ", String.valueOf(objective)));
                    sexField.setText(String.format("Sex of the user: %s", sex));
                } else {
                    Toast.makeText(OptionsCalculateCaloriesMacro.this, "User data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OptionsCalculateCaloriesMacro.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calculate(){
        try{
        double bmr;
        if (sex.equalsIgnoreCase("Man")) {
            bmr = 66.5  + (13.75  * weight) + (5.003  * height) - (6.75 * age);
        } else {
            bmr = 655.1  + (9.563  * weight) + (1.850  * height) - (4.676  * age);
        }

        // Determine activity factor
        double activityFactor;
        switch (selectedActivityLevel) {
            case "Sedentary(little or no exercise)":
                activityFactor = 1.2;
                activity = "Sedentary";
                break;
            case "Lightly Active (1–3 exercise per week)":
                activityFactor = 1.375;
                activity = "Lightly Active";
                break;
            case "Moderately active (3–5 exercise per week)":
                activityFactor = 1.55;
                activity = "Moderately active";
                break;
            case "Very active (6–7 exercise per week)":
                activityFactor = 1.725;
                activity = "Very Active";
                break;
            case "Very high activity (physical job, intense exercise)":
                activityFactor = 1.9;
                activity = "Very High Activity";
                break;
            default:
                activityFactor = 1.55; // Default to low activity
        }

        double tdee = bmr * activityFactor;
        double caloriesObjective;
        if (selectedGoal.equals("Lose weight")) {
            goal = "Lose Weight";
            caloriesObjective = tdee - 500 * objective;
        } else {
            goal = "Gain Weight";
            caloriesObjective = tdee + 500 * objective;
        }
        double proteinCalories = caloriesObjective * 0.3; // 30% of calories from protein
        double fatCalories = caloriesObjective * 0.25; // 25% of calories from fat
        double carbCalories = caloriesObjective * 0.45; // 45% of calories from carbs

        double proteinGrams = proteinCalories / 4;
        double fatGrams = fatCalories / 9;
        double carbGrams = carbCalories / 4;

        caloriesField.setText("Number of Calories per day: " + Math.round(caloriesObjective));
        proteinField.setText("Protein (g): " + Math.round(proteinGrams));
        fatField.setText("Fat (g): " + Math.round(fatGrams));
        carbField.setText("Carbohydrates (g): " + Math.round(carbGrams));
        } catch (NumberFormatException e) {
            Toast.makeText(OptionsCalculateCaloriesMacro.this, "Please enter valid data in all fields", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveResultsToDatabase() {
        try {
            String calories = caloriesField.getText().toString().split(": ")[1];
            String protein = proteinField.getText().toString().split(": ")[1];
            String fat = fatField.getText().toString().split(": ")[1];
            String carbs = carbField.getText().toString().split(": ")[1];

            databaseReference.child("Calories").setValue(calories);
            databaseReference.child("Protein").setValue(protein);
            databaseReference.child("Fat").setValue(fat);
            databaseReference.child("Carbs").setValue(carbs);
            databaseReference.child("Activity Level").setValue(activity);
            databaseReference.child("Goal").setValue(goal);


            Toast.makeText(OptionsCalculateCaloriesMacro.this, "Results saved to database", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(OptionsCalculateCaloriesMacro.this, "Error saving results", Toast.LENGTH_SHORT).show();
        }
    }
}