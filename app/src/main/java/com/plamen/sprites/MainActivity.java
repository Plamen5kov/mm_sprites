package com.plamen.sprites;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

    private SpriteView stv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stv = new SpriteView(this);
        setContentView(stv);
        stv.requestFocus();
    }
}