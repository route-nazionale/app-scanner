package it.rn2014.scanner;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
 
public class SplashScreenActivity extends Activity {
 
    private static int SPLASH_TIMER = 3000;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
         
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                RestoreUser ru = new RestoreUser();
                ru.execute();
            }
        }, SPLASH_TIMER);
    }
    
    private class RestoreUser extends AsyncTask<Void, Void, Void> {

    	private boolean logged = false;
    	
    	@Override
		protected void onPostExecute(Void result) {
			
    		if (logged){
    			Intent main = new Intent(getApplicationContext(), MainActivity.class);
    			startActivity(main);
    			finish();
    		} else {
    			Intent i = new Intent(SplashScreenActivity.this, AuthActivity.class);
                startActivity(i);
                finish();
    		}
		}
    	
		@Override
		protected Void doInBackground(Void... params) {
			
			if (UserData.restoreInstance(getApplicationContext()) == true){
				if (UserData.getInstance().getCU() != null &&
					UserData.getInstance().getDate() != null){
					logged = true;
				}
			}
			return null;
		}
    }
}
