package my.iooogik.Book;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

public class ARcamera extends FragmentActivity implements View.OnClickListener {

    ArFragment arFragment;
    private ModelRenderable solarSystem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.planets_show);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ar_fragment);
        setupModel();
        createModel();

        FloatingActionButton back = findViewById(R.id.back);
        back.setOnClickListener(this);

    }

    private void setupModel() {
        ModelRenderable.builder()
                .setSource(this, R.raw.solar).build()
                .thenAccept(renderable -> solarSystem = renderable)
                .exceptionally(throwable -> {
                    Toast toast =
                            Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return null;
                });
    }

    private void createModel(){

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
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }
}
