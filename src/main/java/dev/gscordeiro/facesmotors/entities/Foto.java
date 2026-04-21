package dev.gscordeiro.facesmotors.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;

@Entity
public class Foto {

	@Id @GeneratedValue
	private Long id;
	private String nome;
	@Lob
	private byte[] content;
	@ManyToOne
	private Automovel automovel;
	
	public Foto() {
	}
	
	public Foto(String nome, byte[] content, Automovel automovel) {
		this.nome = nome;
		this.content = content;
		this.automovel = automovel;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public byte[] getContent() {
		return content;
	}
	public void setContent(byte[] content) {
		this.content = content;
	}
	public Automovel getAutomovel() {
		return automovel;
	}
	public void setAutomovel(Automovel automovel) {
		this.automovel = automovel;
	}
}
