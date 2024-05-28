package dudu.nutrifitapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import dudu.nutrifitapp.R;

public class SignUpActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword, inputRepeatPassword;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        firebaseAuth = FirebaseAuth.getInstance();
        inputEmail = findViewById(R.id.emailSignUp);
        inputPassword = findViewById(R.id.passwordSignUp);
        inputRepeatPassword = findViewById(R.id.repeatPasswordSignUp);
        Button buttonSignUp = findViewById(R.id.signUpButtonSignUp);
        progressBar = findViewById(R.id.progressBarSignUp);

        buttonSignUp.setOnClickListener(v -> signUpUser());
    }

    private void signUpUser() {
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        String repeatPassword = inputRepeatPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(repeatPassword)) {
            Toast.makeText(getApplicationContext(), "Repeat the password!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(repeatPassword)) {
            Toast.makeText(getApplicationContext(), "Passwords do not match!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignUpActivity.this, task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        Toast.makeText(SignUpActivity.this, "User registered successfully!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignUpActivity.this, LogInActivity.class));
                        finish();
                    } else {
                        Toast.makeText(SignUpActivity.this, "Authentication failed. " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

    }
}

