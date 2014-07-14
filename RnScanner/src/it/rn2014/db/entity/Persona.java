package it.rn2014.db.entity;


//CREATE TABLE persone ( 
//	    codiceUnivoco VARCHAR PRIMARY KEY,
//	    ristampaBadge NUMERIC,
//	    nome          VARCHAR,
//	    cognome       VARCHAR,
//	    idGruppo      VARCHAR,
//	    ruolo         VARCHAR REFERENCES ruolo,
//	    codiceAgesci  VARCHAR,
//	    idUnita       VARCHAR,
//	    sottocampo    VARCHAR,
//	    quartiere     VARCHAR 
//	);


public class Persona {
	
	private String nome ;
	private String cognome ;
	private String ruolo ;
	private String codiceAgesci ;
	private String idGruppo ;
	private String idUnita ;
	private String codiceUnivoco  ;
	private String ristampaBadge ;
	private String quartiere ;
	private String contrada ;
	
	
	
	public String getIdGruppo() {
		return idGruppo;
	}
	public void setIdGruppo(String idGruppo) {
		this.idGruppo = idGruppo;
	}
	public String getIdUnita() {
		return idUnita;
	}
	public void setIdUnita(String idUnita) {
		this.idUnita = idUnita;
	}
	public String getCodiceUnivoco() {
		return codiceUnivoco;
	}
	public void setCodiceUnivoco(String codiceUnivoco) {
		this.codiceUnivoco = codiceUnivoco;
	}
	public String getRistampaBadge() {
		return ristampaBadge;
	}
	public void setRistampaBadge(String ristampaBadge) {
		this.ristampaBadge = ristampaBadge;
	}
	public String getQuartiere() {
		return quartiere;
	}
	public void setQuartiere(String quartiere) {
		this.quartiere = quartiere;
	}
	public String getContrada() {
		return contrada;
	}
	public void setContrada(String contrada) {
		this.contrada = contrada;
	}
	
	

	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getCognome() {
		return cognome;
	}
	public void setCognome(String cognome) {
		this.cognome = cognome;
	}
	public String getRuolo() {
		return ruolo;
	}
	public void setRuolo(String ruolo) {
		this.ruolo = ruolo;
	}
	public String getCodiceAgesci() {
		return codiceAgesci;
	}
	public void setCodiceAgesci(String codiceAgesci) {
		this.codiceAgesci = codiceAgesci;
	}

	
	

}
	

