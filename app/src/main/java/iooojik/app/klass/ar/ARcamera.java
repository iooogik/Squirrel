package iooojik.app.klass.ar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import iooojik.app.klass.MainActivity;
import iooojik.app.klass.R;

public class ARcamera extends FragmentActivity implements View.OnClickListener {

    ArFragment arFragment;
    private ModelRenderable solarSystem;
    public static String TYPE;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //определение типа 3д объекта
        if (getIntent().getExtras() != null)
            TYPE = getIntent().getExtras().getString("TYPE");

        setContentView(R.layout.ar_camera_activity);

        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ar_fragment);
        //загрузка и установка модели на найденную плоскость
        loadModel();
        createModel();

        FloatingActionButton back = findViewById(R.id.back);
        back.setOnClickListener(this);
        TextView findSurface = findViewById(R.id.findARsurf);
        findSurface.setOnClickListener(this);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void loadModel() {
        int res = -1;
        //получение файла-ресурса с 3д объектом
        switch (TYPE){
            case ("SolarSystem"):
                //Солнечная система
                 res = R.raw.solar_system;
                 break;
            case ("Sphere"):
                //Сфера
                res = R.raw.sphere;
                break;
            case ("Cube"):
                //Куб
                res = R.raw.cube;
                break;

        }

        //создание рендера модели
        ModelRenderable.builder()
                .setSource(this, res).build()
                .thenAccept(renderable -> solarSystem = renderable)
                .exceptionally(throwable -> {
                    Toast toast =
                            Toast.makeText(this, "Unable to load andy renderable",
                                    Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return null;
                });





    }

    private void createModel(){
        //установка модели на плоскость
        arFragment.setOnTapArPlaneListener(
                (HitResult hitresult, Plane plane, MotionEvent motionevent) -> {
                    if (solarSystem == null){
                        return;
                    }

                    Anchor anchor = hitresult.createAnchor();
                    AnchorNode anchorNode = new AnchorNode(anchor);
                    anchorNode.setParent(arFragment.getArSceneView().getScene());
                    TransformableNode lamp = new TransformableNode(arFragment.getTransformationSystem());
                    lamp.setParent(anchorNode);
                    lamp.setRenderable(solarSystem);
                    lamp.select();
                }
        );

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.back){
            //нажатие "Назад"
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        } else if(v.getId() == R.id.findARsurf){
            //"заново найти поверхность
            recreate();
        }
    }
}
