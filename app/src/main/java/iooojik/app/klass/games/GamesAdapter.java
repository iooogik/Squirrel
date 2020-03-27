package iooojik.app.klass.games;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import iooojik.app.klass.R;


public class GamesAdapter extends RecyclerView.Adapter<GamesAdapter.ViewHolder> {

    List<Game> games;
    Context context;
    LayoutInflater inflater;
    Fragment fragment;

    public GamesAdapter(List<Game> games, Context context, Fragment fragment) {
        this.games = games;
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
        Game game = games.get(position);
        holder.name.setText(game.getName());
        holder.logo.setImageResource(game.getImageID());
        holder.layout.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(fragment);
            navController.navigate(game.getGameID());
        });
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView logo;
        LinearLayout layout;
        ViewHolder(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.layout);
            name = itemView.findViewById(R.id.textView1);
            logo = itemView.findViewById(R.id.imageView1);
        }
    }
}
