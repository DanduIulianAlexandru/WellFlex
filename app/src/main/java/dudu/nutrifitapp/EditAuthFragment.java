package dudu.nutrifitapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditAuthFragment extends Fragment {

    private EditText editEmail, editPassword;
    private Button buttonSaveAuth;
    private DatabaseReference userReference;

    public EditAuthFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_auth, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbarEditAuth);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigateUp();
            }
        });

        editEmail = view.findViewById(R.id.edit_email);
        editPassword = view.findViewById(R.id.edit_password);
        buttonSaveAuth = view.findViewById(R.id.button_save_auth);

        userReference = FirebaseDatabase.getInstance().getReference("User").child("x7ElvtDsuuQ7G7cS1nFl5Zz7P143"); // Use the actual user ID

        buttonSaveAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAuthDetails();
            }
        });

        return view;
    }

    private void saveAuthDetails() {
        String email = editEmail.getText().toString();
        String password = editPassword.getText().toString();

        if (!email.isEmpty() && !password.isEmpty()) {
            userReference.child("email").setValue(email);
            userReference.child("password").setValue(password);
            Toast.makeText(getActivity(), "Authentication Details Updated", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "All fields are required", Toast.LENGTH_SHORT).show();
        }
    }
}
