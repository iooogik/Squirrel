package iooojik.app.klass.games;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import iooojik.app.klass.R;

public class Games extends Fragment {

    public Games() {}

    private View view;
    private List<Game> gamesList;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_games, container, false);
        RecyclerView games = view.findViewById(R.id.games);
        context = getContext();
        gamesList = new ArrayList<>();

        gamesList.add(new Game("Life at space", R.drawable.las_logo, R.id.nav_las));
        gamesList.add(new Game("Поиск пар", R.drawable.las_logo, R.id.pairs_menu));

        games.setLayoutManager(new LinearLayoutManager(getContext()));

        GamesAdapter gamesAdapter = new GamesAdapter(gamesList, context, this);
        games.setAdapter(gamesAdapter);
        return view;
    }
}
