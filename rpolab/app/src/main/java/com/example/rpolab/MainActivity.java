package com.example.rpolab;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.annotation.SuppressLint;
import android.os.Bundle;
//import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

//import java.nio.charset.StandardCharsets;
//import java.util.Arrays;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.rpolab.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements TransactionEvents {

    // Used to load the 'rpolab' library on application startup.
    static {
        System.loadLibrary("rpolab");
        System.loadLibrary("mbedcrypto");
    }

    private ActivityMainBinding binding;

    ActivityResultLauncher activityResultLauncher;
    private String pin;

    public native boolean transaction(byte[] trd);

    @Override
    public String enterPin(int ptc, String amount) {
        pin = new String();
        Intent it = new Intent(MainActivity.this, PinpadActivity.class);
        it.putExtra("ptc", ptc);
        it.putExtra("amount", amount);
        synchronized (MainActivity.this) {
            activityResultLauncher.launch(it);
            try {
                MainActivity.this.wait();
            } catch (Exception ex) {
                //todo: log error
            }
        }
        return pin;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initRng();

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult> () {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            pin = data.getStringExtra("pin");
                            synchronized (MainActivity.this){
                                MainActivity.this.notifyAll();
                            }
                        }
                    }
                });
    }

    public static byte[] stringToHex(String s) {
        byte[] hex;

        try {
            hex = Hex.decodeHex(s.toCharArray());
        } catch (DecoderException ex) {
            hex = null;
        }

        return hex;
    }

    public void onButtonClick(View v) {
        /*        new Thread(()-> {
             try {
                 byte[] trd = stringToHex("9F0206000000000100");
                 boolean ok = transaction(trd);
                 runOnUiThread(()-> {
                     Toast.makeText(MainActivity.this, ok ? "ok" : "failed", Toast.LENGTH_SHORT).show();
                 });
             } catch (Exception ex) {
                 // todo: log error
             }
         }).start(); */
        //byte[] trd = stringToHex("9F0206000000000100");
        //transaction(trd);
        testHttpClient();
    }

    @Override
    public void transactionResult(boolean result) {
        runOnUiThread(()-> {
            Toast.makeText(MainActivity.this, result ? "ok" : "failed", Toast.LENGTH_SHORT).show();
        });
    }

    protected void testHttpClient() {
        new Thread(() -> {
            try {
                HttpURLConnection uc = (HttpURLConnection)
                        (new URL("http://10.0.2.2:8081/api/v1/title").openConnection());
                InputStream inputStream = uc.getInputStream();
                String html = IOUtils.toString(inputStream);
                String title = getPageTitle(html);
                runOnUiThread(() -> {
                    Toast.makeText(this, title, Toast.LENGTH_LONG).show();
                });
            } catch (Exception ex) {
                Log.e("fapptag", "Http client fails", ex);
            }
        }).start();
    }

//    protected String getPageTitle(String html) {
//        int pos = html.indexOf("<title");
//        String p="not found";
//        if (pos >= 0) {
//            int pos2 = html.indexOf("<", pos + 1);
//            if (pos >= 0)
//                p = html.substring(pos + 7, pos2);
//        }
//        return p;
//    }

    protected String getPageTitle(String html) {
        Pattern pattern = Pattern.compile("<title>(.+)</title>", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(html);
        String p;
        if (matcher.find())
            p = matcher.group(1);
        else
            p = "Not found";
        return p;
    }


    /**
     * A native method that is implemented by the 'rpolab' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
    public static native int initRng();
    public static native byte[] randomBytes(int no);
    public static native byte[] encrypt(byte[] key, byte[] data);
    public static native byte[] decrypt(byte[] key, byte[] data);
}