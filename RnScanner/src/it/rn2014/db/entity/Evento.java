package it.rn2014.db.entity;

/**
 * Classe del modello.
 * Rappresenta un entita' di tipo evento, utile per gestire gli eventi
 * tramite il Database
 * 
 * @author Nicola Corti
 */
public class Evento {
	
	/** ID univoco evento */
	private String idEvento;
	/** Codice di stampa dell'evento */
	private String codiceStampa;
	/** Nome dell'evento */
	private String nome;
	/** Quartiere dell'evento */
	private String quartiere;
	/** Strada di coraggio */
	private String stradaCoraggio;
	/** Contrata - NON UTILIZZATO */
	private String contrada;
	/** Tipologia dell'evento */
	private String tipoEvento;
	
	public String getIdEvento() {
		return idEvento;
	}
	public void setIdEvento(String idEvento) {
		this.idEvento = idEvento;
	}
	public void setQuartiere(String quartiere) {
		this.quartiere = quartiere;
	}
	public String getQuartiere() {
		return this.quartiere;
	}
	public String getStradaCoraggio() {
		return stradaCoraggio;
	}
	public void setStradaCoraggio(String stradaCoraggio) {
		this.stradaCoraggio = stradaCoraggio;
	}
	public String getContrada() {
		return contrada;
	}
	public void setContrada(String contrada) {
		this.contrada = contrada;
	}
	public String getTipoEvento() {
		return tipoEvento;
	}
	public void setTipoEvento(String tipoEvento) {
		this.tipoEvento = tipoEvento;
	}
	public String getCodiceEvento() {
		return idEvento;
	}
	public void setCodiceEvento(String codiceEvento) {
		this.idEvento = codiceEvento;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public void setSottocampo(String sottocampo) {
		this.quartiere = sottocampo;
	}
	public String getStrada() {
		return stradaCoraggio;
	}
	public void setStrada(String strada) {
		this.stradaCoraggio = strada;
	}
	public String getCodiceStampa() {
		return codiceStampa;
	}
	public void setCodiceStampa(String codiceStampa) {
		this.codiceStampa = codiceStampa;
	}
	@Override
	public String toString() {
		return this.codiceStampa + " - " + this.nome;
	}
	
}
