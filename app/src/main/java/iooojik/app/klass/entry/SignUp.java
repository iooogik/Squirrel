package iooojik.app.klass.entry;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;

import iooojik.app.klass.R;


public class SignUp extends Fragment implements View.OnClickListener{

    public SignUp() {}

    private View view;

    private String accountType = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        Button signIn = view.findViewById(R.id.login);
        signIn.setOnClickListener(this);
        //поле email с слушателем, чтобы после изменения поля показывать пароль (аналогично для последующих полей)
        EditText email = view.findViewById(R.id.email);
        TextInputLayout password = view.findViewById(R.id.text_input_pass3);
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

        TextInputLayout textInputLayout = view.findViewById(R.id.text_input_pass4);
        EditText editPass = view.findViewById(R.id.password);
        editPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count != 0){
                    textInputLayout.setVisibility(View.VISIBLE);
                } else {
                    textInputLayout.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        EditText name = view.findViewById(R.id.name);
        TextInputLayout textInputLayout2 = view.findViewById(R.id.text_input_pass5);
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count != 0){
                    textInputLayout2.setVisibility(View.VISIBLE);
                } else {
                    textInputLayout2.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login:
                EditText email = view.findViewById(R.id.email);
                EditText password = view.findViewById(R.id.password);
                EditText name = view.findViewById(R.id.name);
                EditText surname = view.findViewById(R.id.surname);
                RadioGroup radioGroup = view.findViewById(R.id.radioGroup);
                //слушатель, чтобы получить тип аккаунта
                radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                    switch (checkedId){
                        case R.id.teacher:
                            accountType = "teacher";
                            break;
                        case R.id.pupil:
                            accountType = "pupil";
                            break;
                        default:
                            accountType = null;
                            break;
                    }
                });
                /**
                 * 1. проверяем, не пустые ли поля, если не все поля заполнены, то выводим сообщение: "Не все поля заполнены"
                 * 2. проводим регистрацию, в случае неудачи выводим сообщение: "Что-то пошло не так. Попробуйте снова."
                 */
                break;
        }
    }
}
