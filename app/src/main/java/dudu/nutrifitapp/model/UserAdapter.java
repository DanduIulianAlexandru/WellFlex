package dudu.nutrifitapp.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import dudu.nutrifitapp.R;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<User> userList;
    private Context context;
    private OnUserClickListener onUserClickListener;

    public UserAdapter(List<User> userList, Context context, OnUserClickListener onUserClickListener) {
        this.userList = userList;
        this.context = context;
        this.onUserClickListener = onUserClickListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.social_item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUserName;
        ImageView imageViewProfile;

        UserViewHolder(View itemView) {
            super(itemView);
            textViewUserName = itemView.findViewById(R.id.textViewUserNameUser);
            imageViewProfile = itemView.findViewById(R.id.imageViewProfile);
        }

        void bind(User user) {
            textViewUserName.setText(user.getSocialProfile().getName());
            if (user.getSocialProfile().getProfilePictureUrl() != null && !user.getSocialProfile().getProfilePictureUrl().isEmpty()) {
                StorageReference profilePicRef = FirebaseStorage.getInstance().getReference(user.getSocialProfile().getProfilePictureUrl());
                profilePicRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    Glide.with(context).load(uri).into(imageViewProfile);
                });
            }
            itemView.setOnClickListener(v -> onUserClickListener.onUserClick(user));
        }
    }

    public interface OnUserClickListener {
        void onUserClick(User user);
    }
}
