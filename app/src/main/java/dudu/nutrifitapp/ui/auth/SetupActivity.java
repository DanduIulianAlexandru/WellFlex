package dudu.nutrifitapp.ui.auth;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import dudu.nutrifitapp.R;
import dudu.nutrifitapp.model.NutritiveProfile;
import dudu.nutrifitapp.model.SocialProfile;
import dudu.nutrifitapp.model.User;

public class SetupActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText etUsername, etName, etBiography;
    private EditText etAge, etHeight, etWeight, etSex, etObjective;
    private Button btnSave, btnChooseImage;
    private ImageView ivProfilePicture;

    private Uri imageUri;
    private String profilePictureUrl;

    private DatabaseReference userDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private StorageReference storageReference;
    private String userPassowrd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        etUsername = findViewById(R.id.usernameSetup);
        etName = findViewById(R.id.nameSetup);
        etBiography = findViewById(R.id.biographySetup);

        etAge = findViewById(R.id.ageSetup);
        etHeight = findViewById(R.id.heightSetup);
        etWeight = findViewById(R.id.weightSetup);
        etSex = findViewById(R.id.sexSetup);
        etObjective = findViewById(R.id.objectiveSetup);

        btnSave = findViewById(R.id.saveButtonSetup);
        btnChooseImage = findViewById(R.id.ChooseImageButtonSetup);
        ivProfilePicture = findViewById(R.id.profilePicSetup);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        Intent intent = getIntent();
        userPassowrd =intent.getStringExtra("passwordUser");
        userDatabase = FirebaseDatabase.getInstance().getReference().child("User").child(currentUser.getUid());
        storageReference = FirebaseStorage.getInstance().getReference();

        btnChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInformation();
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
            Glide.with(this).load(imageUri).into(ivProfilePicture);
        }
    }

    private void saveUserInformation() {
        String username = etUsername.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String biography = etBiography.getText().toString().trim();

        String ageStr = etAge.getText().toString().trim();
        String heightStr = etHeight.getText().toString().trim();
        String weightStr = etWeight.getText().toString().trim();
        String sex = etSex.getText().toString().trim();
        String objectiveStr = etObjective.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(name) || TextUtils.isEmpty(biography) ||
                TextUtils.isEmpty(ageStr) || TextUtils.isEmpty(heightStr) || TextUtils.isEmpty(weightStr) ||
                TextUtils.isEmpty(sex) || TextUtils.isEmpty(objectiveStr) || imageUri == null) {
            Toast.makeText(this, "Please fill all fields and select a profile picture", Toast.LENGTH_SHORT).show();
            return;
        }

        int age = Integer.parseInt(ageStr);
        int height = Integer.parseInt(heightStr);
        int weight = Integer.parseInt(weightStr);
        double objective = Double.parseDouble(objectiveStr);

        String path = username + "_profile_picture.jpg";
        final StorageReference fileReference = storageReference.child(path);
        fileReference.putFile(imageUri).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    profilePictureUrl = username + "_profile_picture.jpg";
                    saveUserToDatabase(username, name, biography, age, height, weight, sex, objective, profilePictureUrl);
                });
            } else {
                Toast.makeText(SetupActivity.this, "Failed to upload profile picture. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserToDatabase(String username, String name, String biography, int age, int height, int weight, String sex, double objective, String profilePictureUrl) {
        User user = new User(currentUser.getEmail(), userPassowrd,
                new SocialProfile(username, profilePictureUrl, name, biography),
                new NutritiveProfile(age, (float) height, (float) weight, sex, (float) objective));

        userDatabase.setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(SetupActivity.this, "Profile setup complete", Toast.LENGTH_SHORT).show();
                redirectToLoggedIn();
            } else {
                Toast.makeText(SetupActivity.this, "Failed to save information. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void redirectToLoggedIn() {
        Intent mainIntent = new Intent(SetupActivity.this, LoggedInActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
