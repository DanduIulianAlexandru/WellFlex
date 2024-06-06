package dudu.nutrifitapp.ui.nutrition;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

import dudu.nutrifitapp.R;

public class NutritionStatisticsActivity extends AppCompatActivity {

    private PieChart pieChart;
    private ImageButton buttonBack;
    private TextView textViewCarbs, textViewProtein, textViewFat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nutrition_statistics);

        pieChart = findViewById(R.id.pieChart);
        buttonBack = findViewById(R.id.buttonBack);
        textViewCarbs = findViewById(R.id.textViewCarbs);
        textViewProtein = findViewById(R.id.textViewProtein);
        textViewFat = findViewById(R.id.textViewFat);

        buttonBack.setOnClickListener(v -> finish());

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            double carbs = extras.getDouble("carbs");
            double protein = extras.getDouble("protein");
            double fat = extras.getDouble("fat");
            generatePieChart(carbs, protein, fat);
            displayStatistics(carbs, protein, fat);
        }
    }

    private void generatePieChart(double carbs, double protein, double fat) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry((float) carbs, "Carbohydrates"));
        entries.add(new PieEntry((float) protein, "Proteins"));
        entries.add(new PieEntry((float) fat, "Fats"));

        PieDataSet dataSet = new PieDataSet(entries, "Calorie Sources");
        dataSet.setColors(new int[]{ColorTemplate.rgb("#FF0000"), ColorTemplate.rgb("#0000FF"), ColorTemplate.rgb("#FFA500")});
        PieData data = new PieData(dataSet);

        pieChart.setData(data);
        pieChart.invalidate(); // refresh
    }

    private void displayStatistics(double carbs, double protein, double fat) {
        textViewCarbs.setText(String.format("Carbohydrates: %.1f g", carbs));
        textViewProtein.setText(String.format("Proteins: %.1f g", protein));
        textViewFat.setText(String.format("Fats: %.1f g", fat));
    }
}
