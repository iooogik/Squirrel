package iooojik.app.klass.subjects.ar;

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

import iooojik.app.klass.R;

public class ARcamera extends FragmentActivity implements View.OnClickListener {

    ArFragment arFragment;
    private ModelRenderable solarSystem;
    public int TYPE;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //определение типа 3д объекта
        if (getIntent().getExtras() != null)
            TYPE = getIntent().getExtras().getInt("TYPE");

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
        //получение файла-ресурса с 3д объектом
        //создание рендера модели
        ModelRenderable.builder()
                .setSource(this, TYPE).build()
                .thenAccept(renderable -> solarSystem = renderable)
                .exceptionally(throwable -> {
                    Toast toast =
                            Toast.makeText(this, "Невозможно загрузить объект",
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
            super.onBackPressed();
        } else if(v.getId() == R.id.findARsurf){
            //"заново найти поверхность
            recreate();
        }
    }
}
