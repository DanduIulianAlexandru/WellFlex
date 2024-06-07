package dudu.nutrifitapp.ui.options;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.concurrent.TimeUnit;

import dudu.nutrifitapp.Activities.LogInActivity;
import dudu.nutrifitapp.R;

public class OptionsFragment extends Fragment {

    private ImageView profilePicture;
    private TextView userName, streak;
    private DatabaseReference userDatabase;
    private FirebaseUser currentUser;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_options, container, false);

        profilePicture = root.findViewById(R.id.profile_picture);
        userName = root.findViewById(R.id.user_name);
        streak = root.findViewById(R.id.streak);

        Button btnProfile = root.findViewById(R.id.btn_profile);
        Button btnCalculateMacros = root.findViewById(R.id.btn_calculate_macros);
        Button btnProgressNutrition = root.findViewById(R.id.btn_progress_nutrition);
        Button btnProgressFitness = root.findViewById(R.id.btn_progress_fitness);
        Button btnMealsRecipesFood = root.findViewById(R.id.btn_meals_recipes_food);
        Button btnFriends = root.findViewById(R.id.btn_friends);
        Button btnLogout = root.findViewById(R.id.btn_logout);

        // Get the currently logged-in user
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            userDatabase = FirebaseDatabase.getInstance().getReference().child("User").child(userId).child("socialProfile");
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        long creationTimestamp = user.getMetadata().getCreationTimestamp();

                        // Calculate the number of days since the account was created
                        long currentTime = System.currentTimeMillis();
                        long diffInMillis = currentTime - creationTimestamp;
                        long daysSinceCreation = TimeUnit.MILLISECONDS.toDays(diffInMillis);
                        String streakString = "Streak: " + String.valueOf(daysSinceCreation) + " days \uD83D\uDD25";
                        String username = dataSnapshot.child("username").getValue(String.class);
                        String profilePicUrl = dataSnapshot.child("profilePictureUrl").getValue(String.class);

                        userName.setText(username);
                        streak.setText(streakString);

                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference storageRef = storage.getReference();

                        StorageReference imageRef = storageRef.child(profilePicUrl);
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Check if fragment is still attached
                            if (isAdded() && getActivity() != null) {
                                Glide.with(OptionsFragment.this)
                                        .load(uri)
                                        .apply(RequestOptions.circleCropTransform())
                                        .into(profilePicture);
                            }
                        }).addOnFailureListener(exception -> {
                            // Handle any errors
                            Toast.makeText(getActivity(), "Error getting download URL", Toast.LENGTH_SHORT).show();
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle potential errors
                }
            });
        }

        // Set up button click listeners
        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), OptionsProfileActivity.class);
            startActivity(intent);
        });

        btnCalculateMacros.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), OptionsCalculateCaloriesMacro.class);
            startActivity(intent);
        });

        btnProgressNutrition.setOnClickListener(v -> {
            // Navigate to Progress Nutrition Fragment or Activity
        });

        btnProgressFitness.setOnClickListener(v -> {
            // Navigate to Progress Fitness Fragment or Activity
        });

        btnMealsRecipesFood.setOnClickListener(v -> {
            // Navigate to Meals, Recipes & Food Fragment or Activity
        });

        btnFriends.setOnClickListener(v -> {
            // Navigate to Friends Fragment or Activity
        });

        btnLogout.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Logged Out!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), LogInActivity.class);
            startActivity(intent);
            FirebaseAuth.getInstance().signOut();
        });

        return root;
    }
}
