package dudu.nutrifitapp.ui.social;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import dudu.nutrifitapp.R;

public class SocialUserDetailActivity extends AppCompatActivity {

    private DatabaseReference userFriendsRef;
    private DatabaseReference userRef;
    private StorageReference storageReference;
    private String currentUserId;
    private String viewedUserId;

    private Button buttonAddFriend;
    private Button buttonRemoveFriend;
    private TextView textViewUsername;
    private TextView textViewEmail;
    private TextView textViewBiography;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.social_user_detail);

        ImageButton buttonBack = findViewById(R.id.buttonBack);
        TextView textViewUserNameDetail = findViewById(R.id.textViewUserNameDetail);
        ImageView imageViewProfilePictureDetail = findViewById(R.id.imageViewProfilePictureDetail);
        buttonAddFriend = findViewById(R.id.buttonAddFriend);
        buttonRemoveFriend = findViewById(R.id.buttonRemoveFriend);
        textViewUsername = findViewById(R.id.textViewUsername);
        textViewEmail = findViewById(R.id.textViewEmail);
        textViewBiography = findViewById(R.id.textViewBiography);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();

        Intent intent = getIntent();
        viewedUserId = intent.getStringExtra("USER_ID");
        String userName = intent.getStringExtra("USER_NAME");

        textViewUserNameDetail.setText(userName);

        userRef = FirebaseDatabase.getInstance().getReference("User").child(viewedUserId);
        userFriendsRef = FirebaseDatabase.getInstance().getReference("UserFriends");

        loadProfilePicture(imageViewProfilePictureDetail);
        loadUserProfile();
        setupFriendButtons();

        buttonBack.setOnClickListener(v -> onBackPressed());
    }

    private void loadProfilePicture(ImageView imageView) {
        userRef.child("socialProfile").child("profilePictureUrl").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String profilePictureUrl = snapshot.getValue(String.class);
                if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
                    StorageReference profilePicRef = storageReference.child(profilePictureUrl);
                    profilePicRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        Glide.with(SocialUserDetailActivity.this).load(uri).into(imageView);
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void loadUserProfile() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String username = snapshot.child("socialProfile").child("username").getValue(String.class);
                String email = snapshot.child("email").getValue(String.class);
                String biography = snapshot.child("socialProfile").child("biography").getValue(String.class);

                textViewUsername.setText("Username: " + username);
                textViewEmail.setText("Email: " + email);
                textViewBiography.setText("Biography: " + biography);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void setupFriendButtons() {
        DatabaseReference currentUserFriendsRef = userFriendsRef.child(currentUserId).child("friends");

        currentUserFriendsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean areFriends = snapshot.hasChild(viewedUserId) && Boolean.TRUE.equals(snapshot.child(viewedUserId).getValue(Boolean.class));

                buttonAddFriend.setEnabled(!areFriends);
                buttonRemoveFriend.setEnabled(areFriends);

                buttonAddFriend.setOnClickListener(v -> {
                    if (areFriends) {
                        Toast.makeText(SocialUserDetailActivity.this, "You are already friends", Toast.LENGTH_SHORT).show();
                    } else {
                        addFriend();
                    }
                });

                buttonRemoveFriend.setOnClickListener(v -> {
                    if (!areFriends) {
                        Toast.makeText(SocialUserDetailActivity.this, "You are not friends", Toast.LENGTH_SHORT).show();
                    } else {
                        removeFriend();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void addFriend() {
        DatabaseReference currentUserFriendsRef = userFriendsRef.child(currentUserId).child("friends");
        DatabaseReference viewedUserFriendsRef = userFriendsRef.child(viewedUserId).child("friends");

        currentUserFriendsRef.child(viewedUserId).setValue(true).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                viewedUserFriendsRef.child(currentUserId).setValue(true).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        Toast.makeText(this, "Friend added", Toast.LENGTH_SHORT).show();
                        refreshButtons();
                    }
                });
            }
        });
    }

    private void removeFriend() {
        DatabaseReference currentUserFriendsRef = userFriendsRef.child(currentUserId).child("friends");
        DatabaseReference viewedUserFriendsRef = userFriendsRef.child(viewedUserId).child("friends");

        currentUserFriendsRef.child(viewedUserId).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                viewedUserFriendsRef.child(currentUserId).removeValue().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        Toast.makeText(this, "Friend removed", Toast.LENGTH_SHORT).show();
                        refreshButtons();
                    }
                });
            }
        });
    }

    private void refreshButtons() {
        DatabaseReference currentUserFriendsRef = userFriendsRef.child(currentUserId).child("friends");

        currentUserFriendsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean areFriends = snapshot.hasChild(viewedUserId) && Boolean.TRUE.equals(snapshot.child(viewedUserId).getValue(Boolean.class));

                buttonAddFriend.setEnabled(!areFriends);
                buttonRemoveFriend.setEnabled(areFriends);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }
}
