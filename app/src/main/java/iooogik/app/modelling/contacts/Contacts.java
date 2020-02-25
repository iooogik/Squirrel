package iooogik.app.modelling.contacts;


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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

import iooogik.app.modelling.MainActivity;
import iooogik.app.modelling.R;

public class Contacts extends Fragment implements View.OnClickListener {

    View view;

    public Contacts() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_contacts, container, false);

        ImageButton telegram = view.findViewById(R.id.telegram);
        ImageButton gmail = view.findViewById(R.id.gmail);
        ImageButton discord = view.findViewById(R.id.discord);
        ImageButton vk = view.findViewById(R.id.vk);
        ImageButton instagram = view.findViewById(R.id.instagram);

        telegram.setOnClickListener(this);
        gmail.setOnClickListener(this);
        discord.setOnClickListener(this);
        vk.setOnClickListener(this);
        instagram.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.telegram){

            Uri address = Uri.parse("https://t.me/iooogik");
            Intent openLink = new Intent(Intent.ACTION_VIEW, address);
            startActivity(openLink);

        } else if(v.getId() == R.id.gmail){

            ClipboardManager clipboard = (ClipboardManager)
                    Objects.requireNonNull(getContext()).
                            getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("", "iooogikdev@gmail.com");
            assert clipboard != null;
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getContext(), "Адрес электронной почты был " +
                    "скопирован в буфер обмена.", Toast.LENGTH_LONG).show();

        } else if(v.getId() == R.id.discord){

            ClipboardManager clipboard = (ClipboardManager)
                    Objects.requireNonNull(getContext()).
                            getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("", "Стасян#6249");
            assert clipboard != null;
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getContext(), "Тег дискорда был " +
                    "скопирован в буфер обмена.", Toast.LENGTH_LONG).show();

        } else if(v.getId() == R.id.vk){

            Uri address = Uri.parse("https://vk.com/iooogikdev");
            Intent openLink = new Intent(Intent.ACTION_VIEW, address);
            startActivity(openLink);

        } else if(v.getId() == R.id.instagram){

            Uri address = Uri.parse("https://www.instagram.com/iooogik/?r=nametag");
            Intent openLink = new Intent(Intent.ACTION_VIEW, address);
            startActivity(openLink);

        } else if(v.getId() == R.id.back){

            Intent main = new Intent(getContext(), MainActivity.class);
            startActivity(main);

        }
    }
}
