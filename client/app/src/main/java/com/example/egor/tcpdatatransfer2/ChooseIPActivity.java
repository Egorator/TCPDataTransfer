package com.example.egor.tcpdatatransfer2;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import net.rdrei.android.dirchooser.DirectoryChooserActivity;

import java.io.File;
import java.net.Socket;

public class ChooseIPActivity extends AppCompatActivity {

    Model model = Model.getModelInstance();

    //private static final String hostname = "192.168.0.103"; //"192.168.0.103"; //"192.168.2.70";
    private final String debugString = "debug"; // TODO wtf?
    private int temp = 0; //for ViewFileNamesButton
    // TODO wtf, no other way? Useless conventionality:
    private static final int FILE_SELECT_CODE = 0; //for SendFileButton
    private String userInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_ip);
    }

    public void proceedButtonClick(View view) {
        EditText ipEditText = (EditText)(findViewById(R.id.ipEditText));
        userInput = String.valueOf(ipEditText.getText());
        if (model.checkUserInputIp(userInput)) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_CODE);
        }
        else {
            ipEditText.setText("Wrong input, please retry!");
        }
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
                    final String realPath = model.getRealPathFromURI(this, uri);
                    System.out.println("Real path: " + realPath);
                    model.sendFilePackets(realPath, userInput);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
