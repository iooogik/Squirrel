package iooojik.app.klass.games;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import iooojik.app.klass.R;

public class Games extends Fragment {

    public Games() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_games, container, false);
        RecyclerView games = view.findViewById(R.id.gameObjects);
        Context context = getContext();
        List<GameObject> gamesList = new ArrayList<>();

        FloatingActionButton floatingActionButton = getActivity().findViewById(R.id.fab);
        floatingActionButton.hide();

        gamesList.add(new GameObject("Life at space", R.drawable.las_logo, R.id.nav_las));
        gamesList.add(new GameObject("Поиск пар", R.drawable.pairs_logo, R.id.nav_pairs));
        gamesList.add(new GameObject("Крестики-нолики", R.drawable.pairs_logo, R.id.nav_gameTikTak));

        games.setLayoutManager(new LinearLayoutManager(getContext()));

        GamesAdapter gamesAdapter = new GamesAdapter(gamesList, context, this);
        games.setAdapter(gamesAdapter);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }
}
