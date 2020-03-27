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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import iooojik.app.klass.R;


public class Group extends Fragment implements View.OnClickListener{

    public Group() {}

    private View view;
    private String groupName;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private List<Mate> groupMates;
    private Context context;
    private GroupMatesAdapter groupmatesAdapter;
    private FirebaseDatabase database;
    private FloatingActionButton fab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_group, container, false);
        //список учеников
        groupMates = new ArrayList<>();
        //получение названия нажатого класса
        getGroupName();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        context = getContext();
        database = FirebaseDatabase.getInstance();
        //получение списка одноклассников
        getGroupMates();

        groupmatesAdapter =  new GroupMatesAdapter(context, this, groupMates);
        RecyclerView groupmates = view.findViewById(R.id.groupmates);
        groupmates.setLayoutManager(new LinearLayoutManager(context));
        groupmates.setAdapter(groupmatesAdapter);


        Button testEditor = view.findViewById(R.id.testEditor);
        testEditor.setOnClickListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        fab = getActivity().findViewById(R.id.fab);
        fab.show();
        fab.setOnClickListener(this);
    }

    private void getGroupMates(){
        DatabaseReference databaseReference = database.getReference(user.getUid());
        databaseReference.child("groups").child(groupName).child("groupmates").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){

                    databaseReference.child("groups").child(groupName).child("groupmates").child(ds.getKey()).child("email").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String email = String.valueOf(dataSnapshot.getValue(String.class));
                            groupMates.add(new Mate(ds.getKey(), email));
                            groupmatesAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getGroupName(){
        Bundle args = this.getArguments();
        groupName = args.getString("groupName");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab:
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
                        if(user != null){
                            for(UserInfo profile : user.getProviderData()){
                                String tempMail = profile.getEmail();
                                if (email.equals(tempMail)){
                                    result = true;
                                    break;
                                }
                            }
                        }
                        if(result){
                            DatabaseReference databaseReference = FirebaseDatabase.
                                    getInstance().getReference(user.getUid());

                            databaseReference.child("groups").child(groupName).child("groupmates")
                                    .child(nameSurname.getText().toString()).child("averageScore").setValue(0);

                            databaseReference.child("groups").child(groupName).child("groupmates")
                                    .child(nameSurname.getText().toString()).child("email").setValue(email);
                            getGroupMates();
                        } else {
                            Toast.makeText(getContext(),
                                    "Пользователь с указанным e-mail адресом не был найден." +
                                            "Пожалуйста, повторите попытку снова или напишите разработчику.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });

                builder.setView(layout);
                builder.create().show();
                break;
            case R.id.testEditor:
                Bundle bundle = new Bundle();
                bundle.putString("groupName", groupName);
                NavController navController = NavHostFragment.findNavController(this);
                navController.navigate(R.id.nav_testEditor, bundle);
        }
    }
}
