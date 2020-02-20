package iooogik.app.modelling;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

public class QR_Demo extends AppCompatActivity {

    private Database mDBHelper;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr__demo);

        mDBHelper = new Database(this);
        mDBHelper.openDataBase();

        final ImageView imageView = findViewById(R.id.imageView);

        Button saveBtn = findViewById(R.id.save);

        final LinearLayout mainLayout  = new LinearLayout(this);
        final LinearLayout layout1 = new LinearLayout(this);
        final Intent intent = new Intent(this, Notes.class);
        saveBtn.setOnClickListener(v -> {
            Bitmap bitmap = null;
            try {
                bitmap = encodeAsBitmap(getQr());
            } catch (WriterException e) {
                e.printStackTrace();
            }
            if(bitmap != null){
                final ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 2, stream);


                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                mainLayout.setOrientation(LinearLayout.VERTICAL);
                layout1.setOrientation(LinearLayout.HORIZONTAL);
                final EditText name = new EditText(getApplicationContext());
                name.setHint("Введите имя");

                final Typeface tpf = Typeface.createFromAsset(getAssets(), "rostelekom.otf");

                name.setTypeface(tpf);
                name.setTextSize(18);
                name.setMinHeight(15);

                layout1.addView(name);
                mainLayout.addView(layout1);
                builder.setView(mainLayout);

                builder.setPositiveButton(Html.fromHtml
                                ("<font color='#7AB5FD'>Добавить запись</font>"),
                        (dialog, which) -> {
                            try {
                                saveQRandText(name.getText().toString(),
                                        stream.toByteArray());
                                startActivity(intent);
                                finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(),
                                        "Что-то пошло не так" + e,
                                        Toast.LENGTH_LONG).show();
                            }
                        });

                builder.setCancelable(true);

                AlertDialog dlg = builder.create();

                dlg.setOnShowListener(dialog -> {
                    Window v1 = ((AlertDialog)dialog).getWindow();
                    assert v1 != null;
                    v1.setBackgroundDrawableResource(R.drawable.alert_dialog_backgrond);
                    Button posButton = ((AlertDialog)dialog).
                            getButton(DialogInterface.BUTTON_POSITIVE);
                    posButton.setTypeface(tpf);
                    posButton.setTypeface(Typeface.DEFAULT_BOLD);
                    posButton.setTextColor(R.color.colorFont);
                });



                dlg.show();

            }else {
                Toast.makeText(getApplicationContext(), "Что-то пошло не так",
                        Toast.LENGTH_LONG).show();
            }

        });



        getQr();

        try {
            Bitmap btm = encodeAsBitmap(getQr());
            imageView.setImageBitmap(btm);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    protected String getQr(){
        TextView tv = findViewById(R.id.encodedText);
        Bundle arguments = getIntent().getExtras();
        assert arguments != null;
        String qr_text = arguments.getString("qr_text");
        tv.setText(qr_text);
        return qr_text;
    }

    Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, 200, 200, null);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, 200, 0, 0, w, h);
        return bitmap;
    }

    protected void saveQRandText(String name, byte[] image){
        TextView tv = findViewById(R.id.encodedText);

        SQLiteDatabase mDb = mDBHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();

        String type = "standart";

        String fullName = "QR " + name;

        cv.put("_id", Notes.id);
        cv.put("name", fullName);
        cv.put("shortName", tv.getText().toString());
        cv.put("text", " ");
        cv.put("image", image);
        cv.put("type", type);

        //получение даты
        Date currentDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy",
                Locale.getDefault());
        String dateText = dateFormat.format(currentDate);
        cv.put("date", dateText);
        //запись
        mDb.insert("Notes", null, cv);
        mDb.close();
        Notes.id++;
        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
        Notes.ITEMS.add(new Note(fullName, tv.getText().toString(), bitmap, type, Notes.id));
        Notes.NOTES_ADAPTER.notifyDataSetChanged();
        bitmap = null;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, BarcodeCaptureActivity.class);
        startActivity(intent);
        finish();
    }
}
