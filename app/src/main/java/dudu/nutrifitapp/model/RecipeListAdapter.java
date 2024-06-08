package dudu.nutrifitapp.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import dudu.nutrifitapp.R;

public class RecipeListAdapter extends ArrayAdapter<String> {

    private Context context;
    private List<String> recipeNames;
    private List<String> recipeIds;
    private DeleteRecipeListener deleteRecipeListener;

    public interface DeleteRecipeListener {
        void onDeleteRecipe(int position);
    }

    public RecipeListAdapter(Context context, List<String> recipeNames, List<String> recipeIds, DeleteRecipeListener deleteRecipeListener) {
        super(context, R.layout.option_item_recipe, recipeNames);
        this.context = context;
        this.recipeNames = recipeNames;
        this.recipeIds = recipeIds;
        this.deleteRecipeListener = deleteRecipeListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.option_item_recipe, parent, false);
        }

        TextView recipeNameTextView = convertView.findViewById(R.id.recipeNameTextView);
        recipeNameTextView.setText(recipeNames.get(position));

        Button deleteButton = convertView.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(v -> deleteRecipeListener.onDeleteRecipe(position));

        return convertView;
    }
}
