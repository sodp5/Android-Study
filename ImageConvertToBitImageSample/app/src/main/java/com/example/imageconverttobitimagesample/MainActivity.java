package com.example.imageconverttobitimagesample;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {
    private static final int SET_BMP_IMG = 1;

    private final String TAG = getClass().getSimpleName();
    String mPath = Environment.getExternalStorageDirectory().toString() + "/test" + ".bmp";
    String s = "";
    String short_s;
    int length;

    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnLog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((EditText)findViewById(R.id.edtPrice)).setText("");
                ((ImageView) findViewById(R.id.imageView)).setImageResource(R.drawable.ic_launcher_foreground);
            }
        });

        findViewById(R.id.btnSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.btnSave).setVisibility(View.GONE);
                findViewById(R.id.edtPrice).setBackground(null);
                findViewById(R.id.edtPrice).setEnabled(false);
                takeScreenshot();

            }
        });

        findViewById(R.id.btnOpen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "parsing...", Toast.LENGTH_SHORT).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        byte[] bytes = bitmapConvertToByteArray();
                        Log.d(TAG, "parseToHex");

                        if(s.equals("")) {
                            short_s = byteArrayToHex(bytes, 300);
                            s = byteArrayToHex(bytes, bytes.length);
                        }
                        Log.d(TAG, s);

                        Log.d(TAG, "parseToByte[]");
                        bytes = hexStringToByteArray(s);
                        Log.d(TAG, "complete");

                        Message message = Message.obtain(handler, SET_BMP_IMG, bytes);
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });
    }

    private void takeScreenshot() {
        try {
            View v1 = findViewById(R.id.frameLayout);
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);

            outputStream.flush();
            outputStream.close();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private byte[] bitmapConvertToByteArray() {
        Bitmap bitmap = BitmapFactory.decodeFile(mPath);

        Log.d(TAG, "parsing...");
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        Log.d(TAG, "complete!");
        return imageBytes;
    }

    private Bitmap byteArrayToBitmap(byte[] bytes) {
        Bitmap bitmap = null;
        bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        bytes = null;
        return bitmap;
    }

    private String byteArrayToHex(byte[] a, int length) {
        StringBuilder sb = new StringBuilder();
        this.length = length;
        for (int i = 0; i < length; i++)
            sb.append(String.format("%02x", a[i]&0xff));
        return sb.toString();
    }

    private byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    private Bitmap GetBinaryBitmap(Bitmap bitmap_src) {
        Bitmap bitmap_new=bitmap_src.copy(bitmap_src.getConfig(), true);
        for(int x=0; x<bitmap_new.getWidth(); x++) {
            for(int y=0; y<bitmap_new.getHeight(); y++) {
                int color=bitmap_new.getPixel(x, y);
                color=GetNewColor(color);
                bitmap_new.setPixel(x, y, color);
            }
        }
        return bitmap_new;
    }

    private int GetNewColor(int c) {
        double dwhite=GetColorDistance(c,Color.WHITE);
        double dblack=GetColorDistance(c,Color.BLACK);
        if(dwhite<=dblack) {
            return Color.WHITE;
        }
        else {
            return Color.BLACK;
        }
    }
    private double GetColorDistance(int c1, int c2) {
        int db= Color.blue(c1)-Color.blue(c2);
        int dg=Color.green(c1)-Color.green(c2);
        int dr=Color.red(c1)-Color.red(c2);
        double d=Math.sqrt(  Math.pow(db, 2) + Math.pow(dg, 2) +Math.pow(dr, 2)  );
        return d;
    }

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case SET_BMP_IMG:
                    Log.d(TAG, "receiveMsg");
                    ((ImageView) findViewById(R.id.imageView)).setImageBitmap(byteArrayToBitmap((byte[])msg.obj));
                    Toast.makeText(MainActivity.this, "length=" + length + ",\n" + short_s, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
}
