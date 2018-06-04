package com.example.egor.tcpdatatransfer2;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.ByteBuffer;


import net.rdrei.android.dirchooser.DirectoryChooserActivity;
import net.rdrei.android.dirchooser.DirectoryChooserConfig;

import android.widget.TextView;

import static java.lang.System.exit;

public class MainActivity extends AppCompatActivity {

    Model model = Model.getModelInstance();

    private TextView mDirectoryTextView;
    private static final int FOLDER_SELECT_CODE_OLD = 1; //for SynchronizeFolderButton [OUTDATED]
    private static final int FOLDER_SELECT_CODE = 2; //for SynchronizeFolderButton [NEW]

    //new vars for DirChooser
    private static final int REQUEST_DIRECTORY = 0;
    private static final String TAG = "DirChooserSample";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

/*        Button ViewFileNamesButton = (Button)(findViewById(R.id.ViewFileNamesButton));
        ViewFileNamesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String path = "/storage/sdcard1/"; //Environment.getExternalStorageDirectory().toString();
                File directory = new File(path);
                File[] files = directory.listFiles();
                EditText editText = (EditText)(findViewById(R.id.editText));
                editText.setText(files[temp].getName());
                temp++;
            }
        });*/

/*        Button Temp = (Button)(findViewById(R.id.Temp));
        Temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File outputFile = new File("/storage/sdcard1/123.txt");// /sdcard/test.txt  // /storage/sdcard1/123.txt
                byte[] bytes = new byte[5];
                bytes[0] = 'h';
                bytes[1] = 'e';
                bytes[2] = 'l';
                bytes[3] = 'l';
                bytes[4] = 'o';
                int temp = 0;
                boolean fileExists = outputFile.exists();
                boolean temp1 = outputFile.canWrite();
                boolean temp2 = outputFile.canRead();
                try {
                    FileWriter fw = new FileWriter(outputFile);
                    fw.append("Hello world");
                    fw.flush();
                    fw.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    editText.setText("FileNotFoundException");
                    temp = 1;
                } catch (IOException e) {
                    e.printStackTrace();
                    editText.setText("IOException");
                    temp = 1;
                }
                if (temp == 0)
                    editText.setText("SUCCESS, YAY ^-^");
            }
        });*/
    }

    public void sendFileButtonClick(View view) {
        Intent intent = new Intent(this, ChooseIPActivity.class);
        startActivity(intent);
    }

    public void synchronizeFolderButtonClick(View view) {
        final Intent chooserIntent = new Intent(this, DirectoryChooserActivity.class);

        final DirectoryChooserConfig config = DirectoryChooserConfig.builder()
                .newDirectoryName("DialogSample")
                .allowNewDirectoryNameModification(true)
                .allowReadOnlyDirectory(true)
                .initialDirectory(Environment.getExternalStorageDirectory().getPath())
                .build();

        chooserIntent.putExtra(DirectoryChooserActivity.EXTRA_CONFIG, config);

        // REQUEST_DIRECTORY is a constant integer to identify the request, e.g. 0
        startActivityForResult(chooserIntent, REQUEST_DIRECTORY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_DIRECTORY) {
            if (resultCode == DirectoryChooserActivity.RESULT_CODE_DIR_SELECTED) {
                String a = model.handleDirectoryChoice(data
                        .getStringExtra(DirectoryChooserActivity.RESULT_SELECTED_DIR));
            } else {
                // Nothing selected
            }
        }
    }
}