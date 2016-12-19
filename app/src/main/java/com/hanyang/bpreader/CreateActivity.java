package com.hanyang.bpreader;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

public class CreateActivity extends AppCompatActivity implements View.OnLongClickListener {
    Bitmap barcode_bitmap;
    CardView barcode_card_view;
    private static final int REQUEST_WRITE_STORAGE = 112;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        ImageView barcode_image = (ImageView) findViewById(R.id.barcodeImageView);
        barcode_card_view = (CardView) findViewById(R.id.barcode_image_card_view);
        Intent intent = getIntent();
        String data = intent.getStringExtra("data");
        if(data != null) {
            try {
                barcode_bitmap = encodeAsBitmap(data, BarcodeFormat.PDF_417, 1800, 900);
                barcode_image.setImageBitmap(barcode_bitmap);
                barcode_card_view.setVisibility(View.VISIBLE);
            } catch (WriterException e) {
                e.printStackTrace();
            }
            barcode_card_view.setOnLongClickListener(this);
        } else {
            finish();
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this,Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_WRITE_STORAGE);
        } else {
            saveBitmapToJpg(barcode_bitmap, "BPReader", new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss", Locale.US).format(System.currentTimeMillis()));
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_WRITE_STORAGE:

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    saveBitmapToJpg(barcode_bitmap, "BPReader", new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss", Locale.US).format(System.currentTimeMillis()));
                } else {
                    Toast.makeText(this, "Permission denied. \nBPReader app requires External Storage Write Permission to save image on external storage.", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }
    public void saveBitmapToJpg(Bitmap bitmap,String folder, String name) {
        String ex_storage = Environment.getExternalStorageDirectory().getAbsolutePath();
        // Get Absolute Path in External Sdcard
        String folder_name = "/" + folder+ "/";
        String file_name = name + ".jpg";
        String string_path = ex_storage + folder_name;

        File file_path;
        try{
            file_path = new File(string_path);
            if(!file_path.exists()){
                Log.i("CreateActivity", "Making Folder: "+ string_path + file_path.mkdirs());
            }
            FileOutputStream out = new FileOutputStream(string_path + file_name);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
            Toast.makeText(CreateActivity.this, "Image saved to Internal Storage: ." + string_path, Toast.LENGTH_SHORT).show();
        }catch(FileNotFoundException exception){
            exception.printStackTrace();
        }catch(IOException exception){
            Log.e("IOException", exception.getMessage());
        }
    }


    Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int img_width, int img_height) throws WriterException {
        String contentsToEncode = contents;
        if (contentsToEncode == null) {
            return null;
        }
        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contentsToEncode);
        if (encoding != null) {
            hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;
        try {
            result = writer.encode(contentsToEncode, format, img_width, img_height, hints);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private static String guessAppropriateEncoding(CharSequence contents) {
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }        return null;
    }


}
