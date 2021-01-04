package com.example.phonebookimagetotab;

import android.app.Activity;
import android.app.AppComponentFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.P)
public class MineActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine);

        Button button = (Button) findViewById(R.id.resetbutton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.example.phonebookimagetotab.GameEngine.getInstance().createGrid(getApplicationContext());
            }
        });

//        ((TextView) findViewById(R.id.textView)).setText("dsf");

        com.example.phonebookimagetotab.GameEngine.getInstance().createGrid(this);
    }
}