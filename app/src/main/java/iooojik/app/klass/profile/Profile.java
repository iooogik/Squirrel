package iooojik.app.klass.profile;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import iooojik.app.klass.R;

public class Profile extends Fragment implements View.OnClickListener {
    public Profile(){}

    private View view;
    private FirebaseUser user;
    private List<String> groupList;
    private List<Test> testList;
    private FirebaseAuth mAuth;
    private FloatingActionButton fab;
    private GetTestAdapter getTestAdapter;
    private String teacherRole = "teacher", pupilRole = "pupil";
    private String userRole;
    private GroupsAdapter groupsAdapter;
    private Context context;
    private FirebaseDatabase database;
    private Fragment fragment;

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        database = FirebaseDatabase.getInstance();
        groupList = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        testList = new ArrayList<>();
        setUserInformation();
        fragment = this;

        fab = getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(this::onClick);
        context = getContext();

        return view;
    }

    private void getTeachers() {
        //получаем список учителей
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(user.getUid());
        List<String> teachersIDs = new ArrayList<>();
        reference.child("teachers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    getTestForPupil(ds.getKey());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //Toast.makeText(context, String.valueOf(teachersIDs.size()), Toast.LENGTH_LONG).show();
    }

    private void getTestForPupil(String teacherUID) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(teacherUID);
        reference.child("11a").child("currentTest").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String test = String.valueOf(dataSnapshot.getValue(String.class));
                Toast.makeText(context, String.valueOf(test), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fab.hide();
    }

    private void setUserInformation() {
        //устанавливаем пользовательскую информацию

        TextView email = view.findViewById(R.id.email);
        TextView name = view.findViewById(R.id.name);
        TextView surname = view.findViewById(R.id.surname);
        TextView role = view.findViewById(R.id.role);

        email.setText(user.getEmail());

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(user.getUid());
        //получаем "роль" профиля
        databaseReference.child("type").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String type;
                type = String.valueOf(dataSnapshot.getValue(String.class));
                if (type.equals(teacherRole)) {
                    //учительский профиль
                    FrameLayout pupilProfile = view.findViewById(R.id.pupil_profile);
                    pupilProfile.setVisibility(View.GONE);
                    role.setText(String.format("%s учитель", role.getText()));

                    RecyclerView recyclerView = null;
                    fab.show();
                    fab.setImageResource(R.drawable.round_add_24);

                    recyclerView = view.findViewById(R.id.classes);
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    groupsAdapter = new GroupsAdapter(context, groupList, fragment);
                    recyclerView.setAdapter(groupsAdapter);
                    //получаем спиоск классов
                    getGroupsFromDatabase();

                } else {
                    //ученический профиль
                    FrameLayout teacherProfile = view.findViewById(R.id.teacher_profile);
                    teacherProfile.setVisibility(View.GONE);
                    role.setText(String.format("%s ученик", role.getText()));
                    userRole = pupilRole;
                    RecyclerView recyclerView = null;
                    fab.hide();
                    recyclerView = view.findViewById(R.id.teachers);
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    getTestAdapter = new GetTestAdapter(context, testList, fragment);
                    recyclerView.setAdapter(getTestAdapter);
                    //получаем список учителей и список активных тестов
                    getTeachers();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name.setText(String.valueOf(dataSnapshot.getValue()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("surname").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                surname.setText(String.valueOf(dataSnapshot.getValue()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void getGroupsFromDatabase(){
        //получаем группы(классы) учителя и доавляем их в список
        DatabaseReference databaseReference = database.getReference(user.getUid());
        databaseReference.child("groups").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    groupList.add(String.valueOf(ds.getKey()));
                    groupsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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

                        groupList.add(editText.getText().toString());
                        groupsAdapter.notifyDataSetChanged();

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                                .getReference(user.getUid());

                        databaseReference.child("groups").child(editText.getText().toString())
                                .child("count").setValue(0);

                    });
                    builder.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());
                    builder.create().show();
                }
                break;
        }
    }

}
