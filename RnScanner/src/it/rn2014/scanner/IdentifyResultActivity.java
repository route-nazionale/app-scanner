package it.rn2014.scanner;

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
			
			ListView lw = (ListView)findViewById(R.id.listIdentify);
			
			ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
			Map<String, String> datum;
			
			datum = new HashMap<String, String>(2);
			datum.put("title", "Pinco");
			datum.put("subtitle", "Nome");
			data.add(datum);
			
			datum = new HashMap<String, String>(2);
			datum.put("title", "Pallino");
			datum.put("subtitle", "Cognome");
			data.add(datum);
			
			datum = new HashMap<String, String>(2);
			datum.put("title", code);
			datum.put("subtitle", "Codice Univoco");
			data.add(datum);
			
			datum = new HashMap<String, String>(2);
			datum.put("title", "01/01/2001");
			datum.put("subtitle", "Data di nascita");
			data.add(datum);
			
			datum = new HashMap<String, String>(2);
			datum.put("title", "Pisa 4 - S1367 T1");
			datum.put("subtitle", "Gruppo - Unita'");
			data.add(datum);
			
			datum = new HashMap<String, String>(2);
			datum.put("title", "5 - 18");
			datum.put("subtitle", "Sottocampo - Contrada");
			data.add(datum);
			
			datum = new HashMap<String, String>(2);
			datum.put("title", "LAB 123 - Laboratorio 1123");
			datum.put("subtitle", "Evento primo turno");
			data.add(datum);
			
			datum = new HashMap<String, String>(2);
			datum.put("title", "LAB 567 - Laboratorio 1123");
			datum.put("subtitle", "Evento secondo turno");
			data.add(datum);
			
			datum = new HashMap<String, String>(2);
			datum.put("title", "TAV 123 - Laboratorio 1123");
			datum.put("subtitle", "Evento terzo turno");
			data.add(datum);
			
			
			SimpleAdapter adapter = new SimpleAdapter(this, data,
			                                      android.R.layout.simple_list_item_2,
			                                      new String[] {"title", "subtitle"},
			                                      new int[] {android.R.id.text1,
			                                                 android.R.id.text2});
			lw.setAdapter(adapter);
		}
	}
}
