package iooojik.app.klass.astronomy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import iooojik.app.klass.Database;
import iooojik.app.klass.R;
import iooojik.app.klass.ar.ARcamera;

public class Planets extends Fragment implements View.OnClickListener {

    private View view;
    private Database mDBHelper;
    private Cursor userCursor;
    private SQLiteDatabase mDb;


    public Planets(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_planets, container ,false);
        Button showAR = view.findViewById(R.id.openAr);
        showAR.setOnClickListener(this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        mDBHelper = new Database(getContext());
        mDBHelper.openDataBase();
        mDBHelper.updateDataBase();
        mDb = mDBHelper.getReadableDatabase();
        userCursor = mDb.rawQuery("Select * from Planets", null);
        getPlanets();

    }

    private void getPlanets(){
        userCursor.moveToLast();
        String name, description;
        Bitmap bitmap = null;
        int max = userCursor.getInt(userCursor.getColumnIndex("_id"));
        userCursor.moveToFirst();
        for (int i = 0; i < max; i++) {
            name = userCursor.getString(userCursor.getColumnIndex("name"));
            description = userCursor.getString(userCursor.getColumnIndex("description"));
            byte[] bytesImg = userCursor.getBlob(userCursor.getColumnIndex("images"));
            if(bytesImg!=null)
            bitmap = BitmapFactory.decodeByteArray(bytesImg, 0, bytesImg.length);
            setInformation(name, description, bitmap, i);
            userCursor.moveToNext();
        }
    }

    private void setInformation(String name, String description, Bitmap bitmap, int id){
        LinearLayout linearLayout = view.findViewById(R.id.linear);
        @SuppressLint("InflateParams")
        View view1 = getLayoutInflater().inflate(R.layout.recycler_view_item_planet, null, false);
        FrameLayout frameLayout = view1.findViewById(R.id.frame_formulae);
        ImageView imageView = frameLayout.findViewById(R.id.formulae);
        TextView desc = frameLayout.findViewById(R.id.description);
        TextView nameTv = frameLayout.findViewById(R.id.namePlanet);
        nameTv.setText(name);
        int width = 300;
        int height = 300;
        if(bitmap != null)
        imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, width, height, false));

        desc.setText(description);

        view1.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putInt("_id", id);
            NavController navHostFragment = NavHostFragment.findNavController(this);
            navHostFragment.navigate(R.id.nav_planets_article, args);
        });

        linearLayout.addView(view1);

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.openAr){
            Intent intent = new Intent(getContext(), ARcamera.class);
            intent.putExtra("TYPE", "SolarSystem");
            startActivity(intent);
        }
    }
}
