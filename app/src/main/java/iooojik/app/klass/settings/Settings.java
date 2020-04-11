package iooojik.app.klass.settings;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import iooojik.app.klass.AppСonstants;
import iooojik.app.klass.MainActivity;
import iooojik.app.klass.R;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

import static iooojik.app.klass.AppСonstants.APP_PREFERENCES_SHOW_BOOK_MATERIALS;
import static iooojik.app.klass.AppСonstants.APP_PREFERENCES_THEME;

public class Settings extends Fragment implements View.OnClickListener{

    public Settings() {}

    private View view;
    private PackageInfo packageInfo;
    private SharedPreferences preferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings, container, false);
        FloatingActionButton floatingActionButton = getActivity().findViewById(R.id.fab);
        floatingActionButton.hide();
        //получаем настройки
        preferences = getActivity().getSharedPreferences(AppСonstants.APP_PREFERENCES, Context.MODE_PRIVATE);;
        //получаем packageInfo, чтобы узнать версию установленного приложения
        try {
            packageInfo = getActivity().getPackageManager().
                    getPackageInfo(getActivity().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        //выбор фона для профиля
        setProfileBackground();
        //установка тем
        setDarkTheme();
        //"чек" для того, чтобы убрать справочные материалы из заметок
        setShowBookMaterials();
        //установка текущей версии
        setCurrentVersion();
        deAuth();

        contacts();

        return view;
    }

    private void setDarkTheme() {
        Switch darkTheme = view.findViewById(R.id.darkTheme);

        if (preferences.contains(APP_PREFERENCES_THEME)) {
            // Получаем число из настроек
            int val = preferences.getInt(APP_PREFERENCES_THEME, 0);

            if(val == 1){
                darkTheme.setChecked(true);
            } else if (val == 0){
                darkTheme.setChecked(false);
            }
        }

        Intent intent = new Intent(getContext(), MainActivity.class);

        darkTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    preferences.edit().putInt(APP_PREFERENCES_THEME, 1).apply();
                }else {
                    preferences.edit().putInt(APP_PREFERENCES_THEME, 0).apply();
                }
                startActivity(intent);
            }
        });
    }

    private void setProfileBackground() {
        int[] res = new int[4];
        res[0] = R.drawable.background0;
        res[1] = R.drawable.background1;
        res[2] = R.drawable.background2;
        res[3] = R.drawable.background3;
        LinearLayout layout = view.findViewById(R.id.horizontalBackProfile);

        for (int re : res) {
            View viewInfl = getLayoutInflater().inflate(R.layout.background_selector, null);
            ImageView imageView = viewInfl.findViewById(R.id.imageView4);
            Picasso.get().load(re).resize(250, 150)
                    .transform(new RoundedCornersTransformation(20, 0)).into(imageView);

            imageView.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("CommitPrefEdits")
                @Override
                public void onClick(View v) {
                    if (re != R.drawable.background0)
                    preferences.edit().putInt(AppСonstants.BACKGROUND_PROFILE, re).apply();
                    else preferences.edit().putInt(AppСonstants.BACKGROUND_PROFILE, -1).apply();
                    Snackbar.make(view, "Задний фон профиля установлен", Snackbar.LENGTH_LONG).show();
                }
            });
            layout.addView(viewInfl);
        }
    }

    private void setShowBookMaterials() {
        //убираем справочные материалы из заметок
        Switch show_book_mat = view.findViewById(R.id.book_items);

        if (preferences.contains(APP_PREFERENCES_SHOW_BOOK_MATERIALS)) {
            // Получаем число из настроек
            int val = preferences.getInt(APP_PREFERENCES_SHOW_BOOK_MATERIALS, 0);

            if(val == 1){
                show_book_mat.setChecked(true);
            } else if (val == 0){
                show_book_mat.setChecked(false);
            }
        }

        show_book_mat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                SharedPreferences.Editor SettingsEditor = preferences.edit();
                SettingsEditor.putInt(APP_PREFERENCES_SHOW_BOOK_MATERIALS, 1);
                SettingsEditor.apply();
            } else {
                SharedPreferences.Editor SettingsEditor = preferences.edit();
                SettingsEditor.putInt(APP_PREFERENCES_SHOW_BOOK_MATERIALS, 0);
                SettingsEditor.apply();
            }
        });

    }

    private void setCurrentVersion(){
        //установка версии
        TextView version = view.findViewById(R.id.version);
        version.setText(String.format("%s%s", version.getText() + " ", packageInfo.versionName));
    }

    private void deAuth(){
        Button exit = view.findViewById(R.id.exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());

                builder.setTitle("Важное сообщение!");
                builder.setMessage("При выходе все ваши заметки будут удалены, результаты тестов сброшены." +
                        "Вы действительно хотите выйти?");

                builder.setPositiveButton("Выйти", (dialog, which) -> {
                    getActivity().getSharedPreferences(AppСonstants.APP_PREFERENCES, Context.MODE_PRIVATE)
                            .edit().putString(AppСonstants.AUTH_SAVED_TOKEN, "").apply();

                    getActivity().getSharedPreferences(AppСonstants.APP_PREFERENCES, Context.MODE_PRIVATE)
                            .edit().putInt(AppСonstants.APP_PREFERENCES_THEME, 0).apply();

                    getActivity().getSharedPreferences(AppСonstants.APP_PREFERENCES, Context.MODE_PRIVATE)
                            .edit().putInt(AppСonstants.BACKGROUND_PROFILE, -1).apply();

                    startActivity(new Intent(getContext(), MainActivity.class));

                });

                builder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.create().show();
            }
        });

    }

    private void contacts(){
        ImageView telegram = view.findViewById(R.id.telegram);
        ImageView gmail = view.findViewById(R.id.gmail);
        ImageView discord = view.findViewById(R.id.discord);
        ImageView vk = view.findViewById(R.id.vk);
        ImageView instagram = view.findViewById(R.id.instagram);

        telegram.setOnClickListener(this);
        gmail.setOnClickListener(this);
        discord.setOnClickListener(this);
        vk.setOnClickListener(this);
        instagram.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.telegram:
                Uri address = Uri.parse("https://t.me/iooojik");
                Intent openLink = new Intent(Intent.ACTION_VIEW, address);
                startActivity(openLink);
                break;
            case R.id.gmail:
                ClipboardManager clipboard = (ClipboardManager)
                        Objects.requireNonNull(getContext()).
                                getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("", "iooogikdev@gmail.com");
                assert clipboard != null;
                clipboard.setPrimaryClip(clip);
                Snackbar.make(view, "Адрес электронной почты был скопирован в буфер обмена.",
                        Snackbar.LENGTH_LONG).show();
                break;
            case R.id.discord:
                ClipboardManager clipboardDiscord = (ClipboardManager)
                        Objects.requireNonNull(getContext()).
                                getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipDiscord = ClipData.newPlainText("", "Стасян#6249");
                clipboardDiscord.setPrimaryClip(clipDiscord);
                Snackbar.make(view, "Тег дискорда был скопирован в буфер обмена.",
                        Snackbar.LENGTH_LONG).show();
                break;
            case R.id.vk:
                Uri addressVK = Uri.parse("https://vk.com/iooojikdev");
                Intent openVk = new Intent(Intent.ACTION_VIEW, addressVK);
                startActivity(openVk);
                break;
            case R.id.instagram:
                Uri addressInst = Uri.parse("https://www.instagram.com/iooojik/?r=nametag");
                Intent openInst = new Intent(Intent.ACTION_VIEW, addressInst);
                startActivity(openInst);
                break;
        }
    }
}
