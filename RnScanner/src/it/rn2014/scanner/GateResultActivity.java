package it.rn2014.scanner;

import it.rn2014.db.DataManager;
import it.rn2014.db.StatsManager;
import it.rn2014.db.entity.Persona;
import it.rn2014.db.entity.StatisticheScansioni;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Interfaccia che mostra i risultati di una scansione varchi accesso
 * 
 * @author Nicola Corti
 */
public class GateResultActivity extends Activity implements OnClickListener {
	
	/** Costante per scansione invalida */
	private final static int INVALID_SCAN = -1;
	/** Costante per scansione non autorizzata */
	private final static int NOT_AUTH = -2;
	/** Costante per scansione autorizzata */
	private final static int AUTH = -3;
	
	/** Nuova statistica della scansione effettuata */
	private StatisticheScansioni scan = new StatisticheScansioni();
	/** Status della scansione */
	private int status = INVALID_SCAN;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gate_result);
		
		Button enter = (Button)findViewById(R.id.btnEnter);
		Button exit = (Button)findViewById(R.id.btnExit);
		Button abort = (Button)findViewById(R.id.btnAbort);
		enter.setOnClickListener(this);
		exit.setOnClickListener(this);
		abort.setOnClickListener(this);
			
		Bundle extras = getIntent().getExtras();
		if (extras != null && extras.containsKey("qrscanned")) {
			
		    String code = extras.getString("qrscanned");
		    if (code == null) finish();
		    
		    try {
		    	
		    	// Scompongo il qr code
		    	String cu = code.substring(0, code.length()-2);
		    	String reprint = code.substring(code.length()-1);
			    status = computeStatus(code);
			    
			    // Imposto i dati della scansione nella statistica
			    scan.setCodiceUnivoco(cu);
			    scan.setCodiceRistampa(reprint);
			    scan.setIdVarco("ACCESSO");
			    scan.setTurno(0);
	            TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
	            scan.setImei(telephonyManager.getDeviceId());
			    
		    } catch (IndexOutOfBoundsException e) {
		    	status = INVALID_SCAN;
		    }
		    

			if (status == AUTH){
		    	/* Persona autorizzata */
				
		    	TextView result = (TextView)findViewById(R.id.result);
		    	TextView codetext = (TextView)findViewById(R.id.code);
		    	LinearLayout background = (LinearLayout)findViewById(R.id.backGroundResult);
		    	
		    	result.setText(getResources().getString(R.string.autorizzato));
		    	codetext.setText(code);
		    	background.setBackgroundColor(getResources().getColor(R.color.LightGreen));
				
		    	scan.setAuth();
				
		    } else if (status == NOT_AUTH) {
		    	
				/* Persona non autorizzata */
				TextView result = (TextView)findViewById(R.id.result);
		    	TextView codetext = (TextView)findViewById(R.id.code);
		    	LinearLayout background = (LinearLayout)findViewById(R.id.backGroundResult);
		    	
		    	result.setText(getResources().getString(R.string.non_autorizzato));
		    	codetext.setText(code);
		    	background.setBackgroundColor(getResources().getColor(R.color.LightPink));
		    	
		    	exit.setVisibility(View.INVISIBLE);
		    	enter.setVisibility(View.INVISIBLE);
		    	
		    	scan.setNotAuth();
		    	
		    } else {
		    	
		    	/* Scansione invalida */
		    	TextView result = (TextView)findViewById(R.id.result);
		    	TextView codetext = (TextView)findViewById(R.id.code);
		    	LinearLayout background = (LinearLayout)findViewById(R.id.backGroundResult);
		    	
		    	result.setText(getResources().getString(R.string.invalido));
		    	codetext.setText(code);
		    	background.setBackgroundColor(getResources().getColor(R.color.LightBlue));
		    	
		    	exit.setVisibility(View.INVISIBLE);
		    	enter.setVisibility(View.INVISIBLE);
		    	
		    	scan.setInvalid();
		    	
		    }
		    

		} else {
			finish();
		}
	}
	
	/**
	 * Calcola lo stato della scansione effettuata (valida, non valida, etc...)
	 * 
	 * @param code QR Scansionato
	 * @return Lo stato calcolato
	 */
	private int computeStatus(String code) {
		String cu;
		String reprint;
		
		try {
			
			cu = code.substring(0, code.length()-2);
			reprint = code.substring(code.length()-1);
			
			Persona p = DataManager.getInstance(this).findPersonaByCodiceUnivoco(cu, reprint);
			if (p.getCodiceUnivoco() == "") return INVALID_SCAN;
			return AUTH;
			
			/* TODO ritornare non valido? Se si quando?
			 */
			
		} catch (IndexOutOfBoundsException e ){
			return INVALID_SCAN;
		}
	}


	@Override
	public void onClick(View v) {
		// Memorizzo la selezione dell'operatore (entrata, uscita, etc...)
		Toast t = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
		if (v.getId() == R.id.btnEnter){
			t.setText("Accesso al varco registrato");
			scan.setEnter();
		} else if (v.getId() == R.id.btnExit) {
			t.setText("Escita dal varco registrata");
			scan.setExit();
		} else if (v.getId() == R.id.btnAbort) {
			t.setText("Scansione annullata");
			if (scan.getType() == StatisticheScansioni.AUTH)
				scan.setAbort();
		}
		
		// Salvo nel database la statistica
		StatsManager.getInstance(GateResultActivity.this).insertStats(scan);
		t.show();
		finish();
	}
}
