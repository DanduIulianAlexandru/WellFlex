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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import dudu.nutrifitapp.R;
import dudu.nutrifitapp.model.User;
import dudu.nutrifitapp.repo.RepoInterface;
import dudu.nutrifitapp.repo.repoDB.UserRepository;
import dudu.nutrifitapp.service.UserService;

public class LogInActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;
    private Button buttonLogIn, buttonSignUp, buttonForgotPassword;
    private UserRepository repository = new UserRepository();
    private UserService service = new UserService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        inputEmail = findViewById(R.id.emailSignIn);
        inputPassword = findViewById(R.id.passwordSignIn);
        progressBar = findViewById(R.id.progressBarSignIn);
        buttonLogIn = findViewById(R.id.loginButtonSignIn);
        buttonSignUp = findViewById(R.id.signupButtonSignIn);
        buttonForgotPassword = findViewById(R.id.forgotPasswordButtonSignIn);

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LogInActivity.this, SignUpActivity.class));
            }
        });
        buttonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        buttonForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
    }

    private void resetPassword() {
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Enter your email address to reset password!", Toast.LENGTH_SHORT).show();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);

        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Toast.makeText(LogInActivity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LogInActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void loginUser() {
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LogInActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.INVISIBLE);
                        if (task.isSuccessful()) {
                            //checks if Account is setup
                            String idUser = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference userTable = database.getReference("User");

                            userTable.child(idUser).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()) {
                                        repository.getOne(idUser, new RepoInterface.DataStatus() {
                                            @Override
                                            public void onSuccess(Object data) {
                                                User user = (User) data;
                                                Intent intent = new Intent(LogInActivity.this, LoggedInActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                            @Override
                                            public void onError(Exception e) {
                                            }
                                        });
                                    }
                                    else{
                                        Intent intent = new Intent(LogInActivity.this, SetupActivity.class);
                                        intent.putExtra("passwordUser", password);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(LogInActivity.this, "EROARE", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            String errorMessage = "Authentication failed. ";
                            if (task.getException() != null) {
                                errorMessage += task.getException().getMessage();
                            }
                            Toast.makeText(LogInActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                });


    }
}