package br.com.casadocodigo.jsfjpa.controllers;

import java.io.Serializable;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;

import org.primefaces.model.LazyDataModel;

import br.com.casadocodigo.jsfjpa.entities.Modelo;
import br.com.casadocodigo.jsfjpa.persistence.JpaUtil;
import br.com.casadocodigo.jsfjpa.persistence.QueryDataModel;


@Named
@ViewScoped
public class ModeloBean implements Serializable{

	private static final long serialVersionUID = -8606041573319607244L;

	private Modelo modelo;
	private List<Modelo> modelos;

	private LazyDataModel<Modelo> lazyDataModel;

	@PostConstruct
	public void init(){
		modelo = new Modelo();
	}
	
	
	public String salvar(Modelo modelo) {
		EntityManager em = JpaUtil.getEntityManager();
		em.persist(modelo);
		
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Modelo salvo com sucesso!"));
		
		return "listar";
	}

	public LazyDataModel<Modelo> getLazyDataModel() {
		if (lazyDataModel == null) {
			String jpql = "select m from Modelo m";
			lazyDataModel = new QueryDataModel<Modelo>(jpql);
		}

		return lazyDataModel;
	}
	
	public List<Modelo> getModelos() {
		if (modelos == null) {
			modelos = JpaUtil.getEntityManager().createQuery("select m from Modelo m", Modelo.class).getResultList();
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
