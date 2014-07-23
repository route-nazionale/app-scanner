package it.rn2014.db.entity;

public class StatisticheScansioni {
	
    private int idScansione   ; // INTEGER PRIMARY KEY AUTOINCREMENT,
    private String codiceUnivoco ; // VARCHAR,
    private String codiceRistampa ; // VARCHAR,
    private String time          ; // VARCHAR,
    private String operatore     ; // VARCHAR,
    private int slot          ; // INTEGER,
    private String imei          ; // VARCHAR,
    private boolean errore        ; // BOOLEAN,
    private boolean entrata       ; // BOOLEAN,
    private boolean sync          ; // BOOLEAN 
    private String idEvento       ; 
    
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
	public int getSlot() {
		return slot;
	}
	public void setSlot(int slot) {
		this.slot = slot;
	}
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public boolean isErrore() {
		return errore;
	}
	public void setErrore(boolean errore) {
		this.errore = errore;
	}
	public boolean isEntrata() {
		return entrata;
	}
	public void setEntrata(boolean entrata) {
		this.entrata = entrata;
	}
	public boolean isSync() {
		return sync;
	}
	public void setSync(boolean sync) {
		this.sync = sync;
	}
	public String getIdEvento() {
		return idEvento;
	}
	public void setIdEvento(String idEvento) {
		this.idEvento = idEvento;
	}
    public String toString(){
        return "["+idScansione+"] "+codiceUnivoco+codiceRistampa+" "+idEvento;
    }
}