package it.rn2014.scanner;

import it.rn2014.downloader.DownloadActivity;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends ActionBarActivity {

	EditText code;
	DatePicker date;
	ProgressBar prb;
	TextView error;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		code = (EditText)findViewById(R.id.code);
		date = (DatePicker)findViewById(R.id.date);
		
		Button btn = (Button)findViewById(R.id.btnSignin);
		final ProgressBar prb = (ProgressBar)findViewById(R.id.login_progress);
		
		error = (TextView)findViewById(R.id.errorMessage);
		
		
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				LoginTask login = new LoginTask(prb, error);
				String dateValue = date.getDayOfMonth() + "/" + date.getMonth() + "/" + date.getYear();
				login.execute(new String[]{code.getText().toString(), dateValue});
			}
		});
		
		Bundle extras = getIntent().getExtras();
		if (extras != null && extras.containsKey("qrscanned")) {
		    String scannedCode = extras.getString("qrscanned");
		    code.setText(scannedCode);
		    code.setClickable(false);
		    code.setFocusable(false);
		    code.setFocusableInTouchMode(false);
		    code.setEnabled(false);
		}
	}
	
	public void testDB(View view){
		Intent intent = new Intent(this,DownloadActivity.class);
    	startActivity(intent);
	}
	

	private class LoginTask extends AsyncTask<String, Void, String>{

		ProgressBar prb;
		TextView error;
		
		private LoginTask(ProgressBar p, TextView err){
			this.prb = p;
			this.error = err;
		}
		
		@Override
		protected void onPreExecute() {
			error.setVisibility(View.GONE);
			prb.setVisibility(View.VISIBLE);
		}
		
		@Override
		protected void onPostExecute(String result) {
		    if(result.equals("1")){
		    	Toast.makeText(getApplicationContext(), "Autenticazione Riuscita", Toast.LENGTH_SHORT).show();
		    	Intent main = new Intent(getApplicationContext(), MainActivity.class);
				startActivity(main);
				UserData.getInstance().setLevel("OK");
				UserData.saveInstance(getApplicationContext());
		    }
		    else{
		    	error.setVisibility(View.VISIBLE);
		    	UserData.getInstance().logOut();
		    }
		    prb.setVisibility(View.GONE);
		}
		
		@Override
		protected String doInBackground(String... params) {
			
			ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
			postParams.add(new BasicNameValuePair("code", params[0]));
			postParams.add(new BasicNameValuePair("date", params[1]));
			
			UserData.getInstance().setCU(params[0]);
			UserData.getInstance().setDate(params[1]);
			
			String res = null;
			String response = null;
			try{
				response = CustomHttpClient.executeHttpPost("http://ncorti.it/files/rn.php", postParams);
				res = response.toString();
				res = res.replaceAll("\\s+","");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return res;
		}
	}
}