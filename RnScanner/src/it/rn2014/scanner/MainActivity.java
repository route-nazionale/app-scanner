package it.rn2014.scanner;

import it.rn2014.db.DataManager;
import it.rn2014.scanner.RemoteResources.DownloadTask;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Classe che gestisce la schermata principale dell'applicazione.
 * La schermata viene disengata in modo differente in base al livello di
 * autenticazione dell'utente (security o event).
 * 
 * @author Nicola Corti
 */
public class MainActivity extends ActionBarActivity implements OnClickListener{


	/** Riferimento al progress dialog per il download del file */
	ProgressDialog mProgressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Disegno l'interfaccia in base al livello
		if (UserData.getInstance().getLevel().contentEquals("security")){
			setContentView(R.layout.activity_main);
			
			// bottoni non presenti per `event`
			Button btnGate = (Button)findViewById(R.id.btnGate);
			btnGate.setOnClickListener(this);
			Button btnIdentify = (Button)findViewById(R.id.btnIdentify);
			btnIdentify.setOnClickListener(this);
		}
		else
			setContentView(R.layout.activity_mainevt);
			
		TextView cu = (TextView)findViewById(R.id.cu);
		cu.setText(UserData.getInstance().getCU());
		
		Button btnLogout = (Button)findViewById(R.id.btnLogout);
		btnLogout.setOnClickListener(this);
		Button btnEvents = (Button)findViewById(R.id.btnEvents);
		btnEvents.setOnClickListener(this);

		Button btnSync = (Button)findViewById(R.id.btnSyncro);
		btnSync.setOnClickListener(this);


		// ProgressDialog per lo scaricamento del database
		mProgressDialog = new ProgressDialog(MainActivity.this);
		mProgressDialog.setMessage("Download dei dati Route Nazionale");
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		
		
		// Controllo se non posso scaricare i dati Route Nazionale
		// Internet assente e Db assente
		if (!RemoteResources.haveNetworkConnection(MainActivity.this) && 
				!DataManager.getInstance(this).checkDataBase()){
			
		    AlertDialog.Builder adb = new AlertDialog.Builder(this);
		    adb.setTitle("Connessione Assente");
		    adb.setIcon(R.drawable.no_network);
		    adb.setMessage("Non sono presenti i dati della Route Nazionale sul device! Devi essere connesso ad Internet per scaricarli");
		    adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) {
		        	
		        	// Faccio tornare alla home
		        	Intent intent = new Intent(Intent.ACTION_MAIN);
					intent.addCategory(Intent.CATEGORY_HOME);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
		      } });
		    adb.show();
		} else {
			DownloadTask dt = new DownloadMainTask(MainActivity.this);
			dt.execute();
		}
		
		// Creo alert per Notificare eventi da sincronizzare
		final AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
	    adb.setTitle("Eventi da sincronizzare");
	    adb.setIcon(R.drawable.syncro);
	    adb.setMessage("Ci sono eventi da sincronizzare non ancora inviati, farlo adesso?");
	    
	    adb.setPositiveButton("Sincronizza ora", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent sync = new Intent(getApplicationContext(), SyncroActivity.class);
				startActivity(sync);
			}
	      });
	    
	    adb.setNegativeButton("Non ora", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) { }
	      });
	    if (RemoteResources.haveNetworkConnection(MainActivity.this) &&
	    		UserData.getInstance().getToSync(MainActivity.this) > 0){
	    	adb.show();
	    }
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		// Collego i bottoni alle varie modalita' di esecuzione
		case R.id.btnGate:
			Intent gate = new Intent(getApplicationContext(), ScanningActivity.class);
			gate.putExtra("mode", "gate");
			UserData.getInstance().setChoose("gate");
			startActivity(gate);
			break;
		case R.id.btnLogout:
			// Faccio il logout e torno alla schermata di auth
			UserData.getInstance().logOut();
			UserData.saveInstance(getApplicationContext());
			Intent login = new Intent(getApplicationContext(), AuthActivity.class);
			startActivity(login);
			finish();
			break;
		case R.id.btnEvents:
			Intent event = new Intent(getApplicationContext(), EventActivity.class);
			startActivity(event);
			break;
		case R.id.btnIdentify:
			Intent ident = new Intent(getApplicationContext(), ScanningActivity.class);
			ident.putExtra("mode", "identify");
			UserData.getInstance().setChoose("identify");
			startActivity(ident);
			break;
		case R.id.btnSyncro:
			Intent sync = new Intent(getApplicationContext(), SyncroActivity.class);
			startActivity(sync);
			break;
		default:
			break;
		}
	}
	
	/**
	 * Task per il download del DB
	 * Si comporta come DownloadTask, ma aggiorna anche l'interfaccia
	 * 
	 * @author nicola
	 */
	public class DownloadMainTask extends DownloadTask {

		public DownloadMainTask(Context context) {
			super(context);
		}
		
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	    	if (!super.alreadyExists)
	    		mProgressDialog.show();
	    }

		@Override
		protected void onProgressUpdate(Integer... progress) {
			super.onProgressUpdate(progress);
			
			// Aggiorno la barra di completamento
			mProgressDialog.setIndeterminate(false);
			mProgressDialog.setMax(100);
			mProgressDialog.setProgress(progress[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			
			// Mostro il messaggio in base al risultato del task
			mProgressDialog.dismiss();
			if (result != null)
				Toast.makeText(MainActivity.this,
						"Errore nel download: " + result, Toast.LENGTH_LONG)
						.show();
			else
				if (!super.alreadyExists){
				Toast.makeText(MainActivity.this,
						"Dati scaricati correttamente", Toast.LENGTH_LONG)
						.show();
				}
		}
	}
}
