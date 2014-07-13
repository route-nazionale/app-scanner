package it.rn2014.SyncStats;

/**
 * Created by danger on 13/07/14.
 */
public class Statistics{
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

    /*
                idScansione   INTEGER PRIMARY KEY AUTOINCREMENT,
                codiceUnivoco VARCHAR,
                codiceRisampa VARCHAR,
                time          VARCHAR,
                operatore     VARCHAR,
                slot          INTEGER,
                imei          VARCHAR,
                errore        BOOLEAN,
                entrata       BOOLEAN,
                sync          BOOLEAN
             */
    private int idScansione;
    private String codiceUnivoco;
    private String codiceRistampa;
    private String time;
    private String operatore;
    private int slot;
    private String imei;
    private boolean errore;
    private boolean entrata;
    private boolean sync;

    public Statistics(){

    }
    public void setName(String name){

    }
    public String getName(){
        return "TODO";
    }

    public String toString(){
        return codiceUnivoco+codiceRistampa+" "+time+" "+imei+" "+entrata;
    }
}
