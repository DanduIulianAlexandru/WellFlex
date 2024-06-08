package dudu.nutrifitapp.ui.social;

import static android.app.Activity.RESULT_OK;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dudu.nutrifitapp.R;
import dudu.nutrifitapp.databinding.FragmentSocialBinding;
import dudu.nutrifitapp.model.Post;
import dudu.nutrifitapp.model.PostAdapter;
import dudu.nutrifitapp.model.User;
import dudu.nutrifitapp.model.UserAdapter;

public class SocialFragment extends Fragment {
    private FragmentSocialBinding binding;
    private FirebaseDatabase database;
    private DatabaseReference postsRef, usersRef;
    private FirebaseAuth auth;
    private PostAdapter postAdapter;
    private UserAdapter userAdapter;
    private List<Post> postList;
    private List<User> userList;
    private String currentUserId;
    private StorageReference storageReference;
    private Uri selectedImageUri;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Dialog createPostDialog;
    private Map<String, Boolean> friendsMap = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSocialBinding.inflate(inflater, container, false);
        database = FirebaseDatabase.getInstance();
        postsRef = database.getReference("Post");
        usersRef = database.getReference("User");
        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();

        postList = new ArrayList<>();
        userList = new ArrayList<>();
        postAdapter = new PostAdapter(postList, getContext(), currentUserId);
        userAdapter = new UserAdapter(userList, getContext(), user -> openUserProfile(user));

        binding.recyclerViewPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewPosts.setAdapter(postAdapter);

        binding.recyclerViewSearchResults.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewSearchResults.setAdapter(userAdapter);

        loadProfilePicture();
        loadFriends();
        setupListeners();

        return binding.getRoot();
    }

    private void loadProfilePicture() {
        usersRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String profilePictureUrl = snapshot.child("socialProfile").child("profilePictureUrl").getValue(String.class);
                if (profilePictureUrl != null && isAdded()) {
                    StorageReference profilePicRef = storageReference.child(profilePictureUrl);
                    profilePicRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        if (isAdded()) {
                            Glide.with(SocialFragment.this)
                                    .load(uri)
                                    .into(binding.imageViewProfilePicture);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }


    private void loadFriends() {
        DatabaseReference userFriendsRef = FirebaseDatabase.getInstance().getReference("UserFriends").child(currentUserId).child("friends");
        userFriendsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot friendSnapshot : snapshot.getChildren()) {
                    if (Boolean.TRUE.equals(friendSnapshot.getValue(Boolean.class))) {
                        friendsMap.put(friendSnapshot.getKey(), true);
                    }
                }
                loadPosts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void loadPosts() {
        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    if (post != null && friendsMap.containsKey(post.getUserId())) {
                        post.setPostId(postSnapshot.getKey()); // Ensure postId is set
                        if (post.getLikes() == null) {
                            post.setLikes(new HashMap<>());
                        }
                        postList.add(post);
                    }
                }
                if (isAdded()) {
                    postAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }


    private void setupListeners() {
        binding.editTextPost.setOnClickListener(v -> openCreatePostDialog());

        binding.buttonAddImage.setOnClickListener(v -> openFileChooser());

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchUsers(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchUsers(newText);
                return true;
            }
        });

        binding.searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                binding.recyclerViewSearchResults.setVisibility(View.GONE);
            }
        });

        binding.getRoot().setOnTouchListener((v, event) -> {
            if (binding.recyclerViewSearchResults.getVisibility() == View.VISIBLE) {
                binding.recyclerViewSearchResults.setVisibility(View.GONE);
            }
            return false;
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void openCreatePostDialog() {
        createPostDialog = new Dialog(getContext());
        createPostDialog.setContentView(R.layout.social_dialog_create_post);
        createPostDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        EditText editTextPostDialog = createPostDialog.findViewById(R.id.editTextPostDialog);
        ImageButton buttonAddImageDialog = createPostDialog.findViewById(R.id.buttonAddImageDialog);
        ImageButton buttonPostDialog = createPostDialog.findViewById(R.id.buttonPostDialog);
        ImageView imageViewPostDialog = createPostDialog.findViewById(R.id.imageViewPostDialog);

        buttonAddImageDialog.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        buttonPostDialog.setOnClickListener(v -> {
            String postContent = editTextPostDialog.getText().toString().trim();
            if (postContent.isEmpty()) {
                editTextPostDialog.setError("Post content cannot be empty");
                editTextPostDialog.requestFocus();
                return;
            }
            createPost(postContent, createPostDialog);
        });

        createPostDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            if (createPostDialog != null && createPostDialog.isShowing() && isAdded()) {
                ImageView imageViewPostDialog = createPostDialog.findViewById(R.id.imageViewPostDialog);
                imageViewPostDialog.setImageURI(selectedImageUri);
                imageViewPostDialog.setVisibility(View.VISIBLE);
            }
        }
    }


    private void createPost(String content, Dialog dialog) {
        postsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int postCount = (int) snapshot.getChildrenCount();
                String postId = String.valueOf(postCount + 1);

                usersRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String username = snapshot.child("socialProfile").child("username").getValue(String.class);
                        if (username == null) {
                            Toast.makeText(getContext(), "Error retrieving username", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Post post = new Post(currentUserId, username, content, "", System.currentTimeMillis(), new HashMap<>());

                        if (selectedImageUri != null) {
                            String postImageRef = "posts/" + username + "_" + postId + ".jpg";
                            StorageReference fileReference = storageReference.child(postImageRef);

                            fileReference.putFile(selectedImageUri).addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                                post.setImageUrl(postImageRef);  // Use the same path for imageUrl
                                postsRef.child(postId).setValue(post).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        postList.add(post);
                                        postAdapter.notifyDataSetChanged();
                                        resetPostCreationUI(dialog);
                                    } else {
                                        Toast.makeText(getContext(), "Error creating post", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            })).addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Error uploading image", Toast.LENGTH_SHORT).show();
                            });
                        } else {
                            postsRef.child(postId).setValue(post).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    postList.add(post);
                                    postAdapter.notifyDataSetChanged();
                                    resetPostCreationUI(dialog);
                                } else {
                                    Toast.makeText(getContext(), "Error creating post", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error
                        Toast.makeText(getContext(), "Error retrieving user data", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                Toast.makeText(getContext(), "Error retrieving post count", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resetPostCreationUI(Dialog dialog) {
        dialog.dismiss();
        binding.editTextPost.setText("");
        selectedImageUri = null;
    }

    private void searchUsers(String query) {
        Query searchQuery = usersRef.orderByChild("socialProfile/name").startAt(query).endAt(query + "\uf8ff").limitToFirst(5);
        searchQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (user != null) {
                        userList.add(user);
                    }
                }
                userAdapter.notifyDataSetChanged();
                binding.recyclerViewSearchResults.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                Toast.makeText(getContext(), "Error searching users", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openUserProfile(User user) {
        usersRef.orderByChild("socialProfile/name").equalTo(user.getSocialProfile().getName())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            String userId = userSnapshot.getKey();
                            if (isAdded()) {
                                Intent intent = new Intent(getContext(), SocialUserDetailActivity.class);
                                intent.putExtra("USER_ID", userId);
                                intent.putExtra("USER_NAME", user.getSocialProfile().getName());
                                startActivity(intent);
                            }
                            break;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error
                        Toast.makeText(getContext(), "Error retrieving user ID", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
