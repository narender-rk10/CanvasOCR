package com.example.canvasocr;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.canvasocr.adapter.ViewPagerAdapter;
import com.example.canvasocr.dialogfragment.EraseImageDialogFragment;
import com.example.canvasocr.fragment.PaintFragment;
import com.example.canvasocr.helper.DoodleFragment;
import com.example.canvasocr.helper.LockableViewPager;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    LockableViewPager viewPager;
    Bitmap image;
    private TessBaseAPI mTess;
    String datapath;
    Button convertButton, erase;

    public static ViewPagerAdapter adapter;
    public PaintFragment paint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPagerId);
        viewPager.setSwipeable(false);

        erase = findViewById(R.id.erase);

        convertButton = findViewById(R.id.getocr);

        datapath = getApplicationContext().getFilesDir() + "/tesseract/";

        checkFile(new File(datapath + "tessdata/"));

        String lang = "eng";
        mTess = new TessBaseAPI();
        mTess.init(datapath, lang);

        convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String OCRresult;
                image = DoodleFragment.getDoodleFragment().getDoodleView().getBitmap();
                mTess.setImage(image);
                OCRresult = mTess.getUTF8Text();
                System.out.println("OCRresult"+OCRresult);
                alertDialogDemo(OCRresult);

                if(OCRresult.length() == 0){
                    Toast.makeText(getApplicationContext(), "NO WORDS FOUND!", Toast.LENGTH_LONG).show();
                }
            }
        });


        setFragmentToViewPager();

        erase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EraseImageDialogFragment fragment = new EraseImageDialogFragment();
                fragment.show(getSupportFragmentManager(), "erase paint");

            }
        });
    }

    private void setFragmentToViewPager() {
        paint = new PaintFragment();

        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addToFragment(paint, "Canvas");
        viewPager.setAdapter(adapter);
    }

    private void checkFile(File dir) {
        //directory does not exist, but we can successfully create it
        if (!dir.exists() && dir.mkdirs()) {
            copyFiles(getApplicationContext());
        }
        //The directory exists, but there is no data file in it
        if (dir.exists()) {
            String datafilepath = datapath + "/tessdata/eng.traineddata";
            File datafile = new File(datafilepath);
            if (!datafile.exists()) {
                copyFiles(getApplicationContext());
            }
        }
    }

    private void copyFiles(Context context) {
        try {
            String filepath = datapath + "/tessdata/eng.traineddata";
            AssetManager assetManager = context.getAssets();
            InputStream instream = assetManager.open("tessdata/eng.traineddata");
            OutputStream outstream = new FileOutputStream(filepath);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read);
            }
            outstream.flush();
            outstream.close();
            instream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("File Not Found", e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("IO Error", e.toString());
        }
    }
    public void alertDialogDemo(final String ocrtext) {
        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        View promptsView = li.inflate(R.layout.alert_dialog, null);
        final EditText userInput = (EditText) promptsView.findViewById(R.id.etUserInput);
        userInput.setText(ocrtext);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

        alertDialogBuilder.setView(promptsView);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getApplicationContext(), "Entered: "+ocrtext, Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }
}