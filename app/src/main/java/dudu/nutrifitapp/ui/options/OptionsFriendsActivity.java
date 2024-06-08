package dudu.nutrifitapp.ui.options;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dudu.nutrifitapp.R;
import dudu.nutrifitapp.model.FriendAdapter;

public class OptionsFriendsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewFriends;
    private FriendAdapter friendAdapter;
    private List<String> friendIds;

    private DatabaseReference userFriendsRef;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.options_friends);

        recyclerViewFriends = findViewById(R.id.recyclerViewFriends);
        recyclerViewFriends.setLayoutManager(new LinearLayoutManager(this));
        ImageButton buttonBack = findViewById(R.id.buttonBack);

        friendIds = new ArrayList<>();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        friendAdapter = new FriendAdapter(friendIds, this, currentUserId);
        recyclerViewFriends.setAdapter(friendAdapter);

        userFriendsRef = FirebaseDatabase.getInstance().getReference("UserFriends").child(currentUserId).child("friends");
        buttonBack.setOnClickListener(v -> finish());
        loadFriends();
    }

    private void loadFriends() {
        userFriendsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot friendSnapshot : snapshot.getChildren()) {
                    if (Boolean.TRUE.equals(friendSnapshot.getValue(Boolean.class))) {
                        String friendId = friendSnapshot.getKey();
                        if (friendId != null) {
                            friendIds.add(friendId);
                            friendAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }
}
