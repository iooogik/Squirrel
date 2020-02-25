package iooogik.app.modelling.las;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import iooogik.app.modelling.MainActivity;
import iooogik.app.modelling.R;


public class LifeAtSpace extends Fragment implements View.OnClickListener {


    public LifeAtSpace() {}

    View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_life_at_space, container, false);

        ImageView openGP = view.findViewById(R.id.imageView2);
        openGP.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.imageView2){
            Intent browserIntent = new
                    Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=lifeatspace.company"));
            startActivity(browserIntent);
        } else if(v.getId() == R.id.back){
            Intent main = new Intent(getContext(), MainActivity.class);
            startActivity(main);
        }
    }
}
