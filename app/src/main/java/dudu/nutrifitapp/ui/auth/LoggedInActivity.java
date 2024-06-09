package dudu.nutrifitapp.ui.auth;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import dudu.nutrifitapp.R;

public class LoggedInActivity extends AppCompatActivity {

    private TextView welcome;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference userDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_logged_in);

    }
}