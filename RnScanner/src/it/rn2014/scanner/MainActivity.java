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

public class MainActivity extends ActionBarActivity implements OnClickListener{

	// declare the dialog as a member field of your activity
	ProgressDialog mProgressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (UserData.getInstance().getLevel().contentEquals("security")){
			setContentView(R.layout.activity_main);
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

		// instantiate it within the onCreate method
		mProgressDialog = new ProgressDialog(MainActivity.this);
		mProgressDialog.setMessage("Download dei dati Route Nazionale");
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		
		if (!RemoteResources.haveNetworkConnection(MainActivity.this) && 
				!DataManager.getInstance(this).checkDataBase()){
			
		    AlertDialog.Builder adb = new AlertDialog.Builder(this);
		    adb.setTitle("Connessione Assente");
		    adb.setMessage("Non sono presenti i dati della Route Nazionale sul device! Devi essere connesso ad Internet per scaricarli");
		    adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) {
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
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnGate:
			Intent gate = new Intent(getApplicationContext(), ScanningActivity.class);
			gate.putExtra("mode", "gate");
			UserData.getInstance().setChoose("gate");
			startActivity(gate);
			break;
		case R.id.btnLogout:
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
	
	public class DownloadMainTask extends DownloadTask {

		public DownloadMainTask(Context context) {
			super(context);
		}
		
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        
	        if(super.alreadyExists == false){
		        mProgressDialog.show();
	        }
	    }

		@Override
		protected void onProgressUpdate(Integer... progress) {
			super.onProgressUpdate(progress);
			mProgressDialog.setIndeterminate(false);
			mProgressDialog.setMax(100);
			mProgressDialog.setProgress(progress[0]);
		}

		@Override
		protected void onPostExecute(String result) {
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
