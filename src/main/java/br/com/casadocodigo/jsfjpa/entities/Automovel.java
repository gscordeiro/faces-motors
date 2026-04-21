package br.com.casadocodigo.jsfjpa.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.QueryHint;
import jakarta.validation.constraints.Min;

import br.com.casadocodigo.jsfjpa.validation.MaxAnoAtualMais;



@NamedQueries({
	@NamedQuery(name=Automovel.LISTAR_DESTAQUES, query="select a from Automovel a", hints={
			@QueryHint(name="org.hibernate.cacheable", value="true"),
			@QueryHint(name="org.hibernate.cacheRegion", value=Automovel.LISTAR_DESTAQUES)})
})
@Entity
@Cacheable
public class Automovel implements Serializable {

	private static final long serialVersionUID = 9183366721265460766L;

	public static final String LISTAR_DESTAQUES = "Automovel.buscarDestaques";
	
	@Id @GeneratedValue
	private Long id;
	@ManyToOne
	private Modelo modelo;
	@ManyToOne
	private Cor cor;
	@OneToMany(mappedBy="automovel", cascade=CascadeType.ALL)
	private List<Foto> fotos;
	@Min(1900) @MaxAnoAtualMais(message="O Valor máximo do ano de fabricação é {0}")
	private Integer anoFabricacao;
	@Min(1900) @MaxAnoAtualMais(value=1, message="O Valor máximo do ano do modelo é {0}")
	private Integer anoModelo;
	private Float preco;
	private Float kilometragem;
	private String observacoes;
	
	public Automovel() {
		fotos = new ArrayList<>();
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Modelo getModelo() {
		return modelo;
	}
	public void setModelo(Modelo modelo) {
		this.modelo = modelo;
	}
	public Cor getCor() {
		return cor;
	}
	public void setCor(Cor cor) {
		this.cor = cor;
	}
	public Integer getAnoFabricacao() {
		return anoFabricacao;
	}
	public void setAnoFabricacao(Integer anoFabricacao) {
		this.anoFabricacao = anoFabricacao;
	}
	public Integer getAnoModelo() {
		return anoModelo;
	}
	public void setAnoModelo(Integer anoModelo) {
		this.anoModelo = anoModelo;
	}
	public Float getPreco() {
		return preco;
	}
	public void setPreco(Float preco) {
		this.preco = preco;
	}
	public String getObservacoes() {
		return observacoes;
	}
	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}
	public List<Foto> getFotos() {
		return fotos;
	}
	public void setFotos(List<Foto> fotos) {
		this.fotos = fotos;
	}
	public Float getKilometragem() {
		return kilometragem;
	}
	public void setKilometragem(Float kilometragem) {
		this.kilometragem = kilometragem;
	}
	
}
