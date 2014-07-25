package it.rn2014.scanner;

import it.rn2014.db.DataManager;
import it.rn2014.db.StatsManager;
import it.rn2014.db.entity.Persona;
import it.rn2014.db.entity.StatisticheScansioni;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GateResultActivity extends Activity implements OnClickListener {
	
	private final static int INVALID_SCAN = -1;
	private final static int NOT_AUTH = -2;
	private final static int AUTH = -3;
	
	
	private StatisticheScansioni scan = new StatisticheScansioni();
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
		    	
		    	String cu = code.substring(0, code.length()-2);
		    	String reprint = code.substring(code.length()-1);
			    status = computeStatus(code);
			    
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
				/* Persona non autorizzata */
		    	TextView result = (TextView)findViewById(R.id.result);
		    	TextView codetext = (TextView)findViewById(R.id.code);
		    	LinearLayout background = (LinearLayout)findViewById(R.id.backGroundResult);
		    	
		    	result.setText(getResources().getString(R.string.autorizzato));
		    	codetext.setText(code);
		    	background.setBackgroundColor(getResources().getColor(R.color.LightGreen));
				
				
		    } else if (status == NOT_AUTH) {
		    	
				TextView result = (TextView)findViewById(R.id.result);
		    	TextView codetext = (TextView)findViewById(R.id.code);
		    	LinearLayout background = (LinearLayout)findViewById(R.id.backGroundResult);
		    	
		    	result.setText(getResources().getString(R.string.non_autorizzato));
		    	codetext.setText(code);
		    	background.setBackgroundColor(getResources().getColor(R.color.LightPink));
		    	
		    	exit.setVisibility(View.INVISIBLE);
		    	enter.setVisibility(View.INVISIBLE);
		    	
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
		    	
		    }
		    

		} else {
			finish();
		}
	}
	
	
	private int computeStatus(String code) {
		String cu;
		String reprint;
		
		try {
			
			cu = code.substring(0, code.length()-2);
			reprint = code.substring(code.length()-1);
			
			Persona p = DataManager.getInstance(this).findPersonaByCodiceUnivoco(cu, reprint);
			if (p.getCodiceUnivoco() == "") return INVALID_SCAN;
			return AUTH;
			
			/* TODO ritornare non valido?
			 */
			
		} catch (IndexOutOfBoundsException e ){
			return INVALID_SCAN;
		}
	}


	@Override
	public void onClick(View v) {
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
		StatsManager.getInstance(GateResultActivity.this).insertStats(scan);
		Log.e("Inserito ", scan.toJSONObject().toString());
		t.show();
		finish();
	}
}
