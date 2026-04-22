package dev.gscordeiro.facesmotors.controllers;

import java.io.Serializable;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import org.primefaces.model.LazyDataModel;

import dev.gscordeiro.facesmotors.entities.Modelo;
import dev.gscordeiro.facesmotors.persistence.QueryDataModel;


@Named
@ViewScoped
public class ModeloBean implements Serializable{

	private static final long serialVersionUID = -8606041573319607244L;

	@Inject
	EntityManager em;

	private Modelo modelo;
	private List<Modelo> modelos;

	private LazyDataModel<Modelo> lazyDataModel;

	@PostConstruct
	public void init(){
		modelo = new Modelo();
	}

	@Transactional
	public String salvar(Modelo modelo) {
		em.persist(modelo);

		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Modelo salvo com sucesso!"));

		return "listar";
	}

	public LazyDataModel<Modelo> getLazyDataModel() {
		if (lazyDataModel == null) {
			String jpql = "select m from Modelo m";
			lazyDataModel = new QueryDataModel<Modelo>(jpql, em);
		}

		return lazyDataModel;
	}

	public List<Modelo> getModelos() {
		if (modelos == null) {
			modelos = em.createQuery("select m from Modelo m", Modelo.class).getResultList();
		}

		return modelos;
	}

	public Modelo getModelo() {
		return modelo;
	}

	public void setModelo(Modelo modelo) {
		this.modelo = modelo;
	}

	public void setModelos(List<Modelo> modelos) {
		this.modelos = modelos;
	}
	
}
