package br.com.casadocodigo.jsfjpa.controllers;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.persistence.EntityManager;

import br.com.casadocodigo.jsfjpa.entities.Marca;
import br.com.casadocodigo.jsfjpa.persistence.JpaUtil;


@ManagedBean
@ViewScoped
public class MarcaBean implements Serializable {

	private static final long serialVersionUID = 2806365279342807551L;

	private Marca marca;
	private List<Marca> marcas;
	private boolean continuarInserindo;
	
	@PostConstruct
	public void init(){
		marca = new Marca();
	}

	public void salvar() {
		EntityManager em = JpaUtil.getEntityManager();
		em.persist(marca);
		
	}

	public List<Marca> getMarcas() {
		if (marcas == null) {
			marcas = JpaUtil.getEntityManager().createQuery("select m from Marca m", Marca.class).getResultList();
		}

		return marcas;
	}
	public void setMarcas(List<Marca> marcas) {
		this.marcas = marcas;
	}

	public Marca getMarca() {
		return marca;
	}

	public void setMarca(Marca marca) {
		this.marca = marca;
	}

	public boolean isContinuarInserindo() {
		return continuarInserindo;
	}

	public void setContinuarInserindo(boolean continuarInserindo) {
		this.continuarInserindo = continuarInserindo;
	}


	
}
