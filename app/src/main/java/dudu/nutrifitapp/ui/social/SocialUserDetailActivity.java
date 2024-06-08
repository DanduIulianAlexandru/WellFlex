package dudu.nutrifitapp.ui.social;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import dudu.nutrifitapp.R;

public class SocialUserDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.social_user_detail);

        TextView textViewUserName = findViewById(R.id.textViewUserNameDetail);

        String userName = getIntent().getStringExtra("USER_NAME");
        textViewUserName.setText(userName);
    }
}
