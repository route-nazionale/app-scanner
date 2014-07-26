package it.rn2014.scanner;

import it.rn2014.db.StatsManager;
import it.rn2014.db.entity.StatisticheScansioni;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import android.content.Context;

/**
 * Classe che raccoglie i dati utente dell'utilizzatore
 * - Gestita con pattern Sigleton
 * - Permette di essere serializzata/deserializzata
 * 
 * @author Nicola Corti
 */
public class UserData implements Serializable {
	
	/** Version UID della classe */
	private static final long serialVersionUID = 1L;
	/** Nome del file per la serializzazione */
	private static final String FILENAME = "userdata";

	/** Istanza singleton */
	private static UserData instance= null;

	/** Codice univoco */
	private String cu = null;
    /** Data di nascita */
	private String date = null;
    /** Livello di autenticazione */
	private String authLevel = null;
    /** Ultimo evento selezionato */
	private String lastEvent = null;
	/** Ultimo turno selezionato */
    private int lastEventTurn = 0;
    /** Ultima modalita' scelta */
    private String lastChoose = null;
    
    /**
     * Costruttore vuoto
     */
    private UserData(){}

    /**
     * Metodo che ritorna un'istanza di UserData.
     * Se non e' stata istanziata la ritorna
     * 
     * @return Riferimento allo UserData
     */
    public static synchronized UserData getInstance(){
    	if(null == instance){
    		instance = new UserData();
    	}
    	return instance;
    }
	
    /**Setter per data di nascita
     * @param d Data di nascita
     */
    public synchronized void setDate(String d){ this.date = d; }
    /**Setter per codice univoco
     * @param c Codice univoco
     */
    public synchronized void setCU(String c){ this.cu = c; }
    /**Setter per livello di autenticazione
     * @param l Livello di autenticazione
     */
    public synchronized void setLevel(String l){ this.authLevel = l; }
    /**Setter per ultima scelta della modalita'
     * @param l ultima scelta della modalita'
     */
    public synchronized void setChoose(String l){ this.lastChoose = l; }
    /**Setter per ultima scelta dell'evento
     * @param l ultima scelta dell'evento
     */
    public synchronized void setEvent(String l){ this.lastEvent = l; }
    /**Setter per ultima scelta del turno
     * @param t ultima scelta del turno
     */
    public synchronized void setTurn(int t){ this.lastEventTurn = t; }
    
    /**Ritorna la data di nascita 
     * @return Data di nascita
     */
    public synchronized String getDate(){ return this.date; }
    /**Ritorna il codice univoco
     * @return Codice univoco
     */
    public synchronized String getCU(){ return this.cu; }
    /**Ritorna il codice univoco senza reprint
     * @return Codice univoco senza reprint
     */
    public synchronized String getCUnoReprint(){ return this.cu.substring(0, this.cu.length()-2); }
    /**Ritorna il livello di autenticazione
     * @return Il livello di autenticazione
     */
    public synchronized String getLevel(){ return this.authLevel; }
    /**Ritorna l'ultima scelta della modalita'
     * @return ultima scelta della modalita'
     */
    public synchronized String getChoose(){ return this.lastChoose; }
    /**Ritorna l'ultima scelta del'evento
     * @return ultima scelta del'evento
     */
    public synchronized String getEvent(){ return this.lastEvent; }
    /**Ritorna l'ultima scelta del turno
     * @return ultima scelta del turno
     */
    public synchronized int getTurn(){ return this.lastEventTurn; }
    /**Ritorna il numero di eventi da sincronizzare
     * @param c Contesto di esecuzione
     * @return Numero di eventi da sincronizzare
     */
    public synchronized int getToSync(Context c){ 
		// Recupero le statistiche da sincronizzare
		StatsManager qm = StatsManager.getInstance(c);
        ArrayList<StatisticheScansioni> ls = qm.findAllStatsNotSync();
        return ls.size();
    }
    
    
    /**
     * Metodo che effettua il logout dell'utente.
     */
    public synchronized void logOut() {
    	this.date = null;
    	this.cu = null;
    	this.authLevel = null;
    	this.lastChoose = null;
    	this.lastEvent = null;
    	this.lastEventTurn = 0;
	}
    
    /**
     * Serializza lo UserData
     * 
     * @param c Contesto di esecuzione
     */
    public static synchronized void saveInstance(Context c){
    	FileOutputStream fos;
		try {
			fos = c.openFileOutput(FILENAME, Context.MODE_PRIVATE);
	    	ObjectOutputStream oos = new ObjectOutputStream(fos);
	    	oos.writeObject(instance);
	    	if (oos != null) oos.close();
	    	if (fos != null) fos.close();
	    	
		} catch (FileNotFoundException e) { e.printStackTrace(); }
		  catch (IOException e) { e.printStackTrace(); }
    }
    
    /**
     * Deserializza lo UserData
     * 
     * @param c Contesto di esecuzione
     * @return True se e' andato a buon fine, false altrimenti
     */
    public static synchronized boolean restoreInstance(Context c){
    	FileInputStream fis;
		try {
			fis = c.openFileInput(FILENAME);
	    	ObjectInputStream ois = new ObjectInputStream(fis);
	    	instance = (UserData) ois.readObject();
	    	if (ois != null) ois.close();
	    	if (fis != null) fis.close();
	    	
		} catch (Exception e) { return false; }
		return true;
    }
}
