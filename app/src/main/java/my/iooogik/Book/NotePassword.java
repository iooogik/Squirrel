package my.iooogik.Book;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;



/**
 * A simple {@link Fragment} subclass.
 */
public class NotePassword extends Fragment implements View.OnClickListener {

    private View view;


    public NotePassword() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_note_password, container, false);
        Button btn1 = view.findViewById(R.id.btnOne);
        Button btn2 = view.findViewById(R.id.btnTwo);
        Button btn3 = view.findViewById(R.id.btnThree);
        Button btn4 = view.findViewById(R.id.btnFour);
        Button btn5 = view.findViewById(R.id.btnFive);
        Button btn6 = view.findViewById(R.id.btnSix);
        Button btn7 = view.findViewById(R.id.btnSeven);
        Button btn8 = view.findViewById(R.id.btnEight);
        Button btn9 = view.findViewById(R.id.btnNine);
        Button btn0 = view.findViewById(R.id.btnZero);
        Button btnDel = view.findViewById(R.id.btnDel);

        btn0.setOnClickListener(this);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);
        btn7.setOnClickListener(this);
        btn8.setOnClickListener(this);
        btn9.setOnClickListener(this);
        btnDel.setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View v) {

        StringBuilder stringBuilder = new StringBuilder();
        EditText password = v.findViewById(R.id.password);

        if (v.getId() == R.id.btnOne) {
            stringBuilder.append("1");

        } else if (view.getId() == R.id.btnTwo) {
            stringBuilder.append("2");
            password.append("2");
        } else if (view.getId() == R.id.btnThree) {
            stringBuilder.append("3");
            password.append("3");
        } else if (view.getId() == R.id.btnFour) {
            stringBuilder.append("4");
            password.append("4");
        } else if (view.getId() == R.id.btnFive) {
            stringBuilder.append("5");
            password.append("5");
        } else if (view.getId() == R.id.btnSix) {
            stringBuilder.append("6");
            password.append("6");
        } else if (view.getId() == R.id.btnSeven) {
            stringBuilder.append("7");
            password.append("7");
        } else if (view.getId() == R.id.btnEight) {
            stringBuilder.append("8");
            password.append("8");
        } else if (view.getId() == R.id.btnNine) {
            stringBuilder.append("9");
            password.append("9");
        } else if (view.getId() == R.id.btnZero) {
            stringBuilder.append("0");
            password.append("0");
        } else if (view.getId() == R.id.btnDel) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
    }
}
