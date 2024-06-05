package dudu.nutrifitapp.ui.nutrition;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
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

    private EditText searchBar;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> foodNames;
    private List<String> foodIds;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nutrition_search_food);

        searchBar = findViewById(R.id.search_bar);
        listView = findViewById(R.id.list_view);
        foodNames = new ArrayList<>();
        foodIds = new ArrayList<>();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Food");

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, foodNames);
        listView.setAdapter(adapter);

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchFood(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("foodId", foodIds.get(position));
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    private void searchFood(String query) {
        mDatabase.orderByChild("Name").startAt(query).endAt(query + "\uf8ff")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        foodNames.clear();
                        foodIds.clear();
                        for (DataSnapshot foodSnapshot : snapshot.getChildren()) {
                            String foodId = foodSnapshot.getKey();
                            String foodName = foodSnapshot.child("Name").getValue(String.class);
                            foodNames.add(foodName);
                            foodIds.add(foodId);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle possible errors
                    }
                });
    }
}
