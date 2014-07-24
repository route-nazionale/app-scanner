package it.rn2014.scanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreenActivity extends Activity {

	private static int SPLASH_TIMER = 3000;
	private static int MARKET_CODE = 3000;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		final AlertDialog.Builder adb = new AlertDialog.Builder(SplashScreenActivity.this);
	    adb.setTitle("Scaricare Barcode Scanner");
	    adb.setMessage("Per effettuare le scansioni dei QR code e' necessario scaricare dal Play Store l'app Barcode Scanner");
	    
	    adb.setPositiveButton("Scarica ora", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
		    	Intent intent = new Intent(Intent.ACTION_VIEW);
		    	intent.setData(Uri.parse("market://details?id=com.google.zxing.client.android"));
		    	startActivityForResult(intent, MARKET_CODE);
			}
	      });
	    
	    adb.setNegativeButton("Continua senza", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				RestoreUser ru = new RestoreUser();
				ru.execute();
			}
	      });

		try {
			@SuppressWarnings("unused")
			ApplicationInfo info = getPackageManager().getApplicationInfo("com.google.zxing.client.android", 0);

			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					RestoreUser ru = new RestoreUser();
					ru.execute();
				}
			}, SPLASH_TIMER);
			
		} catch (PackageManager.NameNotFoundException e) {				    
		    adb.show();
		} 
	}

	private class RestoreUser extends AsyncTask<Void, Void, Void> {

		private boolean logged = false;

		@Override
		protected void onPostExecute(Void result) {

			if (logged) {
				Intent main = new Intent(getApplicationContext(),
						MainActivity.class);
				startActivity(main);
				finish();
			} else {
				Intent i = new Intent(SplashScreenActivity.this,
						AuthActivity.class);
				startActivity(i);
				finish();
			}
		}

		@Override
		protected Void doInBackground(Void... params) {

			if (UserData.restoreInstance(getApplicationContext()) == true) {
				if (UserData.getInstance().getCU() != null
						&& UserData.getInstance().getDate() != null) {
					logged = true;
				}
			}
			return null;
		}
	}
	
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MARKET_CODE) {
			RestoreUser ru = new RestoreUser();
			ru.execute();
        }
    }
}
