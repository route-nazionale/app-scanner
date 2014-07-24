package it.rn2014.scanner;

import java.util.ArrayList;

import it.rn2014.db.QueryManager;
import it.rn2014.db.entity.Evento;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ScanningActivity extends ActionBarActivity implements OnClickListener {

	String mode = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scanning);
		
		boolean b = QueryManager.getInstance(ScanningActivity.this).checkDataBase();
		Log.e("DATABASE", "IS " + b);
		
		Button btnScan = (Button)findViewById(R.id.btnBadge);
		btnScan.setOnClickListener(this);
		Button btnWrite = (Button)findViewById(R.id.btnWrite);
		btnWrite.setOnClickListener(this);
		
		if (savedInstanceState != null){
			mode = savedInstanceState.getString("mode");
		} else {
			Bundle extras = getIntent().getExtras();
			if (extras != null && extras.containsKey("mode")) {
				mode = extras.getString("mode");
			} else {
				mode = UserData.getInstance().getChoose();
			}
		}
		
		// Disegno l'interfaccia in base alla modalita'
	    if (mode.contentEquals("gate")){
			
	    	this.setTitle("Autenticazione Varchi");
	    	
	    	TextView title = (TextView)findViewById(R.id.title);
			TextView description = (TextView)findViewById(R.id.description);
			title.setText(R.string.title_gate);
			title.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.gate), null, null, null);
			description.setText(R.string.desc_gate);
			
			
	    } else if (mode.contentEquals("event")) {
	    	
	    	this.setTitle("Autenticazione Eventi");
	    	
	    	TextView title = (TextView)findViewById(R.id.title);
	    	TextView description = (TextView)findViewById(R.id.description);
	    	TextView event = (TextView)findViewById(R.id.eventText);
			TextView turn = (TextView)findViewById(R.id.turnText);
			
	    	
			title.setText(R.string.title_event);
			title.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.event), null, null, null);
			description.setText(R.string.desc_event);
			
			event.setVisibility(View.VISIBLE);
			turn.setVisibility(View.VISIBLE);
			
			String eventcode = UserData.getInstance().getEvent();
			Evento e = QueryManager.getInstance(this).findEventById(eventcode);
			if (e != null)
				event.setText(Html.fromHtml("Evento: <b>" + e.getCodiceStampa() + " - " + e.getNome() + "</b>"));
			turn.setText(Html.fromHtml("Turno: <b>" + UserData.getInstance().getTurn() + "</b>"));
	    	
	    	
	    } else if (mode.contentEquals("identify")) {
	    	
	    	this.setTitle("Identifica Soggetto");
	    	
	    	TextView title = (TextView)findViewById(R.id.title);
			TextView description = (TextView)findViewById(R.id.description);
	    	
			title.setText(R.string.title_identify);
			title.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.identify), null, null, null);
			description.setText(R.string.desc_identify);
	    }
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnWrite){
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			
			alert.setTitle("Codice Badge");
			alert.setMessage("Scrivi il codice badge da scansionare");

			final EditText code = new EditText(this);
			code.setHint(R.string.prompt_code);
			alert.setView(code);

			alert.setPositiveButton("Invia", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					String value = code.getText().toString();
					Intent login = null;
					// TODO da completare
					if (mode.contentEquals("identify"))
						login = new Intent(getApplicationContext(), IdentifyResultActivity.class);
					else if (mode.contentEquals("gate"))
						login = new Intent(getApplicationContext(), GateResultActivity.class);
					else if (mode.contentEquals("event"))
						login = new Intent(getApplicationContext(), EventResultActivity.class);
					
					if (login != null){
						login.putExtra("qrscanned", value);
						startActivity(login);
					}
				}
			});

			alert.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
				}
			});
			alert.show();
		} else if (v.getId() == R.id.btnBadge) {
			IntentIntegrator ii = new IntentIntegrator(this);
			ArrayList<String> formats = new ArrayList<String>();
			formats.add("QR_CODE");
			ii.initiateScan(formats);
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		
		Intent result = null;
		if (mode.contentEquals("gate"))
			result = new Intent(getApplicationContext(), GateResultActivity.class);
		if (mode.contentEquals("identify"))
			result = new Intent(getApplicationContext(), IdentifyResultActivity.class);
		if (mode.contentEquals("event"))
			result = new Intent(getApplicationContext(), EventResultActivity.class);
		
		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		if (scanResult != null && scanResult.getContents() != null && scanResult.getContents() != "" && result != null) {
			result.putExtra("qrscanned", scanResult.getContents());
			startActivity(result);
		} else {
			Log.e(this.getLocalClassName(), "Returned a wrong activity result");
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	    savedInstanceState.putString("mode", mode);
	    super.onSaveInstanceState(savedInstanceState);
	}
	
	public void onRestoreInstanceState(Bundle savedInstanceState) {
	    super.onRestoreInstanceState(savedInstanceState);
	    mode = savedInstanceState.getString("mode");
	}
}
