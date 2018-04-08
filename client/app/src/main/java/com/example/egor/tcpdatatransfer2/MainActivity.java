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

import static android.app.Activity.RESULT_OK;
import static java.lang.System.exit;

public class MainActivity extends AppCompatActivity {
    private static final int portnumber = 60123;
    private static final String hostname = "192.168.0.103"; //"192.168.0.103"; //"192.168.2.70";
    private final String debugString = "debug";
    private Socket socket = null;
    private int temp = 0; //for ViewFileNamesButton
    private static final int FILE_SELECT_CODE = 0; //for SendFileButton
    private static final int FOLDER_SELECT_CODE_OLD = 1; //for SynchronizeFolderButton [OUTDATED]
    private static final int FOLDER_SELECT_CODE = 2; //for SynchronizeFolderButton [NEW]

    //new vars for DitChooser
    private static final int REQUEST_DIRECTORY = 0;
    private static final String TAG = "DirChooserSample";
    private TextView mDirectoryTextView;


    // TODO final, but can be changed! ok?
    //final EditText editText = (EditText)(findViewById(R.id.editText));

    Button SendFileButton = (Button)(findViewById(R.id.SendFileButton));
    Button SynchronizeFolderButton = (Button)(findViewById(R.id.SynchronizeFolderButton));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO onClick inside of onCreate? Try to rework
        SendFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_CODE);
            }
        });

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





        // TODO onClick inside of onCreate? Try to rework
        SynchronizeFolderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
/*                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("file*//*");
                startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), FOLDER_SELECT_CODE_OLD);*/

                // TODO what is textDirectory for?
                mDirectoryTextView = (TextView) findViewById(R.id.textDirectory);

                final Intent chooserIntent = new Intent(
                        MainActivity.this,
                        DirectoryChooserActivity.class);

                final DirectoryChooserConfig config = DirectoryChooserConfig.builder()
                        .newDirectoryName("DirChooserSample")
                        .allowReadOnlyDirectory(true)
                        .allowNewDirectoryNameModification(true)
                        .build();

                chooserIntent.putExtra(
                        DirectoryChooserActivity.EXTRA_CONFIG,
                        config);

                startActivityForResult(chooserIntent, FOLDER_SELECT_CODE);
            }
        });
    }






    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    System.out.println("File Uti: " + uri.toString());
                    // Get the path
                    final String realPath = getRealPathFromURI(this, uri);
                    System.out.println("Real path: " + realPath);
                    sendFilePackets(realPath);
                }
                break;
            case FOLDER_SELECT_CODE_OLD:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    System.out.println("File Uti: " + uri.toString());
                    // Get the path
                    final String realPath = getRealPathFromURI(this, uri);
                    File directory = new File(new File(realPath).getParent());

                    File[] files = directory.listFiles();
                    for (File f : files) {
                        sendFilePackets(f.toString());
                    }
                }
                break;
            case FOLDER_SELECT_CODE:
                if (requestCode == REQUEST_DIRECTORY) {
                    Log.i(TAG, String.format("Return from DirChooser with result %d",
                            resultCode));

                    if (resultCode == DirectoryChooserActivity.RESULT_CODE_DIR_SELECTED) {
                        mDirectoryTextView
                                .setText(data
                                        .getStringExtra(DirectoryChooserActivity.RESULT_SELECTED_DIR));
                    } else {
                        mDirectoryTextView.setText("nothing selected");
                    }
                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);

            return path;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void sendFilePackets(final String realPath) {
        new Thread() {
            @Override
            public void run() {
                //File outputFile = new File(Environment.getExternalStorageDirectory() + "/1.txt"); //TODO why doesn't work?
                File outputFile = new File(realPath); // /storage/sdcard1/123.txt
                boolean temp = outputFile.exists();
                byte[] bytes = new byte[(int) outputFile.length()];
                try {
                    //EditText editText = (EditText)(findViewById(R.id.editText));
                    socket = new Socket(hostname, portnumber);

                    String fileName = outputFile.getName();
                    byte[] fileNameBytes = fileName.getBytes();

                    Byte[] fileNameBytesWrapper = new Byte[fileNameBytes.length]; // TODO check if works :
                    int i = 0;
                    for (byte b: fileNameBytes)
                        fileNameBytesWrapper[i++] = b; // TODO .

                    Long fileSize = outputFile.length(); // TODO check new variable type
                    OutputStream os = socket.getOutputStream();//tcp stream
                    writeSizeToStream(os, null, fileNameBytesWrapper);
                    os.write(fileNameBytes);

                    /*BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
                    bw.write(fileName);
                    bw.newLine();
                    bw.flush();*/

                    // void writeSizeToStream(InputStream stream, long size); // convert size to 8 bytes array and write it to stream.

                    // void writeStringToStream(InputStream stream, String string); // write 8 bytes long string size to stream. Afterwards write bytes from string.

                    writeSizeToStream(os, fileSize, null);


                    FileInputStream fis = new FileInputStream(outputFile);//new stream to read FROM file
                    int numBytesRead = fis.read(bytes);//reads from file byte by byte to byreArray
                    if (numBytesRead != fileSize) {
                        System.out.println("Wrong number of bytes has been read");
                        exit(1);
                    }
                    os.write(bytes);//writes bytes into stream
                    os.flush();

                    InputStream is = socket.getInputStream();

                    try {
                        int a = is.read();
                    }
                    catch(Throwable e) {
                    }

                    socket.shutdownOutput();

                    fis.close();
                    os.close();

                    socket.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    void writeSizeToStream(OutputStream stream, Long size, Byte[] fileNameBytesWrapper) throws IOException {
        ByteBuffer sizeBuffer = ByteBuffer.allocate(Long.BYTES);
        if (size == null) {
            sizeBuffer.putLong(fileNameBytesWrapper.length);
            stream.write(sizeBuffer.array());
        }
        else if (fileNameBytesWrapper == null) {
            sizeBuffer.putLong(size);
            stream.write(sizeBuffer.array());
        }
        sizeBuffer.clear();//TODO new code.
    }
}