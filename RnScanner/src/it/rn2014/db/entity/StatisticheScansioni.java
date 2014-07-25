package it.rn2014.db.entity;

import android.annotation.SuppressLint;
import it.rn2014.scanner.UserData;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class StatisticheScansioni {
	
	public static final String INVALID = "invalid";
	public static final String AUTH = "auth";
	public static final String NOT_AUTH = "not_auth";
	public static final String VALID_ENTER = "valid_enter";
	public static final String VALID_EXIT = "valid_exit";
	public static final String USER_ABORT = "user_abort";
	
    private String codiceUnivoco ; // VARCHAR,
    private String codiceRistampa ; // VARCHAR,
    private String time          ; // VARCHAR,
    private String operatore     ; // VARCHAR,
    private int turno          ; // INTEGER,
    private String imei          ; // VARCHAR,
    private String type 	      ; // VARCHAR,
    private boolean sync          ; // BOOLEAN 
    private String idVarco       ; // VARCHAR
    
    @SuppressLint("SimpleDateFormat")
	public StatisticheScansioni(){
    	imei = UserData.getInstance().getImei();
    	turno = UserData.getInstance().getTurn();
    	sync = false;
    	idVarco = UserData.getInstance().getEvent();
    	String operatorCU = UserData.getInstance().getCU();
    	operatore = operatorCU.substring(0, operatorCU.length()-2);
    	type = "";
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
        
    public JSONObject toJSONObject(){
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
            return jsonObject;
        } catch (JSONException e) {
           
            e.printStackTrace();
            return null;
        }
    }
    
    public static String toJSONArray(ArrayList<StatisticheScansioni> arr) {
    	
    	JSONArray jsonArray = new JSONArray();
        for (StatisticheScansioni s : arr){
			jsonArray.put(s.toJSONObject());
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