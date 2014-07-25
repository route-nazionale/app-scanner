package it.rn2014.db.entity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class StatisticheScansioni {
	
	public static final String INVALID = "invalid";
	public static final String AUTH = "auth";
	public static final String NOT_AUTH = "not_auth";
	
    private int idScansione   ; // INTEGER PRIMARY KEY AUTOINCREMENT,
    private String codiceUnivoco ; // VARCHAR,
    private String codiceRistampa ; // VARCHAR,
    private String time          ; // VARCHAR,
    private String operatore     ; // VARCHAR,
    private int turno          ; // INTEGER,
    private String imei          ; // VARCHAR,
    private String type 	      ; // VARCHAR,
    private boolean sync          ; // BOOLEAN 
    private String idVarco       ; // VARCHAR
    
	public int getIdScansione() {
		return idScansione;
	}
	public void setIdScansione(int idScansione) {
		this.idScansione = idScansione;
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
	public void setInvalid() { this.type = INVALID; }
	public void setAuth() { this.type = AUTH; }
	public void setNotAuth() { this.type = NOT_AUTH; }
	
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
        return "["+idScansione+"] "+codiceUnivoco+codiceRistampa+" "+idVarco;
    }
    
    public String toJSON(){
    	JSONObject jsonObject= new JSONObject();
        try {
            jsonObject.put("cu", getCodiceUnivoco());
            jsonObject.put("reprint", getCodiceRistampa());
            jsonObject.put("time", getTime());
            jsonObject.put("operator", getOperatore());
            jsonObject.put("gate", getIdVarco());
            jsonObject.put("turn", getTurno());
            jsonObject.put("imei", getImei());
            jsonObject.put("type", getType());
            return jsonObject.toString();
        } catch (JSONException e) {
           
            e.printStackTrace();
            return "";
        }
    }
    
    public static String toJSONArray(ArrayList<StatisticheScansioni> arr) {
    	
    	JSONArray jsonArray = new JSONArray();
        for (StatisticheScansioni s : arr){
			jsonArray.put(s);
		}
        
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("update",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
		return jsonObject.toString();
    }
}