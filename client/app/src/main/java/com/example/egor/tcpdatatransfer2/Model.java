package com.example.egor.tcpdatatransfer2;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.exit;

// TODO singleton, check if using model instances in other classes is ok
public final class Model {

    private static Model modelInstance;

    private Socket socket = null;
    private static final int portnumber = 60123;

    private Model() {}

    public static synchronized Model getModelInstance() {
        if (modelInstance == null)
            modelInstance = new Model();
        return modelInstance;
    }

    // TODO check access modifier for all methods!!!
    public String getRealPathFromURI(Context context, Uri contentUri) {
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

    // TODO why FINAL String realPath, hostname?
    public void sendFilePackets(final String realPath, final String hostname) {
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

    public void writeSizeToStream(OutputStream stream, Long size, Byte[] fileNameBytesWrapper) throws IOException {
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

    public boolean checkUserInputIp(String userInput) {
        //   (^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3})(?=([0-9]{1,3}$))
        Pattern p = Pattern.compile("(^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3})(?=([0-9]{1,3}$))");
        Matcher m = p.matcher(userInput);
        return m.lookingAt();
    }
}
