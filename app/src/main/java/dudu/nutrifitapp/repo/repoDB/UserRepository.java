package dudu.nutrifitapp.repo.repoDB;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import dudu.nutrifitapp.model.User;
import dudu.nutrifitapp.repo.RepoInterface;

public class UserRepository implements RepoInterface<User, String>{
    private DatabaseReference databaseReference;

    public UserRepository() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("User");
    }

    @Override
    public void add(User user, String userId, final DataStatus dataStatus) {
        databaseReference.child(userId).setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                dataStatus.onSuccess(user);
            } else {
                dataStatus.onError(task.getException());
            }
        });
    }

    @Override
    public void getOne(String userId, DataStatus dataStatus) {
        databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                dataStatus.onSuccess(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                dataStatus.onError(databaseError.toException());
            }
        });
    }

    @Override
    public void update(User user, String userId, DataStatus dataStatus) {
        databaseReference.child(userId).setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                dataStatus.onSuccess(user);
            } else {
                dataStatus.onError(task.getException());
            }
        });
    }

    @Override
    public void delete(String userId, DataStatus dataStatus) {
        databaseReference.child(userId).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                dataStatus.onSuccess(null);
            } else {
                dataStatus.onError(task.getException());
            }
        });
    }

    @Override
    public void getAll(DataStatus dataStatus) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<User> users = new ArrayList<>();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    users.add(user);
                }
                dataStatus.onSuccess(users);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                dataStatus.onError(databaseError.toException());
            }
        });
    }
}
