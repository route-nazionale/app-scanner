package it.rn2014.db.entity;

public class Evento {
	
	private String idEvento;
	private String nome;
	private String quartiere;
	private String stradaCoraggio;
	private String contrada;
	private String tipoEvento;
	
	@Override
	public String toString() {
		System.out.println("idEvento " + this.idEvento );
		System.out.println("nome " + this.nome );
		System.out.println("quartiere " + this.quartiere );
		System.out.println("stradaCoraggio " + this.stradaCoraggio );
		System.out.println("contrada " + this.contrada );
		System.out.println("tipoEvento " + this.tipoEvento );
				
		return "";
	}
	
	public String getIdEvento() {
		return idEvento;
	}
	public void setIdEvento(String idEvento) {
		this.idEvento = idEvento;
	}
	public void setQuartiere(String quartiere) {
		this.quartiere = quartiere;
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
	
	
}
