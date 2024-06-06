package dudu.nutrifitapp.ui.fitness;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import dudu.nutrifitapp.databinding.FragmentFitnessBinding;

public class FitnessFragment extends Fragment {

    private FragmentFitnessBinding binding;
    private Calendar calendar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFitnessBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        calendar = Calendar.getInstance();
        updateDate();

        binding.buttonPreviousDay.setOnClickListener(v -> {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            updateDate();
        });

        binding.buttonNextDay.setOnClickListener(v -> {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            updateDate();
        });

        return view;
    }

    private void updateDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MM/dd", Locale.getDefault());
        String date = sdf.format(calendar.getTime());
        binding.textViewDate.setText(date);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
