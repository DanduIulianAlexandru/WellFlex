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

import dudu.nutrifitapp.model.NutritiveProfile;

public class EditNutritionProfileFragment extends Fragment {

    private EditText editAge, editHeight, editWeight, editSex, editObjective;
    private Button buttonSaveNutritionProfile;
    private DatabaseReference userReference;

    public EditNutritionProfileFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_nutrition_profile, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbarEditNutrition);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigateUp();
            }
        });

        editAge = view.findViewById(R.id.edit_age);
        editHeight = view.findViewById(R.id.edit_height);
        editWeight = view.findViewById(R.id.edit_weight);
        editSex = view.findViewById(R.id.edit_sex);
        editObjective = view.findViewById(R.id.edit_objective);
        buttonSaveNutritionProfile = view.findViewById(R.id.button_save_nutrition_profile);

        userReference = FirebaseDatabase.getInstance().getReference("User").child("x7ElvtDsuuQ7G7cS1nFl5Zz7P143"); // Use the actual user ID

        buttonSaveNutritionProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNutritionProfile();
            }
        });

        return view;
    }

    private void saveNutritionProfile() {
        int age = Integer.parseInt(editAge.getText().toString());
        float height = Float.parseFloat(editHeight.getText().toString());
        float weight = Float.parseFloat(editWeight.getText().toString());
        String sex = editSex.getText().toString();
        float objective = Float.parseFloat(editObjective.getText().toString());

        if (age > 0 && height > 0 && weight > 0 && !sex.isEmpty() && objective > 0) {
            NutritiveProfile nutritiveProfile = new NutritiveProfile(age, height, weight, sex, objective);
            userReference.child("nutritiveProfile").setValue(nutritiveProfile);
            Toast.makeText(getActivity(), "Nutrition Profile Updated", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "All fields are required and must be valid", Toast.LENGTH_SHORT).show();
        }
    }
}
