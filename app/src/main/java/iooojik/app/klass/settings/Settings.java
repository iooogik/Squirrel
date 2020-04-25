package iooojik.app.klass.settings;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;

import iooojik.app.klass.Api;
import iooojik.app.klass.AppСonstants;
import iooojik.app.klass.Database;
import iooojik.app.klass.MainActivity;
import iooojik.app.klass.R;
import iooojik.app.klass.models.PostResult;
import iooojik.app.klass.models.ServerResponse;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static iooojik.app.klass.AppСonstants.APP_PREFERENCES_SHOW_BOOK_MATERIALS;
import static iooojik.app.klass.AppСonstants.APP_PREFERENCES_THEME;
import static iooojik.app.klass.AppСonstants.PICK_IMAGE_AVATAR;

public class Settings extends Fragment implements View.OnClickListener{

    public Settings() {}

    private View view;
    private PackageInfo packageInfo;
    private SharedPreferences preferences;
    private Context context;
    private Api api;
    private Database mDBHelper;
    private SQLiteDatabase mDb;
    private Cursor userCursor;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings, container, false);
        FloatingActionButton floatingActionButton = getActivity().findViewById(R.id.fab);
        floatingActionButton.hide();
        context = getContext();
        //получаем настройки
        preferences = getActivity().getSharedPreferences(AppСonstants.APP_PREFERENCES, Context.MODE_PRIVATE);;
        //получаем packageInfo, чтобы узнать версию установленного приложения
        try {
            packageInfo = getActivity().getPackageManager().
                    getPackageInfo(getActivity().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Button deleteTests = view.findViewById(R.id.delete_tests);
        deleteTests.setOnClickListener(this);

        //установка тем
        setDarkTheme();
        //"чек" для того, чтобы убрать справочные материалы из заметок
        setShowBookMaterials();
        //установка текущей версии
        setCurrentVersion();
        deAuth();
        setUserInformation();
        contacts();
        changeProfile();
        mDBHelper = new Database(getContext());
        mDBHelper.openDataBase();
        mDBHelper.updateDataBase();
        return view;
    }

    private void changeProfile() {
        ImageView avatar = view.findViewById(R.id.avatar);
        avatar.setOnClickListener(this);
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
                    preferences.edit().clear().apply();

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

    private void setUserInformation() {
        ImageView avatar = view.findViewById(R.id.avatar);
        TextView name = view.findViewById(R.id.name);
        TextView email = view.findViewById(R.id.email);

        if (!preferences.getString(AppСonstants.USER_AVATAR, "").isEmpty()) {

            Picasso.get().load(preferences.getString(AppСonstants.USER_AVATAR, ""))
                    .resize(100, 100)
                    .transform(new RoundedCornersTransformation(30, 5)).into(avatar);
        } else {
            avatar.setImageResource(R.drawable.dark_baseline_account_circle_24);
        }

        name.setText(preferences.getString(AppСonstants.USER_FULL_NAME, ""));
        email.setText(preferences.getString(AppСonstants.USER_EMAIL, ""));
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
                        requireContext().
                                getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("", "iooogikdev@gmail.com");
                assert clipboard != null;
                clipboard.setPrimaryClip(clip);
                Snackbar.make(view, "Адрес электронной почты был скопирован в буфер обмена.",
                        Snackbar.LENGTH_LONG).show();
                break;
            case R.id.discord:
                ClipboardManager clipboardDiscord = (ClipboardManager)
                        requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
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
            case R.id.avatar:
                //запрос на разрешение использование памяти
                int permissionStatus = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
                if (!(permissionStatus == PackageManager.PERMISSION_GRANTED)) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]
                            {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
                if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_PICK);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_AVATAR);
                }
                break;
            case R.id.delete_tests:
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
                builder.setMessage("Вы действительно хотите удалить все тесты? \n" +
                        "При очистке будут удалены и тесты, и результаты!");

                builder.setNegativeButton("Нет", (dialog, which) -> dialog.cancel());
                builder.setPositiveButton("Удалить", (dialog, which) -> {
                    mDb = mDBHelper.getWritableDatabase();
                    mDb.execSQL("DELETE FROM Tests");
                });
                builder.show();
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case PICK_IMAGE_AVATAR:
                if (data != null) {
                    Uri selectedImage = data.getData();
                    Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                            null, null, null);
                    cursor.moveToFirst();
                    File file = new File(getRealPathFromURI(context, selectedImage));

                    if (file.getAbsoluteFile() != null) {
                        doRetrofit();

                        RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);

                        RequestBody requestBody;

                        HashMap<String, RequestBody> map = new HashMap<>();
                        requestBody = RequestBody.create(MediaType.parse("text/plain"),
                                preferences.getString(AppСonstants.USER_EMAIL, ""));

                        map.put("email", requestBody);

                        requestBody = RequestBody.create(MediaType.parse("text/plain"),
                                preferences.getString(AppСonstants.USER_PASSWORD, ""));
                        map.put("password", requestBody);

                        requestBody = RequestBody.create(MediaType.parse("text/plain"),
                                preferences.getString(AppСonstants.USER_FULL_NAME, ""));
                        map.put("full_name", requestBody);

                        requestBody = RequestBody.create(MediaType.parse("text/plain"),
                                preferences.getString(AppСonstants.USER_ID, ""));
                        map.put("id", requestBody);


                        map.put("Avatar", fileReqBody);

                        MultipartBody.Part part = MultipartBody.Part.createFormData("Avatar",
                                preferences.getString(AppСonstants.USER_EMAIL, "avatar"), fileReqBody);

                        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), "Avatar");

                        Call<ServerResponse<PostResult>> postCall = api.userUpdateAvatar(AppСonstants.X_API_KEY,
                                preferences.getString(AppСonstants.AUTH_SAVED_TOKEN, ""), map);

                        postCall.enqueue(new Callback<ServerResponse<PostResult>>() {
                            @Override
                            public void onResponse(Call<ServerResponse<PostResult>> call, Response<ServerResponse<PostResult>> response) {
                                Log.e("tttt", String.valueOf(response.raw()));
                                if (response.code() == 200) {
                                    ImageView avatar = view.findViewById(R.id.avatar);
                                    avatar.setImageURI(selectedImage);
                                } else
                                    Log.e("UPDATE AVATAR", String.valueOf(response.raw() + " " + file.getName()));
                            }

                            @Override
                            public void onFailure(Call<ServerResponse<PostResult>> call, Throwable t) {
                                Log.e("UPDATE AVATAR", String.valueOf(t));
                            }
                        });
                    }
                }

                break;
        }
    }

    private void doRetrofit(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppСonstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(Api.class);
    }

    private static String getRealPathFromURI(Context context, Uri contentURI) {
        String result = null;
        Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            if(idx >= 0) {
                result = cursor.getString(idx);
            }
            cursor.close();
        }
        return result;
    }
}
