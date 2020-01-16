package my.iooogik.Book;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

public class Contacts extends Fragment {

    View view;

    public Contacts() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_contacts, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        ImageButton telegram = view.findViewById(R.id.telegram);
        telegram.setOnClickListener(v -> {
            Uri address = Uri.parse("https://t.me/iooogik");
            Intent openlink = new Intent(Intent.ACTION_VIEW, address);
            startActivity(openlink);
        });

        ImageButton gmail = view.findViewById(R.id.gmail);
        gmail.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager)
                    getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("", "iooogikdev@gmail.com");
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getContext(), "Адрес электронной почты был " +
                    "скопирован в буфер обмена.", Toast.LENGTH_LONG).show();
        });
    }
}
