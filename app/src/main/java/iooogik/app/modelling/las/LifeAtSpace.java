package iooogik.app.modelling.las;


import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

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
            PackageManager packageManager = getActivity().getPackageManager();
            PackageInfo packageInfo = null;
            //проверяем установлено ли приложение
            //если установлено, то запускаем его, иначе перенаправляем пользователя на страницу загрузки
            try {
                packageInfo = packageManager.getPackageInfo("lifeatspace.company", 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            if(packageInfo != null){
                Intent intent = packageManager.getLaunchIntentForPackage("lifeatspace.company");
                startActivity(intent);
            }
            else {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=lifeatspace.company"));
                startActivity(browserIntent);
            }

        }
    }
}
