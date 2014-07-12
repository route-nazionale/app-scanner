package it.rn2014.scanner;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class LoginActivity extends ActionBarActivity {

	EditText code;
	EditText date;
	ProgressBar prb;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		code = (EditText)findViewById(R.id.code);
		
		Button btn = (Button)findViewById(R.id.btnSignin);
		final ProgressBar prb = (ProgressBar)findViewById(R.id.login_progress);
		
		
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// TODO Si deve implementare il login
				// LoginTask login = new LoginTask(prb);
				// login.execute(new String[]{code.getText().toString(), date.getText().toString()});
				prb.setVisibility(View.VISIBLE);
			}
		});
		
		Bundle extras = getIntent().getExtras();
		if (extras != null && extras.containsKey("qrscanned")) {
		    String scannedCode = extras.getString("qrscanned");
		    code.setText(scannedCode);
		    code.setEnabled(false);
		}
	}
	
	@SuppressWarnings("unused")
	private class LoginTask extends AsyncTask<String, Void, String>{

		ProgressBar prb;
		
		private LoginTask(ProgressBar p){
			this.prb = p;
		}
		
		@Override
		protected void onPreExecute() {
			prb.setVisibility(View.VISIBLE);
		}
		
		@Override
		protected void onPostExecute(String result) {
		    if(result.equals("1")){
		    	Log.e("ME", "CORRETTO");
		    }
		    else{
		    	Log.e("ME", "ERRATO"); 
		    }
		    prb.setVisibility(View.GONE);
		}
		
		@Override
		protected String doInBackground(String... params) {
			
			Looper.prepare();
			ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
			postParams.add(new BasicNameValuePair("username", params[0]));
			postParams.add(new BasicNameValuePair("password", params[1]));
			String res = null;
			String response = null;
			Log.e("me", "SONO QUA");
			try{
				response = CustomHttpClient.executeHttpPost("http://ncorti.it/files/rn.php", postParams);
				res = response.toString();
				Log.e("me", res);
				res = res.replaceAll("\\s+","");
				Toast.makeText(getApplicationContext(), res, Toast.LENGTH_SHORT).show();
				
			} catch (Exception e) {
				Log.e("me", e.toString());
			}
			Looper.loop();
			return res;
		}
	}
}