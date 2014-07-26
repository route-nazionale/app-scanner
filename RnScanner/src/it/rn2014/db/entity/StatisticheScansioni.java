package it.rn2014.db.entity;

import it.rn2014.scanner.UserData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;

/**
 * Classe del modello.
 * Rappresenta un entita' di tipo statistiche, utile per gestire le scansioni effettuate
 * tramite il Database
 * 
 * @author Nicola Corti
 */
public class StatisticheScansioni {
	
	// Costanti del tipo di scansione
	/** Scansione invalida (codice strano o assente) */
	public static final String INVALID = "invalid";
	/** Scansione autorizzata */
	public static final String AUTH = "auth";
	/** Scansione non autorizzata */
	public static final String NOT_AUTH = "not_auth";
	/** Scansione con entrata (al varco) valida */
	public static final String VALID_ENTER = "valid_enter";
	/** Scansione con uscita (al varco) valida */
	public static final String VALID_EXIT = "valid_exit";
	/** Scansione con annullata dall'utente */
	public static final String USER_ABORT = "user_abort";
	
	/** Codice univoco persona */
    private String codiceUnivoco ; // VARCHAR,
    /** Codice ristampa badge */
    private String codiceRistampa ; // VARCHAR,
    /** Time stamp della stampa */
    private String time          ; // VARCHAR,
    /** Codice univoco operatore */
    private String operatore     ; // VARCHAR,
    /** Turno di esecuzione (per l'evento) */
    private int turno          ; // INTEGER,
    /** Imei del dispositivo */
    private String imei          ; // VARCHAR,
    /** Tipo della scansione (vedi costanti) */
    private String type 	      ; // VARCHAR,
    /** Sincronizzato? (flag) */
    private boolean sync          ; // BOOLEAN
    /** ID del varco */
    private String idVarco       ; // VARCHAR
    
    @SuppressLint("SimpleDateFormat")
	public StatisticheScansioni(){
    	imei = "";
    	turno = UserData.getInstance().getTurn();
    	sync = false;
    	idVarco = UserData.getInstance().getEvent();
    	String operatorCU = UserData.getInstance().getCU();
    	operatore = operatorCU.substring(0, operatorCU.length()-2);
    	type = "";
    	
    	// Quando creo l'oggetto salvo il time stamp
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        time = dateFormat.format(new Date()); // Find todays date
    }
    
	public String getCodiceUnivoco() {
		return codiceUnivoco;
	}
	public void setCodiceUnivoco(String codiceUnivoco) {
		this.codiceUnivoco = codiceUnivoco;
	}
	public String getCodiceRistampa() {
		return codiceRistampa;
	}
	public void setCodiceRistampa(String codiceRistampa) {
		this.codiceRistampa = codiceRistampa;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getOperatore() {
		return operatore;
	}
	public void setOperatore(String operatore) {
		this.operatore = operatore;
	}
	public int getTurno() {
		return turno;
	}
	public void setTurno(int turno) {
		this.turno = turno;
	}
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	
	// Metodi per impostare lo stato
	public void setInvalid() { this.type = INVALID; }
	public void setAuth() { this.type = AUTH; }
	public void setNotAuth() { this.type = NOT_AUTH; }
	public void setEnter() { this.type = VALID_ENTER; }
	public void setExit() { this.type = VALID_EXIT; }
	public void setAbort() { this.type = USER_ABORT; }
	
	public String getType() {
		return this.type;
	}
	
	public boolean isSync() {
		return sync;
	}
	public void setSync(boolean sync) {
		this.sync = sync;
	}
	public String getIdVarco() {
		return idVarco;
	}
	public void setIdVarco(String idVarco) {
		this.idVarco = idVarco;
	}
    public String toString(){
        return ""+codiceUnivoco+codiceRistampa+" "+idVarco;
    }
        
    /**
     * Ritorna la rappresentazione JSON di un oggetto
     * di tipo StatisticheScansioni
     * 
     * @return Un JSONObject della scansione
     */
    public JSONObject toJSONObject(){
    	JSONObject jsonObject= new JSONObject();
        try {
        	
        	// Inserisco i vari campi
            jsonObject.put("cu", getCodiceUnivoco());
            jsonObject.put("reprint", getCodiceRistampa());
            jsonObject.put("time", getTime());
            jsonObject.put("operator", getOperatore());
            jsonObject.put("gate", getIdVarco());
            jsonObject.put("turn", getTurno());
            jsonObject.put("imei", getImei());
            jsonObject.put("type", getType());
            return jsonObject;
        } catch (JSONException e) {
           
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Statico - Ritorna la rappresentazione in Stringa del JSON
     * di un insieme di StatisticheScansioni
     * 
     * @param arr Un Arraylist di statistiche scansioni
     * @return La rappresentazione completa
     */
    public static String toJSONArray(ArrayList<StatisticheScansioni> arr) {
    	
    	// Creo l'array json
    	JSONArray jsonArray = new JSONArray();
        for (StatisticheScansioni s : arr){
			jsonArray.put(s.toJSONObject());
		}
        
        // Inserisci nell'oggetto update
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("update",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
		return jsonObject.toString();
    }

}