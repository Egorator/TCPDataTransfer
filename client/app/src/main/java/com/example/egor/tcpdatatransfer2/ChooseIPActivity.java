package com.example.egor.tcpdatatransfer2;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class ChooseIPActivity extends AppCompatActivity {

    Model model = Model.getModelInstance();
    EditText ipEditText;
    RadioGroup radioGroup;

    //private static final String hostname = "192.168.0.103"; //"192.168.2.70";
    private final String debugString = "debug"; // TODO wtf?
    private int temp = 0; //for ViewFileNamesButton
    // TODO wtf, no other way? Useless conventionality:
    private static final int FILE_SELECT_CODE = 0; // for SendFileButton
    private String userInput;
    private boolean usePreviousIPButtonClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_ip);

        ipEditText = (EditText)(findViewById(R.id.ipEditText));
        // TODO strange realization, rework:
        radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedButton = (RadioButton) group.getChildAt(checkedId - 1);
                ipEditText.setText(checkedButton.getText());
            }
        });
    }

    // TODO !!! to work properly disable windows firewall on private network !!!
    public void proceedButtonClick(View view) {

        userInput = String.valueOf(ipEditText.getText());
        if (model.checkUserInputIp(userInput)) {
            if (model.uniqueIP(userInput))
                model.writeIPToFile(userInput); // NOTE!!! this string needs to be before others!
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_CODE);
        }
        else
            ipEditText.setText(R.string.Wrong_input_please_retry);
    }

    public void usePreviousIPButtonClick(View view) {
        if (!usePreviousIPButtonClicked) {
            usePreviousIPButtonClicked = true;
            // TODO code repetition!:
            for (String curRadioButtonText : model.getFileContents(model.getOutputFile())) {
                RadioButton curButton = new RadioButton(this);
                curButton.setText(curRadioButtonText);
                radioGroup.addView(curButton);
            }
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
