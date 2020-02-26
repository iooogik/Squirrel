package iooogik.app.modelling.home;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import iooogik.app.modelling.Database;
import iooogik.app.modelling.geometry.GeometricFigures;
import iooogik.app.modelling.MainActivity;
import iooogik.app.modelling.notes.Note;
import iooogik.app.modelling.notes.Notes;
import iooogik.app.modelling.astonomy.Planets;
import iooogik.app.modelling.R;

public class HomeFragment extends Fragment {


    View view;
    FloatingActionButton FAB;
    private Database mDBHelper;
    private SQLiteDatabase mDb;
    private FrameLayout frameLayout;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home, container, false);
        FAB = view.findViewById(R.id.fab);

        Database mDBHelper = new Database(getContext());
        mDBHelper.openDataBase();
        mDBHelper.updateDataBase();

        updateList();
        frameLayout = view.findViewById(R.id.Mainframe);
        frameLayout.removeAllViews();

        return view;
    }

    private void showFragment(Fragment fragment, FrameLayout frameLayout){
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ScrollView scrollView = view.findViewById(R.id.scrollMain);
        scrollView.setVisibility(View.GONE);

        try{
            FrameLayout main = view.findViewById(R.id.Mainframe);
            main.removeAllViews();
        } catch (Exception e){
            Log.i("Planets", "fail");
        }

        frameLayout.setVisibility(View.VISIBLE);

        if (fragment != null) {
            ft.remove(fragment).commitAllowingStateLoss();
        }


        FragmentTransaction addTransaction = fm.beginTransaction();
        addTransaction.setCustomAnimations
                (R.anim.nav_default_enter_anim, R.anim.nav_default_exit_anim);
        addTransaction.addToBackStack(null);
        assert fragment != null;
        addTransaction.add(R.id.Mainframe, fragment,
                "mainFrame").commitAllowingStateLoss();

    }

    private void updateList(){
        LinearLayout linearLayout = view.findViewById(R.id.items_linear);

        View view1 = getLayoutInflater().inflate(R.layout.item_planet, null, false);
        //астрономия
        FrameLayout frameLayout = view1.findViewById(R.id.frame_formulae);
        ImageView imageView = frameLayout.findViewById(R.id.formulae);
        TextView desc = frameLayout.findViewById(R.id.description);
        TextView nameTv = frameLayout.findViewById(R.id.namePlanet);
        nameTv.setText("Астрономия");

        int width = 300;
        int height = 300;
        Bitmap bitmapAstro = BitmapFactory.decodeResource(getResources(), R.drawable.astronomy_logo);
        imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmapAstro, width, height, false));
        desc.setText(R.string.astronomyTerm);
        NavController navHostFragment = NavHostFragment.findNavController(this);

        view1.setOnClickListener(v -> {
            navHostFragment.navigate(R.id.nav_planets_list);
            /*
            Planets planets = new Planets();
            FrameLayout frameLayout1 = view.findViewById(R.id.Mainframe);
            showFragment(planets, frameLayout1);

             */
        });

        linearLayout.addView(view1);

        //геометрия
        View view2 = getLayoutInflater().inflate(R.layout.item_planet, null, false);
        FrameLayout frameLayout2 = view2.findViewById(R.id.frame_formulae);

        ImageView imageView2 = frameLayout2.findViewById(R.id.formulae);
        TextView desc2 = frameLayout2.findViewById(R.id.description);
        TextView nameTv2 = frameLayout2.findViewById(R.id.namePlanet);

        nameTv2.setText("Геометрия");
        Bitmap bitmapGeo = BitmapFactory.decodeResource(getResources(), R.drawable.geometry);
        imageView2.setImageBitmap(Bitmap.createScaledBitmap(bitmapGeo, width, height, false));
        desc2.setText(R.string.geometryTerm);

        view2.setOnClickListener(v -> {
            //открытие фрагмента с фигурами
            navHostFragment.navigate(R.id.nav_geometry_list);
            /*
            GeometricFigures figures = new GeometricFigures();
            FrameLayout frameLayout1 = view.findViewById(R.id.Mainframe);
            showFragment(figures, frameLayout1);

             */
        });
        linearLayout.addView(view2);

    }
}