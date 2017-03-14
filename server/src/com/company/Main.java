package com.company;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Main {

    private static final int portnumber = 60123;

    public static void main(String[] args) {
        File tempFile = new File("D:\\Java_Projects\\Android_Projects\\fileFromServer.txt");
        if (tempFile.exists())
            System.out.println("Before Format : " + tempFile.lastModified());

        //while(true) {
        ServerSocket serverSocket = null;
        try {


            System.out.println("Server starting at port number: " + portnumber);
            serverSocket = new ServerSocket(portnumber);

            //client connecting
            System.out.println("Waiting for clients to connect");
            Socket socket = serverSocket.accept();
            System.out.println("A client has connected");

            InputStream is = socket.getInputStream();
                /*BufferedReader br = new BufferedReader((new InputStreamReader(is)));
                String iFilePath = "D:/Java_Projects/Android_Projects/" + br.readLine();*/

            // long readSizeFromStream(InputStream stream); // read 8 bytes from stream, convert it to long and return it.
            // String readStringFromStream(InputStream stream); // read 8 bytes long string length from stream. Afterwards read string bytes (UTF-16).

            // void writeSizeToStream(InputStream stream, long size); // convert size to 8 bytes array and write it to stream.
            // void writeStringToStream(InputStream stream, String string); // write 8 bytes long string size to stream. Afterwards write bytes from string.

            long fileNameSize = readSizeFromStream(is);//TODO can we use long here instead of int? And do we need long?
            String fileName = readStringFromStream(is, fileNameSize);
            File inputFile = new File("D:/Java_Projects/Android_Projects/" + fileName);
            System.out.println(fileName);

            long fileSize = readSizeFromStream(is);
            System.out.println(fileSize);

            int iBytesAlreadyRead = 0;
            int iNumBytesRead = 0;
            byte[] iFileData = new byte[(int) fileSize];
            FileOutputStream fos = new FileOutputStream(inputFile);
            while (iBytesAlreadyRead != fileSize) {
                iNumBytesRead += is.read(iFileData, iBytesAlreadyRead, (int) fileSize - iBytesAlreadyRead);
                iBytesAlreadyRead = iNumBytesRead;
            }
            fos.write(iFileData);//writes from byteArray into fos

            //socket.shutdownInput();
            socket.shutdownOutput();

            fos.close();
            is.close();

            socket.close();
            serverSocket.close();

            //---------------------------------------           //VIEW ALL FILE NAMES
    /*
                File folder = new File("D:/Java_Projects/Android_Projects");
                File[] listOfFiles = folder.listFiles();

                for (int i = 0; i < listOfFiles.length; i++) {
                    if (listOfFiles[i].isFile()) {
                        System.out.println("File " + listOfFiles[i].getName());
                    } else if (listOfFiles[i].isDirectory()) {
                        System.out.println("Directory " + listOfFiles[i].getName());
                    }
                }*/

            System.out.println("The server has ended");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //}
    }

    private static long readSizeFromStream(InputStream stream) {  //TODO why should is be static?
        ByteBuffer sizeBuffer = ByteBuffer.allocate(Long.BYTES);
        byte[] sizeArray = new byte[Long.BYTES];
        int NumBytesRead = 0;
        try {
            NumBytesRead = stream.read(sizeArray);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (NumBytesRead != Long.BYTES) {
            System.out.println("Wrong number of bytes has been received: " + NumBytesRead);
            System.exit(1);
        }
        sizeBuffer.put(sizeArray);
        sizeBuffer.flip();
        return sizeBuffer.getLong();
    }

    private static String readStringFromStream(InputStream stream, long fileNameSize) {
        int NumBytesRead = 0;
        String filePath = null;

        byte[] fileNameBytes = new byte[(int) fileNameSize];//TODO check cast to int
        try {
            NumBytesRead = stream.read(fileNameBytes);
            if (NumBytesRead != fileNameSize) {
                System.out.println("Wrong number of bytes has been received: " + NumBytesRead);
                System.exit(1);
            }
            filePath = new String(fileNameBytes, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filePath;
    }
}
