package it.rn2014.scanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

/**
 * Classe che visualizza lo splash screen iniziale dell'app.
 * L'activity si occupa di
 * - Mostrare i loghi
 * - Controllare se l'app barcode scanner e' presente
 * - Ricaricare i dati utente
 * 
 * @author Nicola Corti
 *
 */
public class SplashScreenActivity extends Activity {

	/** Tempo di attesa in secondo dello splash screen */
	private static int SPLASH_TIMER = 3000;
	/** Codice di risposta activity play store */
	private static int MARKET_CODE = 1;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		// Mostro il numero versione
		TextView vers = (TextView)findViewById(R.id.versionName);
		PackageInfo pInfo;
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			vers.setText(pInfo.versionName);
		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
		}
		
		
		// Creo alert per Barcode Scanner - Eseguo in caso di eccezione
		final AlertDialog.Builder adb = new AlertDialog.Builder(SplashScreenActivity.this);
	    adb.setTitle("Scaricare Barcode Scanner");
	    adb.setIcon(R.drawable.qr_code_icon);
	    adb.setMessage("Per effettuare le scansioni dei QR code e' necessario scaricare dal Play Store l'app Barcode Scanner");
	    
	    adb.setPositiveButton("Scarica ora", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
		    	Intent intent = new Intent(Intent.ACTION_VIEW);
		    	
		    	// Apro il market alla pagina dell'app
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
			getPackageManager().getApplicationInfo("com.google.zxing.client.android", 0);

			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					
					// Recupero i dati utente dopo SPLASH_TIMER secondi
					RestoreUser ru = new RestoreUser();
					ru.execute();
				}
			}, SPLASH_TIMER);
			
		} catch (PackageManager.NameNotFoundException e) {				    
		    adb.show();
		} 
	}

	/**
	 * Task asincrono per la deserializzazione dei dati di login
	 * 
	 * @author Nicola Corti
	 */
	private class RestoreUser extends AsyncTask<Void, Void, Void> {

		private boolean logged = false;

		@Override
		protected Void doInBackground(Void... params) {

			if (UserData.restoreInstance(getApplicationContext()) == true) {			
				
				// Controllo se ho i dati di login
				if (UserData.getInstance().getCU() != null
						&& UserData.getInstance().getDate() != null) {
					logged = true;
				}
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {

			// Effettuo il login o meno in base ai dati raccolti.
			if (logged) {
				Intent main = new Intent(getApplicationContext(),
						MainActivity.class);
				startActivity(main);
			} else {
				Intent i = new Intent(SplashScreenActivity.this,
						AuthActivity.class);
				startActivity(i);
			}
			finish();
		}
	}
	
    /*
     * Eseguo al ritorno dell'activity market.
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MARKET_CODE) {
			RestoreUser ru = new RestoreUser();
			ru.execute();
        }
    }
}
