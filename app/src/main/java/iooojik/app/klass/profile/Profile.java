package iooojik.app.klass.profile;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import iooojik.app.klass.R;

public class Profile extends Fragment implements View.OnClickListener {
    public Profile(){}

    private View view;
    private List<String> groupList;
    private List<Test> testList;
    private FloatingActionButton fab;
    private String teacherRole = "teacher", pupilRole = "pupil";
    private String userRole;
    private GroupsAdapter groupsAdapter;
    private Context context;
    private Fragment fragment;

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        //список с группами(для учителей)
        groupList = new ArrayList<>();
        //список с активными тестами
        testList = new ArrayList<>();
        //получение пользовательской информации
        setUserInformation();
        //получение текущего фрагмента, чтобы использовать его в адаптере
        fragment = this;
        //получаем fab и ставим слушатель на него
        fab = getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(this);
        //контекст
        context = getContext();
        //запрос на разрешение использования камеры
        int permissionStatus = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
        if (!(permissionStatus == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.CAMERA}, 1);
        }
        //
        return view;
    }

    private void getActiveTests() {
        //получаем список активных тестов
    }

    private void setUserInformation() {
        //получаем и устанавливаем пользовательскую информацию

        TextView email = view.findViewById(R.id.email);
        TextView name = view.findViewById(R.id.name);
        TextView surname = view.findViewById(R.id.surname);
        TextView role = view.findViewById(R.id.role);

        /**
         * 1. если стоит учительский профиль, то убираем ученический профиль, изменяем поле "роль",
         * показываем fab и ставим адаптер для RecyclerView(id = classes). GroupsAdapter(контекст, список с группами, текущий фрагмент)
         * 2. если ученический профиль, то убираем учительский, ставим соответсвующую роль, убираем fab
         * и ставим адаптер на RecyclerView(id = teachers) и получаем список активных тестов
         */

    }

    private void getGroupsFromDatabase(){
        //получаем группы(классы) учителя и доавляем их в список
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab:
                //добавление класса в учительский профиль
                if(userRole.equals(teacherRole)){
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
                    View view1 = getLayoutInflater().inflate(R.layout.edit_text, null);
                    TextInputLayout textInputLayout = view1.findViewById(R.id.text_input_layout);
                    textInputLayout.setHint("Введите класс");
                    textInputLayout.setCounterMaxLength(3);
                    TextInputEditText editText = view1.findViewById(R.id.edit_text);
                    builder.setView(view1);
                    builder.setPositiveButton("Добавить", (dialog, which) -> {
                        //добавление в список и обновление адаптера
                        groupList.add(editText.getText().toString());
                        groupsAdapter.notifyDataSetChanged();
                        //заносим в базу данных
                    });
                    builder.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());
                    builder.create().show();
                }
                break;
        }
    }

}
