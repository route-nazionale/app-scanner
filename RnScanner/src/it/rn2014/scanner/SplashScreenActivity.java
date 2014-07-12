package it.rn2014.scanner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
 
public class SplashScreenActivity extends Activity {
 
    private static int SPLASH_TIMER = 4000;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
 
        // Handler che avvia la nuova activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashScreenActivity.this, AuthActivity.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_TIMER);
    }
}
