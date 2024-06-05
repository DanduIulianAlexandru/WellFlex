package dudu.nutrifitapp.ui.nutrition;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import dudu.nutrifitapp.databinding.FragmentNutritionBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NutritionFragment extends Fragment {

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

        return view;
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
