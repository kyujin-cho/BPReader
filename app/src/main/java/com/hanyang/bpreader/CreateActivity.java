/**
 * CreateActivity
 * 참고
 * http://code.google.com/p/zxing/source/browse/trunk/android/src/com/google/zxing/client/android/encode/EncodeActivity.java
 * http://code.google.com/p/zxing/source/browse/trunk/android/src/com/google/zxing/client/android/encode/QRCodeEncoder.java

 * @author Kyujin Cho
 * @version 1.0
 * @contributor Jaehyeon Ahn
 * @see com.hanyang.bpreader.MainActivity
*/

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

    /**
     * Activity 가 처음 생성될 때 실행되는 메소드. 초기 변수 및 UI Component의 전역 변수들을 정의.
     * 여기서는 Barcode Image가 보여질 barcode_card_view를 정의하고, Intent 전환시 입력된 data를 barcode bitmap으로 전환시켜 보여준다.
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        ImageView barcode_image = (ImageView) findViewById(R.id.barcodeImageView); // XML Layout에서 ImageView를 찾아서 가져옴
        barcode_card_view = (CardView) findViewById(R.id.barcode_image_card_view); // XML Layout에서 CardView를 찾아서 가져옴
        Intent intent = getIntent(); // 액티비티가 실행될 때 전달된 값을 가져오기 위해 Intent 가져옴
        String data = intent.getStringExtra("data"); // 가져온 Intent에서 "data"라는 이름의 String 가져옴
        if(data != null) { // data 값이 null이 아니면
            try {
                barcode_bitmap = encodeAsBitmap(data, BarcodeFormat.PDF_417, 1800, 900); // 가져온 data String을 바탕으로 PDF417 형식의 Barcode를 Bitmap Image로 렌더
                barcode_image.setImageBitmap(barcode_bitmap); // 렌더된 Bitamp을 ImageView에 전달
                barcode_card_view.setVisibility(View.VISIBLE); 
            } catch (WriterException e) {
                e.printStackTrace();
            }
            barcode_card_view.setOnLongClickListener(this); // Image 를 길게 클릭하면 저장하게 하기 위헤 Image에 LongClickListener를 달아줌
        } else {
            finish();
        }
    }

    /**
     * 이미지를 길게 누를 시, 이미지를 스토리지에 저장
     * Android 6.0 이상일 경우, 권한을 따로 획득해야 하기 때문에 Android 6.0 이상인지를 체크하는 조건문 추가
     * @param view 길게 눌러진 View
     * @return
     */
    @Override
    public boolean onLongClick(View view) {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this,Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED) { // Android 6.0 이상이고, 권한을 얻지 못했는지를 체크
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_WRITE_STORAGE); // 권한을 얻지 못했다면 권한을 요청
        } else { // Android 6.0 이하거나 권한을 획득했을 경우
            saveBitmapToJpg(barcode_bitmap, "BPReader", new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss", Locale.US).format(System.currentTimeMillis())); // /sdcard/BPReader/ 폴더에 이미지를 저장
        }
        return false;
    }

    /**
     * 권한 요청 창에서 행동이 이루어졌을 경우
     * @param requestCode 권한 코드
     * @param permissions 요청한 권한 목록
     * @param grantResults 권한 요청의 결과
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_WRITE_STORAGE: // 요청한 권한이 쓰기 권한일 경우
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) { // 권한이 획득되었을 경우
                    saveBitmapToJpg(barcode_bitmap, "BPReader", new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss", Locale.US).format(System.currentTimeMillis())); // /sdcard/BPReader/ 폴더에 이미지를 저장
                } else {
                    Toast.makeText(this, "Permission denied. \nBPReader app requires External Storage Write Permission to save image on external storage.", Toast.LENGTH_SHORT).show(); // 권한 획득에 실패했음을 알려줌
                }
        }
    }

    /**
     * 비트맵 객체를 스토리지의 특정 위치에 JPG 형태로 저장해주는 메소드
     * @param bitmap 저장할 bitmap 객체
     * @param folder 저장할 위치
     * @param name 파일 이름
     */
    public void saveBitmapToJpg(Bitmap bitmap,String folder, String name) {
        String ex_storage = Environment.getExternalStorageDirectory().getAbsolutePath(); // 저장할 위치의 Absolute Path 를 불러옴
        String folder_name = "/" + folder+ "/"; // Folder 이름 구성
        String file_name = name + ".jpg"; // File 이름 구성
        String string_path = ex_storage + folder_name; // 파일이 저장될 Path 생성

        File file_path;
        try{
            file_path = new File(string_path); // 파일 경로로 파일을 읽기
            if(!file_path.exists()){ // 파일을 저장할 폴더가 저장되지 않을 경우
                file_path.mkdirs(); // 해당 위치에 폴더를 생성
            }
            FileOutputStream out = new FileOutputStream(string_path + file_name); // File 을 저장하기 위해 Stream 열기

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out); // 열어놓은 Stream으로 Bitmap을 JPG 형태로 압축 저장
            out.close();
            Toast.makeText(CreateActivity.this, "Image saved to Internal Storage: ." + string_path, Toast.LENGTH_SHORT).show(); // 이미지가 저장되었음을 알려줌
        }catch(FileNotFoundException exception){
            exception.printStackTrace();
        }catch(IOException exception){
            Log.e("IOException", exception.getMessage());
        }
    }

    /**
     * String을 Bitmap 형태의 바코드로 바꾸어 주는 메소드
     * @param contents 변환할 String
     * @param format 바코드의 종류
     * @param img_width 이미지 세로 길이
     * @param img_height 이미지 가로 길이
     * @return 생성된 Bitmap 객체
     * @throws WriterException Bitmap 작성 중 발생할 수 있음
     */
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

    /**
     * 입력받은 Charsequence가 올바른 UTF-8 인코딩으로 구성되어 있는지를 판단하는 메소드
     * @param contents 인코딩을 판단할 Charsequence
     * @return 인코딩 종류
     */
    private static String guessAppropriateEncoding(CharSequence contents) {
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }        return null;
    }


}
