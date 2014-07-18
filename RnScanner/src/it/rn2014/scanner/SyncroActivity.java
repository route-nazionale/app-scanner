package it.rn2014.scanner;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class SyncroActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_syncro);
		
		Button btnSync = (Button)findViewById(R.id.btnStartSyncro);
		btnSync.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {				
				SyncroTask st = new SyncroTask();
				st.execute(new String[]{"pippo", "pluto"});
			}
		});
	}
	
	
	

	private class SyncroTask extends AsyncTask<String, Integer, String>{

		ProgressBar total, p1, p2, p3;
		ImageView i1, i2, i3;
			
		private SyncroTask(){			
			total = (ProgressBar)findViewById(R.id.syncro_progress);
			p1 = (ProgressBar)findViewById(R.id.progressBarDB);
			p2 = (ProgressBar)findViewById(R.id.progressBarSend);
			p3 = (ProgressBar)findViewById(R.id.progressBarReceive);
			
			this.i1 = (ImageView)findViewById(R.id.progressImageDB);
			this.i2 = (ImageView)findViewById(R.id.progressImageSend);
			this.i3 = (ImageView)findViewById(R.id.progressImageReceive);
		}
		
		@Override
		protected void onPreExecute() {
			total.setIndeterminate(true);
			p1.setVisibility(View.VISIBLE);
			p2.setVisibility(View.VISIBLE);
			p3.setVisibility(View.VISIBLE);
			i1.setVisibility(View.GONE);
			i2.setVisibility(View.GONE);
			i3.setVisibility(View.GONE);
		}
		
		@Override
		protected void onPostExecute(String result) {
			total.setIndeterminate(false);
			Toast.makeText(getApplicationContext(), "Sincronizzazione completata!", Toast.LENGTH_LONG).show();
		}
		
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			if (values[0] == 1){
				p1.setVisibility(View.GONE);
				i1.setVisibility(View.VISIBLE);
			} else if (values[0] == 2) {
				p2.setVisibility(View.GONE);
				i2.setVisibility(View.VISIBLE);
			} else if (values[0] == 3) {
				p3.setVisibility(View.GONE);
				i3.setVisibility(View.VISIBLE);
			}
		}

		@Override
		protected String doInBackground(String... params) {
			
			ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
			postParams.add(new BasicNameValuePair("code", params[0]));
			postParams.add(new BasicNameValuePair("date", params[1]));
			
			String res = null;
			String response = null;
			try{
				response = CustomHttpClient.executeHttpPost("http://ncorti.it/files/rn.php", postParams);
				res = response.toString();
				res = res.replaceAll("\\s+","");
				publishProgress(1);
				
				
				response = CustomHttpClient.executeHttpPost("http://ncorti.it/files/rn.php", postParams);
				res = response.toString();
				res = res.replaceAll("\\s+","");
				publishProgress(2);
				
				
				response = CustomHttpClient.executeHttpPost("http://ncorti.it/files/rn.php", postParams);
				res = response.toString();
				res = res.replaceAll("\\s+","");
				publishProgress(3);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return res;
		}
	}

	
}