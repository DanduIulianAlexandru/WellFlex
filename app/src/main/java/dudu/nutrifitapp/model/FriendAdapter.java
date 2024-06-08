package dudu.nutrifitapp.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import dudu.nutrifitapp.R;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {

    private List<String> friendIds;
    private Context context;
    private DatabaseReference userFriendsRef;
    private DatabaseReference usersRef;
    private String currentUserId;

    public FriendAdapter(List<String> friendIds, Context context, String currentUserId) {
        this.friendIds = friendIds;
        this.context = context;
        this.currentUserId = currentUserId;
        userFriendsRef = FirebaseDatabase.getInstance().getReference("UserFriends").child(currentUserId).child("friends");
        usersRef = FirebaseDatabase.getInstance().getReference("User");
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.options_friend_item, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        String friendId = friendIds.get(position);

        // Fetch user details from the database
        usersRef.child(friendId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User friend = snapshot.getValue(User.class);
                if (friend != null) {
                    holder.textViewUserName.setText(friend.getSocialProfile().getName());

                    String profilePictureUrl = friend.getSocialProfile().getProfilePictureUrl();
                    if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
                        StorageReference profilePicRef = FirebaseStorage.getInstance().getReference(profilePictureUrl);
                        profilePicRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            Glide.with(context)
                                    .load(uri)
                                    .placeholder(R.drawable.profile_pic)
                                    .into(holder.imageViewProfilePicture);
                        });
                    }

                    holder.buttonRemoveFriend.setOnClickListener(v -> {
                        removeFriend(friendId, position);
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Error fetching user details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeFriend(String friendId, int position) {
        userFriendsRef.child(friendId).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DatabaseReference friendUserRef = FirebaseDatabase.getInstance().getReference("UserFriends").child(friendId).child("friends");
                friendUserRef.child(currentUserId).removeValue().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        friendIds.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, friendIds.size());
                        Toast.makeText(context, "Friend removed", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Error removing friend", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(context, "Error removing friend", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendIds.size();
    }

    public static class FriendViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewProfilePicture;
        TextView textViewUserName;
        Button buttonRemoveFriend;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewProfilePicture = itemView.findViewById(R.id.imageViewProfilePicture);
            textViewUserName = itemView.findViewById(R.id.textViewUserName);
            buttonRemoveFriend = itemView.findViewById(R.id.buttonRemoveFriend);
        }
    }
}
