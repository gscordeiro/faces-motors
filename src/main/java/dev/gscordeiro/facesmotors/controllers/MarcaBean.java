package dev.gscordeiro.facesmotors.controllers;

import java.io.Serializable;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import dev.gscordeiro.facesmotors.entities.Marca;


@Named
@ViewScoped
public class MarcaBean implements Serializable {

	private static final long serialVersionUID = 2806365279342807551L;

	@PersistenceContext
	private EntityManager em;

	private Marca marca;
	private List<Marca> marcas;
	private boolean continuarInserindo;

	@PostConstruct
	public void init(){
		marca = new Marca();
	}

	@Transactional
	public void salvar() {
		em.persist(marca);
	}

	public List<Marca> getMarcas() {
		if (marcas == null) {
			marcas = em.createQuery("select m from Marca m", Marca.class).getResultList();
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
