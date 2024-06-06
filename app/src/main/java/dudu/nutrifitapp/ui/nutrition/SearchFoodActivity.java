package dudu.nutrifitapp.ui.nutrition;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dudu.nutrifitapp.R;

public class SearchFoodActivity extends AppCompatActivity {

    private SearchView searchView;
    private ListView listView;
    private Button addFoodButton;
    private TextView selectedFoodTextView;
    private TextView categoryTextView;
    private TextView carbsTextView;
    private TextView proteinTextView;
    private TextView fatTextView;
    private TextView caloriesTextView;
    private ArrayAdapter<String> adapter;
    private List<String> foodList;
    private List<String> foodIds;
    private List<String> filteredFoodList;
    private List<String> filteredFoodIds;
    private String selectedFoodId;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nutrition_search_food);

        searchView = findViewById(R.id.searchView);
        listView = findViewById(R.id.listView);
        addFoodButton = findViewById(R.id.addFoodButton);
        selectedFoodTextView = findViewById(R.id.selectedFoodTextView);
        categoryTextView = findViewById(R.id.categoryTextView);
        carbsTextView = findViewById(R.id.carbsTextView);
        proteinTextView = findViewById(R.id.proteinTextView);
        fatTextView = findViewById(R.id.fatTextView);
        caloriesTextView = findViewById(R.id.caloriesTextView);

        databaseReference = FirebaseDatabase.getInstance().getReference("Food");

        foodList = new ArrayList<>();
        foodIds = new ArrayList<>();
        filteredFoodList = new ArrayList<>();
        filteredFoodIds = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        listView.setAdapter(adapter);

        fetchFoodData();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterResults(newText);
                return false;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedFoodId = filteredFoodIds.get(position);
                String selectedFood = filteredFoodList.get(position);
                searchView.setQuery(selectedFood, false);
                listView.setVisibility(View.GONE);
                displayFoodDetails(selectedFoodId);
            }
        });

        addFoodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedFoodId != null) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("foodId", selectedFoodId);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    Toast.makeText(SearchFoodActivity.this, "No food selected!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchFoodData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                foodList.clear();
                foodIds.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String foodName = snapshot.child("Name").getValue(String.class);
                    String foodId = snapshot.getKey();
                    if (foodName != null && foodId != null) {
                        foodList.add(foodName);
                        foodIds.add(foodId);
                    }
                }
                adapter.clear();
                adapter.addAll(foodList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SearchFoodActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterResults(String query) {
        filteredFoodList.clear();
        filteredFoodIds.clear();
        for (int i = 0; i < foodList.size(); i++) {
            if (foodList.get(i).toLowerCase().contains(query.toLowerCase())) {
                filteredFoodList.add(foodList.get(i));
                filteredFoodIds.add(foodIds.get(i));
            }
        }

        if (filteredFoodList.size() > 5) {
            filteredFoodList = filteredFoodList.subList(0, 5);
            filteredFoodIds = filteredFoodIds.subList(0, 5);
        }

        adapter.clear();
        adapter.addAll(filteredFoodList);
        adapter.notifyDataSetChanged();
        listView.setVisibility(View.VISIBLE);
    }

    private void displayFoodDetails(String foodId) {
        databaseReference.child(foodId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String foodName = snapshot.child("Name").getValue(String.class);
                    String category = snapshot.child("Category").getValue(String.class);
                    Double carbs = snapshot.child("Carbohydrates").getValue(Double.class);
                    Double protein = snapshot.child("Proteins").getValue(Double.class);
                    Double fat = snapshot.child("Fat").getValue(Double.class);
                    Integer calories = snapshot.child("Calories").getValue(Integer.class);

                    selectedFoodTextView.setText(String.format("Name: %s", foodName));
                    categoryTextView.setText(String.format("Category: %s", category));
                    carbsTextView.setText(String.format("Carbohydrates: %.1f g", carbs));
                    proteinTextView.setText(String.format("Proteins: %.1f g", protein));
                    fatTextView.setText(String.format("Fat: %.1f g", fat));
                    caloriesTextView.setText(String.format("Calories: %d kcal", calories));

                    selectedFoodTextView.setVisibility(View.VISIBLE);
                    categoryTextView.setVisibility(View.VISIBLE);
                    carbsTextView.setVisibility(View.VISIBLE);
                    proteinTextView.setVisibility(View.VISIBLE);
                    fatTextView.setVisibility(View.VISIBLE);
                    caloriesTextView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SearchFoodActivity.this, "Failed to fetch food details", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
