package it.rn2014.scanner;

import it.rn2014.scanner.RemoteResources.DownloadTask;
import it.rn2014.scanner.RemoteResources.SendTask;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * Activity che si occupa della sincronizzazione
 * 
 * @author Nicola
 */
public class SyncroActivity extends ActionBarActivity {

	/** Conteggio dei sottotask completati */
	private int count = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_syncro);
		
		final ProgressBar pbDownload = (ProgressBar)findViewById(R.id.progressBarDB);
		final ProgressBar pbSend = (ProgressBar)findViewById(R.id.progressBarSend);
		final ProgressBar pbTotal = (ProgressBar)findViewById(R.id.syncro_progress);
		final ImageView imgDownload = (ImageView)findViewById(R.id.progressImageDB);
		final ImageView imgSend = (ImageView)findViewById(R.id.progressImageSend);
		
		
		Button btnSync = (Button)findViewById(R.id.btnStartSyncro);
		btnSync.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				count = 0;
				
				// Faccio partire due task asincroni
				new DownloadSyncTask(SyncroActivity.this, pbDownload, pbTotal, imgDownload).execute();
				new SendSyncTask(SyncroActivity.this, pbSend, pbTotal, imgSend).execute();
			}
		});
	}
	
	/**
	 * Task per l'invio delle statistiche al server remoto
	 * 
	 * @author Nicola Corti
	 */
	private class SendSyncTask extends SendTask{
		
		/** Riferimento alla propria progress bar */
		ProgressBar me = null;
		/** Riferimento alla progress bar global */
		ProgressBar total = null;
		/** Riferimetno all'immagine */
		ImageView img = null;
		
		public SendSyncTask(Context context, ProgressBar pb, ProgressBar total, ImageView img) {
			super(context);
			this.me = pb;
			this.total = total;
			this.img = img;
		}
		
		@Override
		protected void onPostExecute(Integer ret) {
			
			// In caso di errore visualizzo immagine rossa
			try{
				super.onPostExecute(ret);
			} catch (Exception e) {
				img.setImageDrawable(getResources().getDrawable(R.drawable.cancel));
			}
			me.setVisibility(View.GONE);
			
			// TODO Gestire i risultati
			if (ret != 200)
				img.setImageDrawable(getResources().getDrawable(R.drawable.cancel));
			img.setVisibility(View.VISIBLE);
			count++;
			if (count == 2){
				total.setIndeterminate(false);
				Toast.makeText(getApplicationContext(), "Sincronizzazione completata!", Toast.LENGTH_LONG).show();
			}
		}
		
		@Override
		protected Integer doInBackground(Void... params){
			try {
				return super.doInBackground();
			} catch (Exception e) {
				img.setImageDrawable(getResources().getDrawable(R.drawable.cancel));
				return 500;
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			// Faccio partire le progressbar
			total.setIndeterminate(true);
			me.setVisibility(View.VISIBLE);
			img.setVisibility(View.GONE);
		}
	}
	
	/**
	 * Task asincrono per il download del db nella schermata di sincronia
	 * 
	 * @author Nicola Corti
	 */
	private class DownloadSyncTask extends DownloadTask{
		
		/** Riferimento alla propria progress bar */
		ProgressBar me = null;
		/** Riferimento alla progress bar global */
		ProgressBar total = null;
		/** Riferimetno all'immagine */
		ImageView img = null;
		
		public DownloadSyncTask(Context context, ProgressBar pb, ProgressBar total, ImageView img) {
			super(context);
			this.me = pb;
			this.total = total;
			this.img = img;
		}
		
		@Override
		protected void onPostExecute(String ret) {
			// In caso di errore visualizzo immagine rossa
			try{
				super.onPostExecute(ret);
			} catch (Exception e) {
				img.setImageDrawable(getResources().getDrawable(R.drawable.cancel));
			}
			me.setVisibility(View.GONE);
			
			// Gestire i risultati
			img.setVisibility(View.VISIBLE);
			count++;
			if (count == 2){
				total.setIndeterminate(false);
				Toast.makeText(getApplicationContext(), "Sincronizzazione completata!", Toast.LENGTH_LONG).show();
			}
		}

		@Override
		protected String doInBackground(String... sUrl) {
			try {
				return super.doInBackground(sUrl);
			} catch (Exception e) {
				img.setImageDrawable(getResources().getDrawable(R.drawable.cancel));
				return null;
			}
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			total.setIndeterminate(true);
			me.setVisibility(View.VISIBLE);
			img.setVisibility(View.GONE);
		}
	}
}
