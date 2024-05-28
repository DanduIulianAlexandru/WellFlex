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

import dudu.nutrifitapp.model.SocialProfile;

public class EditSocialProfileFragment extends Fragment {

    private EditText editUsername, editProfilePictureUrl, editName, editBiography;
    private Button buttonSaveSocialProfile;
    private DatabaseReference userReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_social_profile, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbarEditProfile);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigateUp();
            }
        });

        editUsername = view.findViewById(R.id.edit_username);
        editProfilePictureUrl = view.findViewById(R.id.edit_profile_picture_url);
        editName = view.findViewById(R.id.edit_name);
        editBiography = view.findViewById(R.id.edit_biography);
        buttonSaveSocialProfile = view.findViewById(R.id.button_save_social_profile);

        userReference = FirebaseDatabase.getInstance().getReference("User").child("x7ElvtDsuuQ7G7cS1nFl5Zz7P143"); // Use the actual user ID

        buttonSaveSocialProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSocialProfile();
            }
        });

        return view;
    }

    private void saveSocialProfile() {
        String username = editUsername.getText().toString();
        String profilePictureUrl = editProfilePictureUrl.getText().toString();
        String name = editName.getText().toString();
        String biography = editBiography.getText().toString();

        if (!username.isEmpty() && !profilePictureUrl.isEmpty() && !name.isEmpty() && !biography.isEmpty()) {
            SocialProfile socialProfile = new SocialProfile(username, profilePictureUrl, name, biography);
            userReference.child("socialProfile").setValue(socialProfile);
            Toast.makeText(getActivity(), "Social Profile Updated", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "All fields are required", Toast.LENGTH_SHORT).show();
        }
    }
}
