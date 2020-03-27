package iooojik.app.klass.games.pairs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import iooojik.app.klass.R;

public class Menu extends Fragment implements View.OnClickListener {

    public Menu() {}

    private SharedPreferences preferences;
    // название настроек
    public static final String APP_PREFERENCES = "Settings";
    //вкл/выкл мзуыки
    public static final String APP_PREFERENCES_MUSIC = "Music";

    private View view;
    private Context context;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pairs_menu, container, false);
        context = getContext();
        preferences = getActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        Button start = view.findViewById(R.id.startGame);
        start.setOnClickListener(this);

        return view;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.startGame:

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

                builder.setPositiveButton("Начать", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Bundle bundle = new Bundle();
                        if(spinner.getSelectedItem().toString().equals("Обычный уровень")){
                            bundle.putInt("Height", 4);
                            bundle.putInt("Width", 4);

                        } else if(spinner.getSelectedItem().toString().equals("Сложный уровень")){
                            bundle.putInt("Height", 6);
                            bundle.putInt("Width", 6);
                        }
                        NavController navHostFragment = NavHostFragment.findNavController(getParentFragment());
                        navHostFragment.navigate(R.id.nav_pairs, bundle);
                    }
                });

                builder.create().show();


                break;
        }
    }
}
