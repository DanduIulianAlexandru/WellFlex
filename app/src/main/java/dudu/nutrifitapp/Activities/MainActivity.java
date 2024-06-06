package dudu.nutrifitapp.Activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.ui.AppBarConfiguration;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import dudu.nutrifitapp.R;
import dudu.nutrifitapp.ui.dashboard.DashboardFragment;
import dudu.nutrifitapp.ui.nutrition.NutritionFragment;
import dudu.nutrifitapp.ui.fitness.FitnessFragment;
import dudu.nutrifitapp.ui.options.OptionsFragment;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                if(item.getItemId() == R.id.navigation_dashboard)
                    selectedFragment = new DashboardFragment();
                else if(item.getItemId() == R.id.navigation_nutrition)
                    selectedFragment = new NutritionFragment();
                else if(item.getItemId() == R.id.navigation_fitness)
                    selectedFragment = new FitnessFragment();
                else if(item.getItemId() == R.id.navigation_options)
                    selectedFragment = new OptionsFragment();

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                return true;
            }
        });
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NutritionFragment()).commit();
        }
    }
}
