package dudu.nutrifitapp.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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

import de.hdodenhof.circleimageview.CircleImageView;
import dudu.nutrifitapp.R;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private List<Post> postList;
    private Context context;
    private String currentUserId;
    private StorageReference storageReference;
    private DatabaseReference usersRef, postsRef;

    public PostAdapter(List<Post> postList, Context context, String currentUserId) {
        this.postList = postList;
        this.context = context;
        this.currentUserId = currentUserId;
        this.storageReference = FirebaseStorage.getInstance().getReference();
        this.usersRef = FirebaseDatabase.getInstance().getReference("User");
        this.postsRef = FirebaseDatabase.getInstance().getReference("Post");
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.social_item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    class PostViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profileImageView;
        TextView textViewUserName, textViewContent;
        ImageView imageViewPost;
        ImageButton buttonLike, buttonDislike;
        TextView textViewLikeCount, textViewDislikeCount;

        PostViewHolder(View itemView) {
            super(itemView);
            profileImageView = itemView.findViewById(R.id.profileImageView);
            textViewUserName = itemView.findViewById(R.id.textViewUserName);
            textViewContent = itemView.findViewById(R.id.textViewContent);
            imageViewPost = itemView.findViewById(R.id.imageViewPost);
            buttonLike = itemView.findViewById(R.id.buttonLike);
            buttonDislike = itemView.findViewById(R.id.buttonDislike);
            textViewLikeCount = itemView.findViewById(R.id.textViewLikeCount);
            textViewDislikeCount = itemView.findViewById(R.id.textViewDislikeCount);
        }

        void bind(Post post) {
            textViewUserName.setText(post.getUserName());
            textViewContent.setText(post.getContent());

            if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
                imageViewPost.setVisibility(View.VISIBLE);
                StorageReference postImageRef = storageReference.child(post.getImageUrl());
                postImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    Glide.with(context).load(uri).into(imageViewPost);
                }).addOnFailureListener(exception -> {
                    imageViewPost.setVisibility(View.GONE);
                });
            } else {
                imageViewPost.setVisibility(View.GONE);
            }

            usersRef.child(post.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String profilePictureUrl = snapshot.child("socialProfile").child("profilePictureUrl").getValue(String.class);
                    if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
                        StorageReference profilePicRef = storageReference.child(profilePictureUrl);
                        profilePicRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            Glide.with(context).load(uri).into(profileImageView);
                        }).addOnFailureListener(exception -> {
                            // Set default image or handle error
                            profileImageView.setImageResource(R.drawable.profile_pic);
                            // Log the error
                        });
                    } else {
                        profileImageView.setImageResource(R.drawable.profile_pic);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error
                }
            });

            updateLikeDislikeButtons(post);

            buttonLike.setOnClickListener(view -> {
                if (post.getPostId() != null) {
                    boolean liked = post.getLikes() != null && post.getLikes().getOrDefault(currentUserId, false);
                    DatabaseReference postRef = postsRef.child(post.getPostId()).child("likes").child(currentUserId);
                    postRef.setValue(!liked).addOnSuccessListener(aVoid -> {
                        post.getLikes().put(currentUserId, !liked);
                        updateLikeDislikeButtons(post);
                        notifyItemChanged(getAdapterPosition());
                    });
                }
            });

            buttonDislike.setOnClickListener(view -> {
                if (post.getPostId() != null) {
                    boolean disliked = post.getLikes() != null && post.getLikes().getOrDefault(currentUserId, true);
                    DatabaseReference postRef = postsRef.child(post.getPostId()).child("likes").child(currentUserId);
                    postRef.setValue(!disliked).addOnSuccessListener(aVoid -> {
                        post.getLikes().put(currentUserId, !disliked);
                        updateLikeDislikeButtons(post);
                        notifyItemChanged(getAdapterPosition());
                    });
                }
            });

            if (post.getLikes() != null) {
                int likeCount = 0;
                int dislikeCount = 0;
                for (Boolean value : post.getLikes().values()) {
                    if (value) {
                        likeCount++;
                    } else {
                        dislikeCount++;
                    }
                }
                textViewLikeCount.setText(String.valueOf(likeCount));
                textViewDislikeCount.setText(String.valueOf(dislikeCount));
            }
        }

        private void updateLikeDislikeButtons(Post post) {
            if (post.getLikes() != null && post.getLikes().containsKey(currentUserId)) {
                boolean liked = post.getLikes().get(currentUserId);
                if (liked) {
                    buttonLike.setImageResource(R.drawable.full_like);
                    buttonDislike.setImageResource(R.drawable.empty_dislike);
                } else {
                    buttonLike.setImageResource(R.drawable.empty_like);
                    buttonDislike.setImageResource(R.drawable.full_dislike);
                }
            } else {
                buttonLike.setImageResource(R.drawable.empty_like);
                buttonDislike.setImageResource(R.drawable.empty_dislike);
            }
        }
    }
}
