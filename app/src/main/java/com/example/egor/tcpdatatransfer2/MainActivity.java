package com.example.egor.tcpdatatransfer2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.ByteBuffer;

public class MainActivity extends AppCompatActivity {
    private static final int portnumber = 60123;
    private static final String hostname = "192.168.1.2";
    private final String debugString = "debug";
    private Socket socket = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button SendTextButton = (Button)(findViewById(R.id.SendTextButton));
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
        });



        Button SendFileButton = (Button)(findViewById(R.id.SendFileButton));
        SendFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread() {
                    @Override
                    public void run() {
                        //File inputFile = new File(Environment.getExternalStorageDirectory() + "/1.txt");
                        File inputFile = new File("/storage/sdcard1/123.txt");
                        boolean exitsts = inputFile.exists();
                        byte[] bytes = new byte[(int) inputFile.length()];
                        try {
                            socket = new Socket(hostname, portnumber);

                            String fileName = inputFile.getName();
                            long fileSize = inputFile.length();
                            OutputStream os = socket.getOutputStream();//tcp stream
                            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
                            bw.write(fileName);
                            bw.newLine();
                            bw.flush();

                            ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
                            buffer.putLong(fileSize);
                            os.write(buffer.array());

                            FileInputStream fis = new FileInputStream(inputFile);//new stream to read FROM file
                            int numBytesRead = fis.read(bytes);//reads from file byte by byte to byreArray
                            if (numBytesRead != fileSize) {
                                System.out.println("Wrong number of bytes has been read");
                                System.exit(1);
                            }
                            os.write(bytes);//writes bytes into stream

                            bw.close();
                            fis.close();
                            socket.close();
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
    }
}