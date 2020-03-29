package iooojik.app.klass.group;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import iooojik.app.klass.R;


public class Group extends Fragment implements View.OnClickListener{

    public Group() {}

    private View view;
    private String groupName;
    private List<Mate> groupMates;
    private Context context;
    private GroupMatesAdapter groupmatesAdapter;
    private FloatingActionButton fab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_group, container, false);
        //список учеников
        groupMates = new ArrayList<>();
        //получение названия нажатого класса
        getGroupName();
        //контекст
        context = getContext();
        //получение списка одноклассников
        getGroupMates();
        //адаптер для списка учеников
        groupmatesAdapter =  new GroupMatesAdapter(context, this, groupMates);
        RecyclerView groupmates = view.findViewById(R.id.groupmates);
        groupmates.setLayoutManager(new LinearLayoutManager(context));
        groupmates.setAdapter(groupmatesAdapter);
        //конпка с открытием редактора тестов
        Button testEditor = view.findViewById(R.id.testEditor);
        testEditor.setOnClickListener(this);

        fab = getActivity().findViewById(R.id.fab);
        fab.show();
        fab.setOnClickListener(this);

        return view;
    }

    private void getGroupMates(){
        //получаем список учеников(их полное имя и email) из бд
    }

    private void getGroupName(){
        Bundle args = this.getArguments();
        groupName = args.getString("groupName");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab:
                /**MaterialAlertDialogBuilder для добавления нового ученика в группу
                 * 1. пользователь вводит email и полное имя ученика, если он есть в базе, то
                 * он добавляется в список и, соответсвенно, в бд
                 */

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);
                View view1 = getLayoutInflater().inflate(R.layout.edit_text, null);
                TextInputEditText emailText = view1.findViewById(R.id.edit_text);
                emailText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                TextInputLayout textInputLayout = view1.findViewById(R.id.text_input_layout);
                textInputLayout.setHint("Введите e-mail адрес");
                textInputLayout.setHelperTextEnabled(false);
                textInputLayout.setCounterEnabled(false);

                View view2 = getLayoutInflater().inflate(R.layout.edit_text, null);
                TextInputEditText nameSurname = view2.findViewById(R.id.edit_text);

                TextInputLayout textInputLayout2 = view2.findViewById(R.id.text_input_layout);
                textInputLayout2.setHint("Введите ФИО ученика");
                textInputLayout2.setHelperTextEnabled(false);
                textInputLayout2.setCounterEnabled(false);


                layout.addView(view2);
                layout.addView(view1);

                builder.setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String email = emailText.getText().toString();
                        boolean result = false;
                        String pupilID = "";
                        //проверяем, есть ли пользовтель в бд


                        if(result && !pupilID.isEmpty()){

                        } else {
                            Snackbar.make(view, "Пользователь с указанным e-mail адресом не был найден." +
                                    "Пожалуйста, повторите попытку снова или напишите разработчику.",
                                    Snackbar.LENGTH_LONG).show();



                        }
                    }
                });

                builder.setView(layout);
                builder.create().show();
                break;
            case R.id.testEditor:
                //редактор тестов
                Bundle bundle = new Bundle();
                bundle.putString("groupName", groupName);
                NavController navController = NavHostFragment.findNavController(this);
                navController.navigate(R.id.nav_testEditor, bundle);
        }
    }
}
