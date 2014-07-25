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

public class SyncroActivity extends ActionBarActivity {

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
				new DownloadSyncTask(SyncroActivity.this, pbDownload, pbTotal, imgDownload).execute();
				new SendSyncTask(SyncroActivity.this, pbSend, pbTotal, imgSend).execute();
			}
		});
	}
	
	private class SendSyncTask extends SendTask{
		
		ProgressBar me = null;
		ProgressBar total = null;
		ImageView img = null;
		
		public SendSyncTask(Context context, ProgressBar pb, ProgressBar total, ImageView img) {
			super(context);
			this.me = pb;
			this.total = total;
			this.img = img;
		}
		
		@Override
		protected void onPostExecute(Integer ret) {
			super.onPostExecute(ret);
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
		protected void onPreExecute() {
			super.onPreExecute();
			total.setIndeterminate(true);
			me.setVisibility(View.VISIBLE);
			img.setVisibility(View.GONE);
		}
	}
	
	private class DownloadSyncTask extends DownloadTask{
		
		ProgressBar me = null;
		ProgressBar total = null;
		ImageView img = null;
		
		public DownloadSyncTask(Context context, ProgressBar pb, ProgressBar total, ImageView img) {
			super(context);
			this.me = pb;
			this.total = total;
			this.img = img;
		}
		
		@Override
		protected void onPostExecute(String ret) {
			super.onPostExecute(ret);
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
		protected void onPreExecute() {
			super.onPreExecute();
			total.setIndeterminate(true);
			me.setVisibility(View.VISIBLE);
			img.setVisibility(View.GONE);
		}
	}
}
