package it.rn2014.scanner;

import it.rn2014.db.DataManager;
import it.rn2014.db.entity.Evento;
import it.rn2014.db.entity.Persona;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;

/**
 * Interfaccia che mostra i risultati di una identificazione
 * 
 * @author Nicola Corti
 */
public class IdentifyResultActivity extends ActionBarActivity {

	/** Riferimento al nome */
	private Map<String, String> datumNome;
	/** Riferimento al cognome */
	private Map<String, String> datumCognome;
	/** Riferimento alla data di nascita */
	private Map<String, String> datumDataNascita;
	
	/** Adapter per i dati */
	SimpleAdapter adapter = null;
	/** Mappa dei dati */
	ArrayList<Map<String, String>> data = null;
	/** Listview dei risultati */
	ListView lw = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_identify_result);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null && extras.containsKey("qrscanned")) {
		    String code = extras.getString("qrscanned");
		    if (code == null) finish();
			
			String cu = code.substring(0, code.length()-2);
			String reprint = code.substring(code.length()-1);
			
			// Raccolgo dal DB i dati che ho presenti in locale
			Persona result = DataManager.getInstance(this).findPersonaByCodiceUnivoco(cu, reprint);
			ArrayList<Evento> ae = DataManager.getInstance(this).findEventiByPersona(result);
			String gruppo = DataManager.getInstance(this).findGruppoByPersona(result);
			
			lw = (ListView)findViewById(R.id.listIdentify);
			
			// Imposto le coppie con titolo/sottotitolo
			data = new ArrayList<Map<String, String>>();
			Map<String, String> datum;
			
			datumNome = new HashMap<String, String>(2);
			datumNome.put("title", result.getNome());
			datumNome.put("subtitle", "Nome");
			data.add(datumNome);
			
			datumCognome = new HashMap<String, String>(2);
			datumCognome.put("title", result.getCognome());
			datumCognome.put("subtitle", "Cognome");
			data.add(datumCognome);
			
			datumDataNascita = new HashMap<String, String>(2);
			datumDataNascita.put("title", "****-**-**");
			datumDataNascita.put("subtitle", "Data di nascita");
			data.add(datumDataNascita);
			
			datum = new HashMap<String, String>(2);
			datum.put("title", result.getCodiceUnivoco());
			datum.put("subtitle", "Codice Univoco");
			data.add(datum);
			
			datum = new HashMap<String, String>(2);
			datum.put("title", result.getCodiceAgesci());
			datum.put("subtitle", "Codice A.G.E.S.C.I.");
			data.add(datum);
			
			datum = new HashMap<String, String>(2);
			datum.put("title", gruppo + " - " + result.getIdGruppo() + " - " + result.getIdUnita());
			datum.put("subtitle", "Gruppo - Unita'");
			data.add(datum);
			
			datum = new HashMap<String, String>(2);
			datum.put("title", result.getContrada() + " - " + result.getQuartiere());
			datum.put("subtitle", "Contrada - Quartiere");
			data.add(datum);
			
			// Elenco degli eventi associati
			for (int i = 0; i < ae.size(); i++){
				Evento e = ae.get(i);
				datum = new HashMap<String, String>(2);
				datum.put("title", e.getCodiceStampa() + " - " + e.getNome());
				datum.put("subtitle", "Turno: " + (i+1) + " Sottocampo evento: " + e.getQuartiere());
				data.add(datum);
			}			
			
			// Imposto l'adapter
			adapter = new SimpleAdapter(this, data,
			                                      android.R.layout.simple_list_item_2,
			                                      new String[] {"title", "subtitle"},
			                                      new int[] {android.R.id.text1,
			                                                 android.R.id.text2});
			lw.setAdapter(adapter);
			
			
			// Se ho connessione raccolgo i dati sensibili online
			if (RemoteResources.haveNetworkConnection(IdentifyResultActivity.this)){
				
			    TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
				new IndentifyTask().execute(cu, telephonyManager.getDeviceId());
				
			} else {
				
				// Se non ho connessione lo nofifico
				final AlertDialog.Builder adb = new AlertDialog.Builder(this);
			    adb.setTitle("Connessione Assente");
			    adb.setMessage("Per visualizzare i dati oscurati e' necessario essere connessi ad internet");
			    adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) {
			      } });
			}	
		}
	}
	
	/**
	 * Task asincrono per l'identificazione
	 * 
	 * @author Nicola Corti
	 */
	private class IndentifyTask extends AsyncTask<String, Void, String>{
		
		/** Progress bar per il caricamento */
		ProgressBar pbIdent = null;
		
		@Override
		protected void onPreExecute() {
			// Mostro la barra
			pbIdent = (ProgressBar)findViewById(R.id.identify_progress);
			pbIdent.setVisibility(View.VISIBLE);
		}
		
		@Override
		protected void onPostExecute(String result) {
		    // Tolgo la barra e aggiorno la lista
			pbIdent.setVisibility(View.GONE);
			adapter = new SimpleAdapter(IdentifyResultActivity.this, data,
                    android.R.layout.simple_list_item_2,
                    new String[] {"title", "subtitle"},
                    new int[] {android.R.id.text1,
                               android.R.id.text2});
			lw.setAdapter(adapter);
		}
		
		@Override
		protected String doInBackground(String... params) {
			
			// Preparo la richiesta Post
			ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
			postParams.add(new BasicNameValuePair("date", UserData.getInstance().getDate()));
			postParams.add(new BasicNameValuePair("cu", UserData.getInstance().getCU().substring(0, UserData.getInstance().getCU().length()-2)));
	        postParams.add(new BasicNameValuePair("reprint", UserData.getInstance().getCU().substring(UserData.getInstance().getCU().length()-1)));
			postParams.add(new BasicNameValuePair("search", params[0]));
	        postParams.add(new BasicNameValuePair("imei", params[1]));
	        
			String res = null;
			String response = null;
			try{
				response = CustomHttpClient.executeHttpPostString("http://mobile.rn2014.it/ident.php", postParams);
				res = response.toString();
				res = res.replaceAll("\\s+","");
				
				// Parso l'oggetto JSON che mi e' tornato
				JSONObject parsedObject = new JSONObject(res);
				String nome  = parsedObject.getString("nome");
				String cognome  = parsedObject.getString("cognome");
				String datanascita  = parsedObject.getString("data");
				
				datumNome.put("title", nome);
				datumCognome.put("title", cognome);
				datumDataNascita.put("title", datanascita);
				
				Log.e("Found ", nome + " " + cognome + " " + datanascita);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return res;
		}
	}
}
