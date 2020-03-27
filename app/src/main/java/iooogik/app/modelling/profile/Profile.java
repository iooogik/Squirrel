package iooogik.app.modelling.profile;

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

import iooogik.app.modelling.R;

public class Profile extends Fragment implements View.OnClickListener {
    public Profile(){}

    private View view;
    private FirebaseUser user;
    private List<String> groupList;
    private FirebaseAuth mAuth;
    private FloatingActionButton fab;
    private String teacherRole = "teacher", pupilRole = "pupil";
    private String userRole = "";
    private GroupsAdapter groupsAdapter;
    private Context context;
    private FirebaseDatabase database;

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        database = FirebaseDatabase.getInstance();
        groupList = new ArrayList<>();

        setUserInformation();

        getGroupsFromDatabase();

        fab = getActivity().findViewById(R.id.fab);
        fab.show();
        fab.setImageResource(R.drawable.round_add_24);
        fab.setOnClickListener(this);

        context = getContext();

        RecyclerView groups = view.findViewById(R.id.classes);
        groupsAdapter = new GroupsAdapter(context, groupList, this);
        groups.setLayoutManager(new LinearLayoutManager(context));
        groups.setAdapter(groupsAdapter);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fab.hide();
    }

    private void setUserInformation(){
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        TextView email = view.findViewById(R.id.email);
        TextView name = view.findViewById(R.id.name);
        TextView surname = view.findViewById(R.id.surname);
        TextView role = view.findViewById(R.id.role);

        email.setText(user.getEmail());

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(user.getUid());

        databaseReference.child("type").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String type;
                type = String.valueOf(dataSnapshot.getValue(String.class));
                if(type.equals("teacher")){
                    FrameLayout pupilProfile = view.findViewById(R.id.pupil_profile);
                    pupilProfile.setVisibility(View.GONE);
                    role.setText(String.format("%s учитель", role.getText()));
                }
                else {
                    FrameLayout teacherProfile = view.findViewById(R.id.teacher_profile);
                    teacherProfile.setVisibility(View.GONE);
                }
                setUserRole(type);
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

    private void setUserRole(String type){
        userRole = type;
    }

    private void getGroupsFromDatabase(){
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
                        Toast.makeText(getContext(), user.getUid(), Toast.LENGTH_LONG).show();
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
