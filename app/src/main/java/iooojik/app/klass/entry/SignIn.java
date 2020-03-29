package iooojik.app.klass.entry;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import iooojik.app.klass.R;

public class SignIn extends Fragment implements View.OnClickListener {

    public SignIn() {}

    private View view;
    private FirebaseAuth mAuth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        mAuth = FirebaseAuth.getInstance();
        //кнопка входа
        Button signIn = view.findViewById(R.id.login);
        signIn.setOnClickListener(this);
        //кнопка перехода на регистрационную форму
        Button reg = view.findViewById(R.id.registr);
        reg.setOnClickListener(this);
        //скрываем fab
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.hide();
        //слушатель для EditText (EditText для ввода пароля
        // будет появляться после изменения EditText с email-ом)
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
                //если EditText не пустые, то
                if(!(email.getText().toString().isEmpty() && password.getText().toString().isEmpty())) {
                    //выполняем авторизацию
                    mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(task -> {
                                if (!task.isSuccessful()) {//если авторизация не удалась
                                    Toast.makeText(getContext(), "Что-то пошло не так. Попробуйте снова.",
                                            Toast.LENGTH_LONG).show();

                                } else {

                                    DrawerLayout mDrawerLayout = getActivity().findViewById(R.id.drawer_layout);
                                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

                                    //переходим на "главный" фрагмент и показываем нижний toolbar
                                    NavController navController = NavHostFragment.
                                            findNavController(getParentFragment());
                                    navController.navigate(R.id.nav_profile);

                                    BottomAppBar bottomAppBar = getActivity().findViewById(R.id.bar);
                                    bottomAppBar.setVisibility(View.VISIBLE);

                                    FloatingActionButton fab = getActivity().findViewById(R.id.fab);
                                    fab.show();
                                }
                            });
                }

                break;
            case R.id.registr:
                //переход на регистрационную форму
                NavController navController = NavHostFragment.findNavController(this);
                navController.navigate(R.id.nav_signUp);
                break;
        }
    }
}
