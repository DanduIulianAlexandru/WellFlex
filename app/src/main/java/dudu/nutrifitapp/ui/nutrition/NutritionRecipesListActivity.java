package dudu.nutrifitapp.ui.nutrition;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dudu.nutrifitapp.R;
import dudu.nutrifitapp.model.Meal;

public class NutritionRecipesListActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> recipeList;
    private List<Meal> meals;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nutrition_recipes_list);

        listView = findViewById(R.id.listViewCustomRecipes);
        recipeList = new ArrayList<>();
        meals = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, recipeList);
        listView.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        loadCustomRecipes();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Meal selectedMeal = meals.get(position);
                Intent resultIntent = new Intent();
                resultIntent.putExtra("foodId", "custom_" + selectedMeal.getFoodId());
                resultIntent.putExtra("foodName", selectedMeal.getFoodName());
                resultIntent.putExtra("carbs", selectedMeal.getCarbs());
                resultIntent.putExtra("protein", selectedMeal.getProtein());
                resultIntent.putExtra("fat", selectedMeal.getFat());
                resultIntent.putExtra("calories", selectedMeal.getCalories());
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }

    private void loadCustomRecipes() {
        String userId = mAuth.getCurrentUser().getUid();
        mDatabase.child("Recipes").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recipeList.clear();
                meals.clear();
                for (DataSnapshot recipeSnapshot : snapshot.getChildren()) {
                    Meal meal = recipeSnapshot.getValue(Meal.class);
                    if (meal != null) {
                        recipeList.add(meal.getFoodName());
                        meals.add(meal);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(NutritionRecipesListActivity.this, "Failed to load recipes", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
