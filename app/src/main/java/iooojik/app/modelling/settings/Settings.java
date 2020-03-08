package iooojik.app.modelling.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
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

import iooojik.app.modelling.MainActivity;
import iooojik.app.modelling.R;
import iooojik.app.modelling.notes.Notes;

public class Settings extends Fragment {

    public Settings() {}

    private View view;
    private PackageInfo packageInfo;
    private AutoCompleteTextView spinner;
    private SharedPreferences Settings;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings, container, false);
        //получаем настройки
        Settings = MainActivity.Settings;
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
                try {
                    Notes.NOTES_ADAPTER.notifyDataSetChanged();
                } catch (Exception e){
                    Log.i("Settings Show_Materials", String.valueOf(e));
                }
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
        themes.add("Красная");
        themes.add("Синяя");
        themes.add("Жёлтая");
        SwitchMaterial darkTheme = view.findViewById(R.id.turn_on_dark_theme);
        spinner = view.findViewById(R.id.themes);
        final int[] val = {0};
        //ставим текст в "спиннер"
        //если тема тёмная, то ставим аналогичную, но не указываем, что она тёмная
        //так как далее идёт "чек" для установки тёмной темы
        // Получаем число из настроек

        val[0] = Settings.getInt(MainActivity.APP_PREFERENCES_THEME, 0);
        if(val[0] > 4){
            switch (val[0]){
                case 5:
                    spinner.setText(themes.get(2));
                    break;
                case 6:
                    spinner.setText(themes.get(3));
                    break;
                case 7:
                    spinner.setText(themes.get(4));
                    break;
            }
            darkTheme.setChecked(true);
        }else  spinner.setText(themes.get(val[0]));

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

        //слушатель для выбора тёмной темы
        darkTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                switch (val[0]){
                    case 2:
                        val[0] = 5;
                        break;
                    case 3:
                        val[0] = 6;
                        break;
                    case 4:
                        val[0] = 7;
                        break;
                }
                SharedPreferences.Editor SettingsEditor = Settings.edit();
                SettingsEditor.putInt(MainActivity.APP_PREFERENCES_THEME, val[0]);
                SettingsEditor.apply();

                getActivity().finish();
                getActivity().startActivity(new Intent(getContext(), MainActivity.class));
                getActivity().overridePendingTransition(android.R.anim.fade_in,
                        android.R.anim.fade_out);
            } else {
                switch (val[0]){
                    case 5:
                        val[0] = 2;
                        break;
                    case 6:
                        val[0] = 3;
                        break;
                    case 7:
                        val[0] = 4;
                        break;
                }
                SharedPreferences.Editor SettingsEditor = Settings.edit();
                SettingsEditor.putInt(MainActivity.APP_PREFERENCES_THEME, val[0]);
                SettingsEditor.apply();

                getActivity().finish();
                getActivity().startActivity(new Intent(getContext(), MainActivity.class));
                getActivity().overridePendingTransition(android.R.anim.fade_in,
                        android.R.anim.fade_out);
            }
        });

    }

    private void setCurrentVersion(){
        //установка версии
        TextView version = view.findViewById(R.id.version);
        version.setText(String.format("%s%s", version.getText(), packageInfo.versionName));
    }

}
