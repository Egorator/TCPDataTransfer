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


            ServerSocket serverSocket = null;
            try {
                /*System.out.println("Server starting at port number: " + portnumber);
                serverSocket = new ServerSocket(portnumber);

                //client connecting
                System.out.println("Waiting for clients to connect");
                Socket socket = serverSocket.accept();
                System.out.println("A client has connected");

                //Send message to client
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                bw.write("This is message from server");
                bw.newLine();
                bw.flush();

                //Receive message from client
                BufferedReader br = new BufferedReader((new InputStreamReader(socket.getInputStream())));
                System.out.println("Message from client: " + br.readLine());
                System.out.println("The server has ended");*/





                System.out.println("Server starting at port number: " + portnumber);
                serverSocket = new ServerSocket(portnumber);

                //client connecting
                System.out.println("Waiting for clients to connect");
                Socket socket = serverSocket.accept();
                System.out.println("A client has connected");

                InputStream is = socket.getInputStream();
                BufferedReader br = new BufferedReader((new InputStreamReader(is)));
                String iFilePath = "D:/Java_Projects/Android_Projects/" + br.readLine();

                File inputFile = new File(iFilePath);
                System.out.println(iFilePath);

                byte[] iFileSizeBytes = new byte[Long.BYTES];
                int iNumBytesRead = is.read(iFileSizeBytes, 0, Long.BYTES);
                if (iNumBytesRead != Long.BYTES) {
                    System.out.println("Wrong number of bytes has been received: " + iNumBytesRead);
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
                FileOutputStream fos = new FileOutputStream(inputFile);
                while (iBytesAlreadyRead != iFileSize) {
                    iNumBytesRead += is.read(iFileData, iBytesAlreadyRead, (int) iFileSize - iBytesAlreadyRead);
                    iBytesAlreadyRead = iNumBytesRead;
                }
                fos.write(iFileData);//writes from byteArray into fos

                //socket.shutdownInput();
                socket.shutdownOutput();

                //----------------------------------                  //SEND FILE TO CLIENT
                /*System.out.println("Server sending file...");

                File outputFile = new File("D:/Java_Projects/Android_Projects/fileFromServer.txt");
                byte[] oBytes = new byte[(int) outputFile.length()];
                String oFileName = outputFile.getName();
                long oFileSize = outputFile.length();
                OutputStream os = socket.getOutputStream();//tcp stream
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
                bw.write(oFileName);
                bw.newLine();
                bw.flush();

                ByteBuffer oBuffer = ByteBuffer.allocate(Long.BYTES);
                oBuffer.putLong(oFileSize);
                os.write(oBuffer.array());
                os.flush();

                FileInputStream fis = new FileInputStream(outputFile);//new stream to read FROM file
                int oNumBytesRead = fis.read(oBytes);//reads from file byte by byte to byreArray
                if (oNumBytesRead != oFileSize) {
                    System.out.println("Wrong number of bytes has been read");
                    System.exit(1);
                }
                os.write(oBytes);//writes bytes into stream
                os.flush();*/

                fos.close();
                is.close();
                br.close();

                /*fis.close();
                os.close();
                bw.close();*/

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

    }
}
