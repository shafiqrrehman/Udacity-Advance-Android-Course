package com.shafiq.favoritetoys;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.shafiq.favoritetoys.data.ToyBox;

public class MainActivity extends AppCompatActivity {

    private TextView mToyListTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToyListTextView = findViewById(R.id.tv_toy_names);

        String[] toyNames = ToyBox.getToyNames();

        for (String toyName : toyNames) {
            mToyListTextView.append(toyName  + "\n\n\n");
        }
    }
}
