package iooogik.app.modelling.entry;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import iooogik.app.modelling.R;


public class SignUp extends Fragment implements View.OnClickListener{

    public SignUp() {}

    private View view;
    private FirebaseAuth mAuth;
    private String userType = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        mAuth = FirebaseAuth.getInstance();
        RadioGroup radioGroup = view.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId){
                case R.id.teacher:
                    userType = "teacher";
                    break;
                case R.id.pupil:
                    userType = "pupil";
                    break;
                default:
                    userType = null;
                    break;
            }
        });
        Button signIn = view.findViewById(R.id.login);
        signIn.setOnClickListener(this);

        EditText email = view.findViewById(R.id.email);
        TextInputLayout password = view.findViewById(R.id.text_input_pass);
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count != 0){
                    password.setVisibility(View.VISIBLE);
                } else {
                    password.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login:
                EditText email = view.findViewById(R.id.email);
                EditText password = view.findViewById(R.id.password);
                if(!(userType == null && email.getText().toString().isEmpty() && password.getText().toString().isEmpty())) {
                    mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(getActivity(), task -> {
                                if (task.isSuccessful()) {

                                    FirebaseUser user = mAuth.getCurrentUser();
                                    DatabaseReference database = FirebaseDatabase.getInstance().
                                            getReference(user.getUid());
                                    database.child("type").setValue(userType);

                                    NavController navController = NavHostFragment.findNavController(getParentFragment());
                                    navController.navigate(R.id.nav_profile);
                                    BottomAppBar bottomAppBar = getActivity().findViewById(R.id.bar);
                                    bottomAppBar.setVisibility(View.VISIBLE);
                                } else {
                                    Toast.makeText(getContext(), "Что-то пошло не так. Попробуйте снова.",
                                            Toast.LENGTH_LONG).show();
                                }
                            });
                }
                break;
        }
    }
}
