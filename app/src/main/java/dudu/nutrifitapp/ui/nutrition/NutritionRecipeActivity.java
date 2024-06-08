package dudu.nutrifitapp.ui.nutrition;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import dudu.nutrifitapp.R;
import dudu.nutrifitapp.model.Meal;

public class NutritionRecipeActivity extends AppCompatActivity {

    private EditText editTextRecipeName, editTextCarbs, editTextProtein, editTextFat, editTextCalories, editTextDescription;
    private Button buttonSaveRecipe;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nutrition_recipe);

        editTextRecipeName = findViewById(R.id.editTextRecipeName);
        editTextCarbs = findViewById(R.id.editTextCarbs);
        editTextProtein = findViewById(R.id.editTextProtein);
        editTextFat = findViewById(R.id.editTextFat);
        editTextCalories = findViewById(R.id.editTextCalories);
        editTextDescription = findViewById(R.id.editTextDescription);
        buttonSaveRecipe = findViewById(R.id.buttonSaveRecipe);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        buttonSaveRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveRecipe();
            }
        });

        // Back button functionality
        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NutritionRecipeActivity.this, SearchFoodActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void saveRecipe() {
        String recipeName = editTextRecipeName.getText().toString().trim();
        double carbs = Double.parseDouble(editTextCarbs.getText().toString().trim());
        double protein = Double.parseDouble(editTextProtein.getText().toString().trim());
        double fat = Double.parseDouble(editTextFat.getText().toString().trim());
        int calories = Integer.parseInt(editTextCalories.getText().toString().trim());
        String description = editTextDescription.getText().toString().trim();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference recipeRef = mDatabase.child("Recipes").child(userId).push();
            String recipeId = recipeRef.getKey();
            Meal recipe = new Meal("custom_" + recipeId, recipeName, carbs, protein, fat, calories, description);
            recipeRef.setValue(recipe);

            Toast.makeText(NutritionRecipeActivity.this, "Recipe created successfully", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
