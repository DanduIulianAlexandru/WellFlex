package dudu.nutrifitapp.ui.options;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.florent37.expansionpanel.ExpansionHeader;
import com.github.florent37.expansionpanel.ExpansionLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import dudu.nutrifitapp.R;

public class OptionsProfileActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageButton backButton;
    private TextView titleText;
    private EditText emailOptionsProfile, passwordOptionsProfile, ageOptionsProfile,
            heightOptionsProfile, weightOptionsProfile, sexOptionsProfile,
            objectiveOptionsProfile, usernameOptionsProfile,
            nameOptionsProfile, biographyOptionsProfile;
    private ImageView profilePicOptionsProfile;
    private Button chooseImageButtonOptionsProfile;
    private ExpansionHeader headerNutritiveProfile;
    private ExpansionLayout layoutNutritiveProfileOptionsProfile;

    private ExpansionHeader headerSocialProfile;
    private ExpansionLayout layoutSocialProfileOptionsProfile;
    private Button saveButtonOptionsProfile;
    private Uri imageUri;
    private String profilePictureUrl;

    private DatabaseReference userRef;
    private FirebaseUser currentUser;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.options_profile);

        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize all views
        backButton = findViewById(R.id.backButtonOptionsProfile);
        titleText = findViewById(R.id.titleText);
        profilePicOptionsProfile = findViewById(R.id.profilePicOptionsProfile);
        chooseImageButtonOptionsProfile = findViewById(R.id.ChooseImageButtonOptionsProfile);
        emailOptionsProfile = findViewById(R.id.emailOptionsProfile);
        passwordOptionsProfile = findViewById(R.id.passwordOptionsProfile);
        headerNutritiveProfile = findViewById(R.id.headerNutritiveProfile);
        layoutNutritiveProfileOptionsProfile = findViewById(R.id.layout_NutritiveProfile_OptionsProfile);
        ageOptionsProfile = findViewById(R.id.ageOptionsProfile);
        heightOptionsProfile = findViewById(R.id.heightOptionsProfile);
        weightOptionsProfile = findViewById(R.id.weightOptionsProfile);
        sexOptionsProfile = findViewById(R.id.sexOptionsProfile);
        objectiveOptionsProfile = findViewById(R.id.objectiveOptionsProfile);
        headerSocialProfile = findViewById(R.id.headerSocialProfile);
        layoutSocialProfileOptionsProfile = findViewById(R.id.layout_SocialProfile_OptionsProfile);
        usernameOptionsProfile = findViewById(R.id.usernameOptionsProfile);
        nameOptionsProfile = findViewById(R.id.nameOptionsProfile);
        biographyOptionsProfile = findViewById(R.id.biographyOptionsProfile);
        saveButtonOptionsProfile = findViewById(R.id.saveButtonOptionsProfile);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        // Link headers to expansion layouts
        headerNutritiveProfile.setExpansionLayout(layoutNutritiveProfileOptionsProfile);
        headerSocialProfile.setExpansionLayout(layoutSocialProfileOptionsProfile);
        storageReference = FirebaseStorage.getInstance().getReference();

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            userRef = firebaseDatabase.getReference("User").child(currentUser.getUid());
        }

        userRef.child("socialProfile").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String profilePicUrl = snapshot.child("profilePictureUrl").getValue(String.class);

                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference();

                    StorageReference imageRef = storageRef.child(profilePicUrl);
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        Glide.with(OptionsProfileActivity.this)
                                .load(uri)
                                .apply(RequestOptions.circleCropTransform())
                                .into(profilePicOptionsProfile);
                    }).addOnFailureListener(exception -> {
                        // Handle any errors
                        Toast.makeText(OptionsProfileActivity.this, "Error getting download URL", Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        chooseImageButtonOptionsProfile.setOnClickListener(v -> {
            openFileChooser();
        });
        saveButtonOptionsProfile.setOnClickListener(v -> saveAll());
        backButton.setOnClickListener(v -> {
            setResult(Activity.RESULT_OK);

            // Finish the activity to return to the fragment
            finish();
        });
    }

    private void saveAll() {
        Map<String, String> fieldsMap = new HashMap<>();
        String emailOptionsProfileString = emailOptionsProfile.getText().toString();
        String passwordOptionsProfileString = passwordOptionsProfile.getText().toString();
        String ageOptionsProfileString = ageOptionsProfile.getText().toString();
        String heightOptionsProfileString = heightOptionsProfile.getText().toString();
        String weightOptionsProfileString = weightOptionsProfile.getText().toString();
        String sexOptionsProfileString = sexOptionsProfile.getText().toString();
        String objectiveOptionsProfileString = objectiveOptionsProfile.getText().toString();
        String usernameOptionsProfileString = usernameOptionsProfile.getText().toString();
        String nameOptionsProfileString = nameOptionsProfile.getText().toString();
        String biographyOptionsProfileString = biographyOptionsProfile.getText().toString();

        fieldsMap.put("email", emailOptionsProfileString);
        fieldsMap.put("password", passwordOptionsProfileString);
        fieldsMap.put("age", ageOptionsProfileString);
        fieldsMap.put("height", heightOptionsProfileString);
        fieldsMap.put("weight", weightOptionsProfileString);
        fieldsMap.put("sex", sexOptionsProfileString);
        fieldsMap.put("objective", objectiveOptionsProfileString);
        fieldsMap.put("username", usernameOptionsProfileString);
        fieldsMap.put("name", nameOptionsProfileString);
        fieldsMap.put("biography", biographyOptionsProfileString);

        for (Map.Entry<String, String> entry : fieldsMap.entrySet()) {
            if(!Objects.equals(entry.getValue(), ""))
            {
                if(Objects.equals(entry.getKey(), "email") || Objects.equals(entry.getKey(), "password")){
                    userRef.child(entry.getKey()).setValue(entry.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                                Toast.makeText(OptionsProfileActivity.this, "Modification done", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(OptionsProfileActivity.this, "Error on modification", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                if(Objects.equals(entry.getKey(), "biography") || Objects.equals(entry.getKey(), "name") || (Objects.equals(entry.getKey(), "username"))){
                    userRef.child("socialProfile").child(entry.getKey()).setValue(entry.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                            Toast.makeText(OptionsProfileActivity.this, "Modification done on Social Media Profile", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(OptionsProfileActivity.this, "Error on modification for Social Media Profile", Toast.LENGTH_SHORT).show();
                    }
                    });
                }
                else{
                    if(Objects.equals(entry.getKey(), "sex")) {
                        userRef.child("nutritiveProfile").child(entry.getKey()).setValue(entry.getValue()).addOnCompleteListener(task -> Toast.makeText(OptionsProfileActivity.this, "Modification done on nutritive Profile", Toast.LENGTH_SHORT).show());
                    }
                    if(Objects.equals(entry.getKey(), "age")){
                        int age = Integer.parseInt(entry.getValue());
                        userRef.child("nutritiveProfile").child(entry.getKey()).setValue(age).addOnCompleteListener(task -> {
                            if(task.isSuccessful())
                                Toast.makeText(OptionsProfileActivity.this, "Modification done on Nutritive Profile", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(OptionsProfileActivity.this, "Error on modification for Nutritive Profile", Toast.LENGTH_SHORT).show();
                        });
                    }
                    else{
                        float val = Float.parseFloat(entry.getValue());
                        userRef.child("nutritiveProfile").child(entry.getKey()).setValue(val).addOnCompleteListener(task -> {
                            if(task.isSuccessful())
                                Toast.makeText(OptionsProfileActivity.this, "Modification done on Nutritive Profile", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(OptionsProfileActivity.this, "Error on modification for Nutritive Profile", Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            }
        }
        savePic();
    }
    private void savePic(){
        userRef.child("socialProfile").child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String username = snapshot.getValue(String.class);
                String path = username + "_profile_picture.jpg";
                StorageReference fileReference = storageReference.child(path);

                fileReference.putFile(imageUri).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            profilePictureUrl = username + "_profile_picture.jpg";
                            Toast.makeText(OptionsProfileActivity.this, "Profile Picture changed successful", Toast.LENGTH_SHORT).show();

                        });
                    } else {
                        Toast.makeText(OptionsProfileActivity.this, "Failed to upload profile picture. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Glide.with(this).load(imageUri).into(profilePicOptionsProfile);
        }
    }
}
