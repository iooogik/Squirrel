package iooogik.app.modelling.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;

import iooogik.app.modelling.MainActivity;
import iooogik.app.modelling.R;
import iooogik.app.modelling.notes.Notes;

public class Settings extends Fragment {

    public Settings() {}

    private View view;
    private PackageInfo packageInfo;
    private AutoCompleteTextView spinner;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings, container, false);

        try {
            packageInfo = getActivity().getPackageManager().
                    getPackageInfo(getActivity().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        setThemeSetting();
        setShowBookMaterials();
        setCurrentVersion();
        return view;
    }

    private void setShowBookMaterials() {
        SwitchMaterial show_book_mat = view.findViewById(R.id.book_items);

        if (MainActivity.Settings.contains(MainActivity.APP_PREFERENCES_SHOW_BOOK_MATERIALS)) {
            // Получаем число из настроек
            int val = MainActivity.Settings.getInt(MainActivity.APP_PREFERENCES_SHOW_BOOK_MATERIALS, 0);

            if(val == 1){
                show_book_mat.setChecked(true);
            } else if (val == 0){
                show_book_mat.setChecked(false);
            }
        }

        show_book_mat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                SharedPreferences.Editor SettingsEditor = MainActivity.Settings.edit();
                SettingsEditor.putInt(MainActivity.APP_PREFERENCES_SHOW_BOOK_MATERIALS, 1);
                SettingsEditor.apply();
                try {
                    Notes.NOTES_ADAPTER.notifyDataSetChanged();
                } catch (Exception e){
                    Log.i("Settings Show_Materials", String.valueOf(e));
                }
            } else {
                SharedPreferences.Editor SettingsEditor = MainActivity.Settings.edit();
                SettingsEditor.putInt(MainActivity.APP_PREFERENCES_SHOW_BOOK_MATERIALS, 0);
                SettingsEditor.apply();
            }
        });

    }

    private void setThemeSetting(){

        List<String> themes = new ArrayList<>();

        themes.add("Стандартная");
        themes.add("Тёмная");
        themes.add("Красная");
        themes.add("Синяя");
        themes.add("Жёлтая");

        spinner = view.findViewById(R.id.themes);

        if (MainActivity.Settings.contains(MainActivity.APP_PREFERENCES_THEME)) {
            // Получаем число из настроек
            int val = MainActivity.Settings.getInt(MainActivity.APP_PREFERENCES_THEME, 0);
            spinner.setText(themes.get(val));
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<>(view.getContext(),
                android.R.layout.simple_spinner_dropdown_item, themes);

        spinner.setAdapter(adapter);


        spinner.setOnItemClickListener((parent, view, position, id) -> {

            SharedPreferences.Editor SettingsEditor = MainActivity.Settings.edit();
            SettingsEditor.putInt(MainActivity.APP_PREFERENCES_THEME, position);
            SettingsEditor.apply();

            getActivity().finish();
            getActivity().startActivity(new Intent(getContext(), MainActivity.class));
            getActivity().overridePendingTransition(android.R.anim.fade_in,
                    android.R.anim.fade_out);
        });
    }

    private void setCurrentVersion(){
        TextView version = view.findViewById(R.id.version);
        version.setText(String.format("%s%s", version.getText(), packageInfo.versionName));
    }

}
