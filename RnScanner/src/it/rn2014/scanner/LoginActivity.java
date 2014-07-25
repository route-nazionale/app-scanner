package it.rn2014.scanner;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
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
		
		code.addTextChangedListener(new TextWatcher() {
			
			boolean delete = false;
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) { }
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				if (count < after) 
					delete = false;
				else
					delete = true;
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				int textlength1 = s.length();
				if (delete) return;
				if(textlength1==2 || textlength1==7 || textlength1==14)
					s.append("-");
			}
		});
		
		
		final AlertDialog.Builder adb = new AlertDialog.Builder(this);
	    adb.setTitle("Connessione Assente");
	    adb.setMessage("Per effettuare il login e' necessario essere connessi ad Internet");
	    adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	      } });
		
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (code.length() <= 1) return;
				
				if (!haveNetworkConnection()){
					adb.show();
				} else {
					LoginTask login = new LoginTask(prb, error);
										 
					String day, month, year;
					if (date.getDayOfMonth() < 10)
						day = "0" + date.getDayOfMonth();
					else
						day = "" + date.getDayOfMonth();
					
					if ((date.getMonth()+1) < 10)
						month = "0" + (date.getMonth() + 1);
					else
						month = "" + (date.getMonth() + 1);
					year = "" + date.getYear();
					
					String dateValue = day + "/" + month + "/" + year;
					login.execute(new String[]{code.getText().toString(), dateValue});
				}
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
				 
				UserData.getInstance().setLevel("OK");
				UserData.saveInstance(getApplicationContext());
				
				startActivity(main);
				finish();
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
				response = CustomHttpClient.executeHttpPostString("http://ncorti.it/files/rn.php", postParams);
				res = response.toString();
				res = res.replaceAll("\\s+","");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return res;
		}
	}
	
    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}