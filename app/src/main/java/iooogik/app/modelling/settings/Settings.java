package iooogik.app.modelling.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;

import iooogik.app.modelling.MainActivity;
import iooogik.app.modelling.R;

import static iooogik.app.modelling.MainActivity.APP_PREFERENCES;

public class Settings extends Fragment {

    public Settings() {}

    private View view;
    private PackageInfo packageInfo;
    private SharedPreferences Settings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings, container, false);
        //получаем настройки
        Settings = getActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);;
        //получаем packageInfo, чтобы узнать версию установленного приложения
        try {
            packageInfo = getActivity().getPackageManager().
                    getPackageInfo(getActivity().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        //установка тем
        setThemeSetting();
        //"чек" для того, чтобы убрать справочные материалы из заметок
        setShowBookMaterials();
        //установка текущей версии
        setCurrentVersion();
        return view;
    }

    private void setShowBookMaterials() {
        //убираем справочные материалы из заметок
        SwitchMaterial show_book_mat = view.findViewById(R.id.book_items);

        if (Settings.contains(MainActivity.APP_PREFERENCES_SHOW_BOOK_MATERIALS)) {
            // Получаем число из настроек
            int val = Settings.getInt(MainActivity.APP_PREFERENCES_SHOW_BOOK_MATERIALS, 0);

            if(val == 1){
                show_book_mat.setChecked(true);
            } else if (val == 0){
                show_book_mat.setChecked(false);
            }
        }

        show_book_mat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                SharedPreferences.Editor SettingsEditor = Settings.edit();
                SettingsEditor.putInt(MainActivity.APP_PREFERENCES_SHOW_BOOK_MATERIALS, 1);
                SettingsEditor.apply();
            } else {
                SharedPreferences.Editor SettingsEditor = Settings.edit();
                SettingsEditor.putInt(MainActivity.APP_PREFERENCES_SHOW_BOOK_MATERIALS, 0);
                SettingsEditor.apply();
            }
        });

    }

    private void setThemeSetting(){

        List<String> themes = new ArrayList<>();

        //список тем
        themes.add("Стандартная");
        themes.add("Тёмная");
        AutoCompleteTextView spinner = view.findViewById(R.id.themes);
        int val = 0;
        //ставим текст в "спиннер"
        //если тема тёмная, то ставим аналогичную, но не указываем, что она тёмная,
        //так как далее идёт "чек" для установки тёмной темы.

        // Получаем число из настроек

        val = Settings.getInt(MainActivity.APP_PREFERENCES_THEME, 0);

        spinner.setText(themes.get(val));

        //адаптер
        ArrayAdapter<String> adapter = new ArrayAdapter<>(view.getContext(),
                android.R.layout.simple_spinner_dropdown_item, themes);
        spinner.setAdapter(adapter);
        spinner.setOnItemClickListener((parent, view, position, id) -> {

            SharedPreferences.Editor SettingsEditor = Settings.edit();
            SettingsEditor.putInt(MainActivity.APP_PREFERENCES_THEME, position);
            SettingsEditor.apply();

            getActivity().finish();
            getActivity().startActivity(new Intent(getContext(), MainActivity.class));
            getActivity().overridePendingTransition(android.R.anim.fade_in,
                    android.R.anim.fade_out);
        });

    }

    private void setCurrentVersion(){
        //установка версии
        TextView version = view.findViewById(R.id.version);
        version.setText(String.format("%s%s", version.getText(), packageInfo.versionName));
    }

}
