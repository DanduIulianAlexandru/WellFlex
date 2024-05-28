package dudu.nutrifitapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;

public class SocialFragment extends Fragment {

    private DrawerLayout drawerLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_social, container, false);

        drawerLayout = view.findViewById(R.id.drawer_layout);
        NavigationView navigationView = view.findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                if(item.getItemId() == R.id.nav_social_profile)
                    selectedFragment = new EditSocialProfileFragment();
                else if(item.getItemId() == R.id.nav_nutrition_profile)
                    selectedFragment = new EditNutritionProfileFragment();
                else if(item.getItemId() == R.id.nav_auth)
                    selectedFragment = new EditAuthFragment();

                if (selectedFragment != null) {
                    getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });

        return view;
    }
}
