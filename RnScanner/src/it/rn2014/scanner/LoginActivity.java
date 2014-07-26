package it.rn2014.scanner;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Classe che si occupa di effettuare il login dell'applicazione
 * 
 * @author Nicola Corti
 */
public class LoginActivity extends ActionBarActivity {

	/** Riferimento alla casella di testo del codice */
	private EditText code;
	/** Riferimento al datepicker della data */
	private DatePicker date;
	/** Riferimento al messsaggio di errore */
	private TextView error;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		code = (EditText)findViewById(R.id.code);
		date = (DatePicker)findViewById(R.id.date);
		Button btn = (Button)findViewById(R.id.btnSignin);
		final ProgressBar prb = (ProgressBar)findViewById(R.id.login_progress);
		error = (TextView)findViewById(R.id.errorMessage);
		
		/*
		 * TextWatcher per l'inserimento dei trattini automatici nel codice
		 */
		code.addTextChangedListener(new TextWatcher() {
			
			boolean delete = false;
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) { }
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				if (count < after) delete = false; else	delete = true;
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				int textlength1 = s.length();
				if (delete) return;
				if(textlength1==2 || textlength1==7 || textlength1==14)
					s.append("-");
			}
		});
		
		/*
		 * Dialog per connessione assente
		 */
		final AlertDialog.Builder adb = new AlertDialog.Builder(this);
	    adb.setTitle("Connessione Assente");
	    adb.setIcon(R.drawable.no_network);
	    adb.setMessage("Per effettuare il login e' necessario essere connessi ad Internet");
	    adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	      } });
		
	    /*
	     * Listner del click sul bottone login
	     */
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (code.length() <= 1) return;
				
				if (!RemoteResources.haveNetworkConnection(LoginActivity.this)){
					adb.show();
				} else {
					// Creo un nuovo async task
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
					
					String dateValue = year + "-" + month + "-" + day;
					
					// Eseguo con i parametri di accesso l'async task
					login.execute(new String[]{code.getText().toString(), dateValue});
				}
			}
		});
		
		// Se e' presente il parametro `qrscanned` (arrivo da un intent)
		// allora blocco la casella di testo
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
	

	/**
	 * Task asincrono che si occupa di effettuare il login
	 * 
	 * @author Nicola Corti
	 */
	private class LoginTask extends AsyncTask<String, Void, String>{

		/** URL dell'autenticazione */
		private static final String URL_AUTH = "http://mobile.rn2014.it/index.php/auth.php";
		
		/** Riferimento alla progressbar */
		private ProgressBar prb;
		/** Riferimento al messaggio di errore */
		private TextView error;
		
		
		/**
		 * Costruttore di base
		 * 
		 * @param p Riferimento alla progressbar
		 * @param err Riferimento alla TextView dell'errore
		 */
		private LoginTask(ProgressBar p, TextView err){
			this.prb = p;
			this.error = err;
		}
		
		@Override
		protected void onPreExecute() {
			// Mostro la barra e nascondo messaggio errore
			error.setVisibility(View.GONE);
			prb.setVisibility(View.VISIBLE);
		}
		
		@Override
		protected String doInBackground(String... params) {
			
			ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
			
			try {

				// Mando in post i parametri di autenticazione
				postParams.add(new BasicNameValuePair("reprint", params[0].substring(params[0].length()-1)));
				postParams.add(new BasicNameValuePair("cu", params[0]
						.substring(0, params[0].length() - 2)));
				postParams.add(new BasicNameValuePair("date", params[1]));
			} catch (IndexOutOfBoundsException e) {
				
				// Se mi inseriscono un cu troppo corto
				return "noauth";
			}
			
			// Mi salvo nell'Userdata i dati di accesso
			UserData.getInstance().setCU(params[0]);
			UserData.getInstance().setDate(params[1]);
			
			String res = "noauth";
			String response = null;
			try{
				response = CustomHttpClient.executeHttpPostString(URL_AUTH, postParams);
				res = response.toString();
				res = res.replaceAll("\\s+","");
				
				return res;
			} catch (Exception e) {
				e.printStackTrace();
				return "noauth";
			}
		}
		
		@Override
		protected void onPostExecute(String result) {
		    
			// In base alla risposta calcolo il livello di autenticazione
			// inoltre serializzo i dati utente
			
			Log.e("CHECK", result);
			
			if(result != null && result.contentEquals("security")){
				
				// Security
				UserData.getInstance().setLevel("security");
				UserData.saveInstance(getApplicationContext());
		    	Toast.makeText(getApplicationContext(), "Autenticazione Riuscita", Toast.LENGTH_SHORT).show();
		    	Intent main = new Intent(getApplicationContext(), MainActivity.class);

				startActivity(main);
				finish();
				
		    } else if (result != null && result.contentEquals("event")) {
		    	
		    	// Capospalla
				UserData.getInstance().setLevel("event");
				UserData.saveInstance(getApplicationContext());
		    	
		    	Toast.makeText(getApplicationContext(), "Autenticazione Riuscita", Toast.LENGTH_SHORT).show();
		    	Intent main = new Intent(getApplicationContext(), MainActivity.class);

				startActivity(main);
				finish();
		    }
		    else{
		    	// Non loggato
		    	error.setVisibility(View.VISIBLE);
		    	UserData.getInstance().logOut();
		    }
		    prb.setVisibility(View.GONE);
		}
	}
}