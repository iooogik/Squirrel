package iooojik.app.klass.games;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.squareup.picasso.Picasso;

import java.util.List;

import iooojik.app.klass.R;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;


public class GamesAdapter extends RecyclerView.Adapter<GamesAdapter.ViewHolder> {

    private List<GameObject> gameObjects;
    private Context context;
    private LayoutInflater inflater;
    private Fragment fragment;

    GamesAdapter(List<GameObject> gameObjects, Context context, Fragment fragment) {
        this.gameObjects = gameObjects;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_view_game_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GameObject gameObject = gameObjects.get(position);
        holder.name.setText(gameObject.getName());
        Picasso.with(context).load(gameObject.getImageID())
                .resize(130, 130)
                .transform(new RoundedCornersTransformation(30, 5)).into(holder.logo);
        if (gameObject.gameID == R.id.nav_pairs){
            holder.itemView.setOnClickListener(v -> {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
                LinearLayout linearLayout = new LinearLayout(context);
                linearLayout.setOrientation(LinearLayout.VERTICAL);

                final Spinner spinner = new Spinner(context);
                String[] difficulties = {"Обычный уровень", "Сложный уровень"};
                ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                        android.R.layout.simple_spinner_dropdown_item, difficulties);
                spinner.setAdapter(adapter);


                builder.setTitle("Выберите уровень: ");

                linearLayout.addView(spinner);

                builder.setView(linearLayout);

                builder.setPositiveButton("Начать", (dialog, which) -> {
                    Bundle bundle = new Bundle();
                    if(spinner.getSelectedItem().toString().equals("Обычный уровень")){
                        bundle.putInt("Height", 4);
                        bundle.putInt("Width", 4);

                    } else if(spinner.getSelectedItem().toString().equals("Сложный уровень")){
                        bundle.putInt("Height", 6);
                        bundle.putInt("Width", 6);
                    }
                    NavController navHostFragment = NavHostFragment.findNavController(fragment);
                    navHostFragment.navigate(R.id.nav_pairs, bundle);
                });

                builder.create().show();
            });


        } else {
            holder.itemView.setOnClickListener(v -> {
                NavController navController = NavHostFragment.findNavController(fragment);
                navController.navigate(gameObject.getGameID());
            });
        }
    }

    @Override
    public int getItemCount() {
        return gameObjects.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView logo;

        ViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.textView1);
            logo = itemView.findViewById(R.id.imageView1);
        }
    }
}
