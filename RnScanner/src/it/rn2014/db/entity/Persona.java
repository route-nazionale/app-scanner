package it.rn2014.db.entity;

/**
 * Classe del modello.
 * Rappresenta un entita' di tipo persona, utile per gestire le persone
 * tramite il Database
 * 
 * @author Nicola Corti
 */
public class Persona {
	
	/** Codice univoco */
	private String codiceUnivoco  ;
	/** Numero ristampa badge */
	private String ristampaBadge ;
	/** Nome della persona */
	private String nome ;
	/** Cognome della persona */
	private String cognome ;
	/** Codice agesci */
	private String codiceAgesci ;
	/** ID Gruppo */
	private String idGruppo ;
	/** ID Unita */
	private String idUnita ;
	/** Quartiere */
	private String quartiere ;
	/** Contrada */
	private String contrada ;
	
	@Override
	public String toString() {

		System.out.println("Persona nome " + this.nome );
		System.out.println("Persona cognome " + this.cognome );
		System.out.println("Persona codiceAgesci " + this.codiceAgesci );
		System.out.println("Persona idGruppo " + this.idGruppo );
		System.out.println("Persona idUnita " + this.idUnita );
		System.out.println("Persona codiceUnivoco  " + this.codiceUnivoco );
		System.out.println("Persona ristampaBadge " + this.ristampaBadge );
		System.out.println("Persona quartiere " + this.quartiere );
		System.out.println("Persona contrada " + this.contrada );

		return super.toString();
	}
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
	public String getCodiceAgesci() {
		return codiceAgesci;
	}
	public void setCodiceAgesci(String codiceAgesci) {
		this.codiceAgesci = codiceAgesci;
	}
}