package com.example.egor.tcpdatatransfer2;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
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

import static java.lang.System.exit;

public class MainActivity extends AppCompatActivity {
    private static final int portnumber = 60123;
    private static final String hostname = "192.168.1.2";
    private final String debugString = "debug";
    private Socket socket = null;
    private int temp = 0; //for ViewFileNamesButton
    private static final int FILE_SELECT_CODE = 0; //for ViewFilesButton

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

/*        Button SendTextButton = (Button)(findViewById(R.id.SendTextButton));
        SendTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            //Connecting
                            Log.i(debugString, "ATTEMPTING TO CONNECT TO SERVER");
                            socket = new Socket(hostname, portnumber);
                            Log.i(debugString, "CONNECTION ESTABLISHED");

                            // send message to server
                            EditText editText = (EditText)(findViewById(R.id.editText));
                            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                            bw.write(editText.getText().toString());
                            bw.newLine();
                            bw.flush();

                            //receive message from server
                            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            System.out.println("Message from server: " + br.readLine());
                        }
                        catch (IOException e) {
                            Log.e(debugString, e.getMessage());
                        }
                    }
                }.start();
            }
        });*/



        Button SendFileButton = (Button)(findViewById(R.id.SendFileButton));
        SendFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_CODE);
            }
        });

        Button ViewFileNamesButton = (Button)(findViewById(R.id.ViewFileNamesButton));
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
        });

        Button Temp = (Button)(findViewById(R.id.Temp));
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
                EditText editText = (EditText)(findViewById(R.id.editText));
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

                    new Thread() {
                        @Override
                        public void run() {
                            //File outputFile = new File(Environment.getExternalStorageDirectory() + "/1.txt"); //TODO why doesn't work?
                            File outputFile = new File(realPath); // /storage/sdcard1/123.txt
                            boolean temp = outputFile.exists();
                            byte[] bytes = new byte[(int) outputFile.length()];
                            try {
                                socket = new Socket(hostname, portnumber);
                                String fileName = outputFile.getName();
                                long fileSize = outputFile.length();
                                OutputStream os = socket.getOutputStream();//tcp stream
                                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
                                bw.write(fileName);
                                bw.newLine();
                                bw.flush();

                                ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
                                buffer.putLong(fileSize);
                                os.write(buffer.array());

                                FileInputStream fis = new FileInputStream(outputFile);//new stream to read FROM file
                                int numBytesRead = fis.read(bytes);//reads from file byte by byte to byreArray
                                if (numBytesRead != fileSize) {
                                    System.out.println("Wrong number of bytes has been read");
                                    exit(1);
                                }
                                os.write(bytes);//writes bytes into stream

                                InputStream is = socket.getInputStream();

                                try {
                                    int a = is.read();
                                }
                                catch(Throwable e) {
                                }

                                socket.shutdownOutput();

                                //------------------------------------------        //RECIEVE FILE FROM SERVER
                                /*System.out.println("Client receiving file...");

                                InputStream is = socket.getInputStream();
                                BufferedReader br = new BufferedReader((new InputStreamReader(is)));
                                String iFilePath = "/storage/sdcard1/" + br.readLine(); // /storage/sdcard1/
                                File inputFile = new File(iFilePath);
                                //File file = new File(context.getFilesDir(), filename);
                                System.out.println(iFilePath);

                                byte[] iFileSizeBytes = new byte[Long.BYTES];
                                int iNumBytesRead = is.read(iFileSizeBytes, 0, Long.BYTES);
                                if (iNumBytesRead != Long.BYTES) {
                                    System.out.println("Wrong number of bytes has been received");
                                    System.exit(1);
                                }

                                ByteBuffer iBuffer = ByteBuffer.allocate(Long.BYTES);
                                iBuffer.put(iFileSizeBytes);
                                iBuffer.flip();
                                long iFileSize = iBuffer.getLong();
                                System.out.println(iFileSize);

                                int iBytesAlreadyRead = 0;
                                iNumBytesRead = 0;
                                byte[] iFileData = new byte[(int) iFileSize];
                                //inputFile.createNewFile();
                                boolean fileExsists = inputFile.exists();
                                FileOutputStream fos = new FileOutputStream(inputFile);
                            *//*String iFileName = iFilePath.substring(17);
                            FileOutputStream fos = openFileOutput(iFileName, MODE_APPEND);*//*
                                while (iBytesAlreadyRead != iFileSize) {
                                    iNumBytesRead += is.read(iFileData, iBytesAlreadyRead, (int) iFileSize - iBytesAlreadyRead);
                                    iBytesAlreadyRead = iNumBytesRead;
                                }
                                fos.write(iFileData);//writes from byteArray into fos
                                System.out.print("Client received file");*/

                                fis.close();
                                bw.close();
                                os.close();

                                /*fos.close();
                                br.close();
                                is.close();*/

                                socket.close();
                            }
                            catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
                break;
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
}