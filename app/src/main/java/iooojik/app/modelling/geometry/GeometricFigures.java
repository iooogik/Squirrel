package iooojik.app.modelling.geometry;


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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import iooojik.app.modelling.ARcamera;
import iooojik.app.modelling.Database;
import iooojik.app.modelling.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class GeometricFigures extends Fragment implements View.OnClickListener{

    public GeometricFigures() {}

    View view;
    private Database mDBHelper;
    private Cursor userCursor;
    private SQLiteDatabase mDb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_geometric_figures, container, false);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mDBHelper = new Database(getContext());
        mDBHelper.openDataBase();
        mDBHelper.updateDataBase();
        mDb = mDBHelper.getReadableDatabase();
        userCursor = mDb.rawQuery("Select * from Geometry", null);
        getFigures();
    }

    private void getFigures(){
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
            String type = "";
            switch (name){
                case "Сфера":
                    type = "Sphere";
                    break;
                case "Куб":
                    type = "Cube";
                    break;
            }
            setInformation(name, description, bitmap, type);
            userCursor.moveToNext();
        }
    }

    private void setInformation(String name, String description, Bitmap bitmap, String type){
        LinearLayout linearLayout = view.findViewById(R.id.linear);
        @SuppressLint("InflateParams")
        View view1 = getLayoutInflater().inflate(R.layout.item_planet, null, false);
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
            Intent intent = new Intent(getContext(), ARcamera.class);
            intent.putExtra("TYPE", type);
            startActivity(intent);
        });

        linearLayout.addView(view1);

    }

    @Override
    public void onClick(View v) {}
}
