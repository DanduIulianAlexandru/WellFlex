package dudu.nutrifitapp.ui.options;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dudu.nutrifitapp.R;
import dudu.nutrifitapp.model.RecipeListAdapter;

public class MyRecipesActivity extends AppCompatActivity {

    private ListView listView;
    private RecipeListAdapter adapter;
    private List<String> recipeNames;
    private List<String> recipeIds;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.options_my_recipes);

        listView = findViewById(R.id.listView);
        recipeNames = new ArrayList<>();
        recipeIds = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        ((TextView) findViewById(R.id.titleTextView)).setText("My Recipes");

        adapter = new RecipeListAdapter(this, recipeNames, recipeIds, this::deleteRecipe);
        listView.setAdapter(adapter);

        loadRecipes();
    }

    private void loadRecipes() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference recipesRef = mDatabase.child("Recipes").child(userId);

            recipesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    recipeNames.clear();
                    recipeIds.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String recipeId = snapshot.getKey();
                        String recipeName = snapshot.child("foodName").getValue(String.class);

                        if (recipeId != null && recipeName != null) {
                            recipeIds.add(recipeId);
                            recipeNames.add(recipeName);
                        }
                    }

                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(MyRecipesActivity.this, "Failed to load recipes.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void deleteRecipe(int position) {
        if (position >= 0 && position < recipeIds.size()) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                String userId = currentUser.getUid();
                String recipeId = recipeIds.get(position);

                DatabaseReference recipeRef = mDatabase.child("Recipes").child(userId).child(recipeId);
                recipeRef.removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(MyRecipesActivity.this, "Recipe deleted successfully", Toast.LENGTH_SHORT).show();
                        loadRecipes();  // Reload the recipes from the database
                    } else {
                        Toast.makeText(MyRecipesActivity.this, "Failed to delete recipe", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            Toast.makeText(this, "Invalid position: " + position, Toast.LENGTH_SHORT).show();
        }
    }
}
