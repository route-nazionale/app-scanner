package it.rn2014.db.entity;

public class StatisticheScansioni {
	
    private int idScansione   ; // INTEGER PRIMARY KEY AUTOINCREMENT,
    private String codiceUnivoco ; // VARCHAR,
    private String codiceRisampa ; // VARCHAR,
    private String time          ; // VARCHAR,
    private String operatore     ; // VARCHAR,
    private String slot          ; // INTEGER,
    private String imei          ; // VARCHAR,
    private String errore        ; // BOOLEAN,
    private String entrata       ; // BOOLEAN,
    private String sync          ; // BOOLEAN 

}
