package it.rn2014.scanner;

import it.rn2014.db.DataManager;
import it.rn2014.db.entity.Evento;
import it.rn2014.db.entity.Persona;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class IdentifyResultActivity extends ActionBarActivity {

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
			
			Persona result = DataManager.getInstance(this).findPersonaByCodiceUnivoco(cu, reprint);
			ArrayList<Evento> ae = DataManager.getInstance(this).findEventiByPersona(result);
			
			ListView lw = (ListView)findViewById(R.id.listIdentify);
			
			ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
			Map<String, String> datum;
			
			datum = new HashMap<String, String>(2);
			datum.put("title", result.getNome());
			datum.put("subtitle", "Nome");
			data.add(datum);
			
			datum = new HashMap<String, String>(2);
			datum.put("title", result.getCognome());
			datum.put("subtitle", "Cognome");
			data.add(datum);
			
			datum = new HashMap<String, String>(2);
			datum.put("title", result.getCodiceUnivoco());
			datum.put("subtitle", "Codice Univoco");
			data.add(datum);
			
			datum = new HashMap<String, String>(2);
			datum.put("title", result.getCodiceAgesci());
			datum.put("subtitle", "Codice A.G.E.S.C.I.");
			data.add(datum);
			
			datum = new HashMap<String, String>(2);
			datum.put("title", result.getIdGruppo() + " - " + result.getIdUnita());
			datum.put("subtitle", "Gruppo - Unita'");
			data.add(datum);
			
			datum = new HashMap<String, String>(2);
			datum.put("title", result.getContrada() + " - " + result.getQuartiere());
			datum.put("subtitle", "Contrada - Quartiere");
			data.add(datum);
			
			for (int i = 0; i < ae.size(); i++){
				Evento e = ae.get(i);
				datum = new HashMap<String, String>(2);
				datum.put("title", e.getCodiceStampa() + " - " + e.getNome());
				datum.put("subtitle", "Turno: " + (i+1) + " Sottocampo evento: " + e.getQuartiere());
				data.add(datum);
			}			
			
			SimpleAdapter adapter = new SimpleAdapter(this, data,
			                                      android.R.layout.simple_list_item_2,
			                                      new String[] {"title", "subtitle"},
			                                      new int[] {android.R.id.text1,
			                                                 android.R.id.text2});
			lw.setAdapter(adapter);
		}
	}
}
