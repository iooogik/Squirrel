package iooogik.app.modelling.entry;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import iooogik.app.modelling.MainActivity;
import iooogik.app.modelling.R;

public class SignIn extends Fragment implements View.OnClickListener {

    public SignIn() {}

    private View view;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        mAuth = FirebaseAuth.getInstance();
        Button signIn = view.findViewById(R.id.login);
        signIn.setOnClickListener(this);
        Button reg = view.findViewById(R.id.registr);
        reg.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login:
                EditText email = view.findViewById(R.id.email);
                EditText password = view.findViewById(R.id.password);
                if(!(email.getText().toString().isEmpty() && password.getText().toString().isEmpty())) {
                    mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(getContext(), "Что-то пошло не так. Попробуйте снова.",
                                                Toast.LENGTH_LONG).show();

                                    } else {
                                        SharedPreferences preferences = getActivity().
                                                getSharedPreferences(MainActivity.APP_PREFERENCES,
                                                        Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = preferences.edit();
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        editor.putString("userLogin", user.getEmail());
                                        NavController navController = NavHostFragment.
                                                findNavController(getParentFragment());
                                        navController.navigate(R.id.nav_home);
                                        BottomAppBar bottomAppBar = getActivity().findViewById(R.id.bar);
                                        bottomAppBar.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                }

                break;
            case R.id.registr:
                NavController navController = NavHostFragment.findNavController(this);
                navController.navigate(R.id.nav_signUp);
                break;
        }
    }
}
