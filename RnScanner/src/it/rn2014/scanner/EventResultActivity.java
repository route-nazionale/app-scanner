package it.rn2014.scanner;

import it.rn2014.db.DataManager;
import it.rn2014.db.StatsManager;
import it.rn2014.db.entity.Evento;
import it.rn2014.db.entity.Persona;
import it.rn2014.db.entity.StatisticheScansioni;

import java.util.ArrayList;

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
 * Interfaccia che mostra i risultati di una scansione varchi eventi
 * 
 * @author Nicola Corti
 */
public class EventResultActivity extends Activity implements OnClickListener {
	
	/** Costante per scansione invalida */
	private final static int INVALID_SCAN = -1;
	/** Costante per scansione non autorizzata */
	private final static int NOT_AUTH = -2;
	/** Costante per scansione autorizzata */
	private final static int AUTH = -3;
	
	/** Lista degli eventi assegnati al ragazzo */
	private ArrayList<Evento> otherEvent = null;
	/** Nuova statistica della scansione effettuata */
	private StatisticheScansioni scan = new StatisticheScansioni();
	/** Status della scansione */
	private int status = INVALID_SCAN;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_result);
		
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
			    scan.setIdVarco(UserData.getInstance().getEvent());
			    scan.setTurno(UserData.getInstance().getTurn());
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
		    	
		    	result.setText(getResources().getString(R.string.partecipante));
		    	codetext.setText(code);
		    	background.setBackgroundColor(getResources().getColor(R.color.LightGreen));
		    	
		    	scan.setAuth();
		    	
		    } else if (status == NOT_AUTH) {
		    	
		    	/* Persona non autorizzata */
		    	TextView result = (TextView)findViewById(R.id.result);
		    	TextView codetext = (TextView)findViewById(R.id.code);
		    	LinearLayout background = (LinearLayout)findViewById(R.id.backGroundResult);
		    	
		    	scan.setNotAuth();
		    	
		    	result.setText(getResources().getString(R.string.non_partecipante));
		    	codetext.setText(code);
		    	background.setBackgroundColor(getResources().getColor(R.color.LightPink));
		    	
		    	exit.setVisibility(View.GONE);
		    	enter.setVisibility(View.GONE);
		    	
		    	if (otherEvent != null){
		    		
		    		// In caso di non autorizzato mostro l'elenco degli eventi
		    		// a cui deve partecipare
		    		Evento evt1 = (otherEvent.size() >= 1 ? otherEvent.get(0) : null);
		    		Evento evt2 = (otherEvent.size() >= 2 ? otherEvent.get(1) : null);
		    		Evento evt3 = (otherEvent.size() >= 3 ? otherEvent.get(2) : null);
		    		
		    		TextView event1 = (TextView)findViewById(R.id.event1);
			    	TextView event2 = (TextView)findViewById(R.id.event2);
			    	TextView event3 = (TextView)findViewById(R.id.event3);
			    	
			    	if (evt1 != null) event1.setText(evt1.getCodiceStampa() + " - " + evt1.getNome());
			    	if (evt2 != null) event2.setText(evt2.getCodiceStampa() + " - " + evt2.getNome());
			    	if (evt3 != null) event3.setText(evt3.getCodiceStampa() + " - " + evt3.getNome());
			    	
			    	// Coloro lo sfondo in base al sottocampo
			    	if (evt1 != null) event1.setBackgroundColor(getSubcampColor(evt1));
			    	if (evt2 != null) event2.setBackgroundColor(getSubcampColor(evt2));
			    	if (evt3 != null) event3.setBackgroundColor(getSubcampColor(evt3));
			    	
			    	if (evt1 != null) event1.setVisibility(View.VISIBLE);
			    	if (evt2 != null) event2.setVisibility(View.VISIBLE);
			    	if (evt3 != null) event3.setVisibility(View.VISIBLE);
		    	}
		    	
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
	 * Funzione che ritorna il colore del sottocampo
	 * 
	 * @param evt Evento di cui si vuole il colore
	 * @return Ritorna il colore associato
	 */
	private int getSubcampColor(Evento evt) {
		switch (Integer.parseInt(evt.getQuartiere())) {
		case 1: return getResources().getColor(R.color.LightYellow);
		case 2: return getResources().getColor(R.color.LightGreen);
		case 3: return getResources().getColor(R.color.Violet);
		case 4: return getResources().getColor(R.color.LightBlue);
		case 5: return getResources().getColor(R.color.LightGoldenrodYellow);
		case 6: return getResources().getColor(R.color.Red);
		default : return getResources().getColor(R.color.WhiteSmoke);
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
		int turn = UserData.getInstance().getTurn();
		
		try {
			cu = code.substring(0, code.length()-2);
			reprint = code.substring(code.length()-1);
			
			// Controlla se la persona e' presente nel DB
			Persona p = DataManager.getInstance(this).findPersonaByCodiceUnivoco(cu, reprint);
			if (p.getCodiceUnivoco() == "") return INVALID_SCAN;
			
			// Trova gli eventi della persona
			otherEvent = DataManager.getInstance(this).findEventiByPersona(p);
			ArrayList<Evento> ae = DataManager.getInstance(this).findEventiByPersona(p, turn);
			
			if (otherEvent.isEmpty()) return INVALID_SCAN;
			if (ae.isEmpty()) return NOT_AUTH;
			else {
				Evento evt = ae.get(0);
				// Controlla se la persona e' autorizzata per l'evento o no
				if (evt.getCodiceEvento().contentEquals(UserData.getInstance().getEvent())){
					return AUTH;
				} else {
					return NOT_AUTH;
				}
			}
		} catch (IndexOutOfBoundsException e ){
			return INVALID_SCAN;
		}
	}

	@Override
	public void onClick(View v) {
		// Memorizzo la selezione dell'operatore (entrata, uscita, etc...)
		Toast t = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
		if (v.getId() == R.id.btnEnter){
			t.setText("Check-in evento registrato");
			scan.setEnter();
		} else if (v.getId() == R.id.btnExit) {
			t.setText("Check-out evento registrato");
			scan.setExit();
		} else if (v.getId() == R.id.btnAbort) {
			t.setText("Scansione annullata");
			if (scan.getType() == StatisticheScansioni.AUTH)
				scan.setAbort();
		}
		
		// Salvo nel database la statistica
		StatsManager.getInstance(EventResultActivity.this).insertStats(scan);
		t.show();
		finish();
	}
}
