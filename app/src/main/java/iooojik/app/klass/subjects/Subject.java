package iooojik.app.klass.subjects;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import iooojik.app.klass.Database;
import iooojik.app.klass.R;

public class Subject extends Fragment {


    private View view;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home, container, false);

        Database mDBHelper = new Database(getContext());
        mDBHelper.openDataBase();
        mDBHelper.updateDataBase();
        FloatingActionButton floatingActionButton = getActivity().findViewById(R.id.fab);
        floatingActionButton.hide();
        updateList();

        return view;
    }

    private void updateList(){
        LinearLayout linearLayout = view.findViewById(R.id.items_linear);

        View view1 = getLayoutInflater().inflate(R.layout.recycler_view_item_planet, null, false);
        //астрономия
        FrameLayout frameLayout = view1.findViewById(R.id.frame_formulae);
        ImageView imageView = frameLayout.findViewById(R.id.formulae);
        TextView desc = frameLayout.findViewById(R.id.description);
        TextView nameTv = frameLayout.findViewById(R.id.namePlanet);
        nameTv.setText("Астрономические объекты");

        int width = 300;
        int height = 300;
        Bitmap bitmapAstro = BitmapFactory.decodeResource(getResources(), R.drawable.astronomy_logo);
        imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmapAstro, width, height, false));
        desc.setText(R.string.astronomyTerm);
        NavController navHostFragment = NavHostFragment.findNavController(this);

        view1.setOnClickListener(v -> {
            navHostFragment.navigate(R.id.nav_planets_list);
        });

        linearLayout.addView(view1);

        //геометрия
        View view2 = getLayoutInflater().inflate(R.layout.recycler_view_item_planet, null, false);
        FrameLayout frameLayout2 = view2.findViewById(R.id.frame_formulae);

        ImageView imageView2 = frameLayout2.findViewById(R.id.formulae);
        TextView desc2 = frameLayout2.findViewById(R.id.description);
        TextView nameTv2 = frameLayout2.findViewById(R.id.namePlanet);

        nameTv2.setText("Геометрические фигуры");
        Bitmap bitmapGeo = BitmapFactory.decodeResource(getResources(), R.drawable.geometry);
        imageView2.setImageBitmap(Bitmap.createScaledBitmap(bitmapGeo, width, height, false));
        desc2.setText(R.string.geometryTerm);

        view2.setOnClickListener(v -> {
            //открытие фрагмента с фигурами
            navHostFragment.navigate(R.id.nav_geometry_list);
        });
        linearLayout.addView(view2);

    }
}